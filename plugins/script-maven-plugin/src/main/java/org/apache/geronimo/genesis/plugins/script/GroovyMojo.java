/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.geronimo.genesis.plugins.script;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import groovy.lang.GroovyResourceLoader;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.project.MavenProject;
import org.apache.maven.plugin.MojoExecutionException;

import org.apache.geronimo.genesis.MojoSupport;
import org.apache.geronimo.genesis.util.ArtifactItem;
import org.apache.geronimo.genesis.util.ExpressionParser;

/**
 * Executes a <a href="http://groovy.codehaus.org">Groovy</a> script.
 *
 * @goal groovy
 * @configurator override
 * @requiresDependencyResolution
 *
 * @version $Rev$ $Date$
 */
public class GroovyMojo
    extends MojoSupport
{
    /**
     * The source of the script to execute.
     *
     * @parameter
     * @required
     */
    private CodeSource source = null;

    /**
     * Additional artifacts to add to the scripts classpath.
     *
     * @parameter
     */
    private ArtifactItem[] classpath = null;

    /**
     * Path to search for imported scripts.
     *
     * @parameter expression
     */
    private File[] scriptpath = null;

    //
    // TODO: Find a better name for this...
    //
    
    /**
     * @parameter
     */
    private DelayedConfiguration custom = null;

    //
    // Maven components
    //
    
    /**
     * @parameter expression="${project}"
     * @readonly
     * @required
     */
    private MavenProject project = null;

    /**
     * @parameter expression="${localRepository}"
     * @readonly
     * @required
     */
    private ArtifactRepository artifactRepository = null;

    //
    // MojoSupport Hooks
    //

    protected MavenProject getProject() {
        return project;
    }
    
    protected ArtifactRepository getArtifactRepository() {
        return artifactRepository;
    }

    //
    // Mojo
    //

    protected void doExecute() throws Exception {
        boolean debug = log.isDebugEnabled();

        source.validate();

        ClassLoader parent = getClass().getClassLoader();
        URL[] urls = getClasspath();
        URLClassLoader cl = new URLClassLoader(urls, parent);

        // Validate and dump the scriptpath
        if (scriptpath != null) {
            log.debug("Scriptpath:");
            for (int i=0; i < scriptpath.length; i++) {
                if (scriptpath[i] == null) {
                    throw new MojoExecutionException("Null element found in scriptpath at index: " + i);
                }

                if (debug) {
                    log.debug("    " + scriptpath[i]);
                }
            }
        }

        //
        // TODO: Investigate using GroovyScript instead of this...
        //

        GroovyClassLoader loader = new GroovyClassLoader(cl);
        loader.setResourceLoader(new GroovyResourceLoader()
        {
            // Allow peer scripts to be loaded
            public URL loadGroovySource(final String classname) throws MalformedURLException {
                String resource = classname.replace('.', '/');
                if (!resource.startsWith("/")) {
                    resource = "/" + resource;
                }
                resource = resource + ".groovy";

                if (scriptpath != null) {
                    for (int i=0; i<scriptpath.length; i++) {
                        assert scriptpath[i] != null;

                        File file = new File(scriptpath[i], resource);
                        if (file.exists()) {
                            return file.toURL();
                        }
                    }
                }

                return null;
            }
        });
        
        Class groovyClass;
        
        if (source.getBody() != null) {
            groovyClass = loader.parseClass(source.getBody());
        }
        else {
            URL url;
            if (source.getFile() != null) {
                url = source.getFile().toURL();
            }
            else {
                url = source.getUrl();
            }
            if (debug) {
                log.debug("Loading source from: " + url);
            }

            String fileName = new File(url.getFile()).getName();
            InputStream input = url.openConnection().getInputStream();
            groovyClass = loader.parseClass(input, fileName);
            input.close();
        }
        
        GroovyObject groovyObject = (GroovyObject)groovyClass.newInstance();

        if (custom != null) {
            //
            // TODO: Perform custom configuration processing
            //
            log.info("Applying delayed configuration: " + custom);
        }

        // Put int a helper to the script object
        groovyObject.setProperty("script", groovyObject);

        // Expose logging
        groovyObject.setProperty("log", log);

        // Create a delegate to allow getProperites() to be fully resolved
        MavenProject delegate = new MavenProject(project) {
            public Properties resolvedProperties;

            public Properties getProperties() {
                if (resolvedProperties == null) {
                    resolvedProperties = resolveProperties(project.getProperties());
                }
                return resolvedProperties;
            }
        };

        groovyObject.setProperty("project", delegate);
        groovyObject.setProperty("pom", delegate);

        // Execute the script
        if (debug) {
            log.debug("Invoking run() on: " + groovyObject);
        }
        groovyObject.invokeMethod("run", new Object[0]);
    }

    private URL[] getClasspath() throws DependencyResolutionRequiredException, MalformedURLException, MojoExecutionException {
        List list = new ArrayList();

        // Add the plugins dependencies
        List classpathFiles = project.getCompileClasspathElements();
        for (int i = 0; i < classpathFiles.size(); ++i) {
            list.add(new File((String)classpathFiles.get(i)).toURL());
        }

        // Add custom dependencies
        if (classpath != null) {
            for (int i=0; i < classpath.length; i++) {
                Artifact artifact = getArtifact(classpath[i]);
                list.add(artifact.getFile().toURL());
            }
        }

        URL[] urls = (URL[])list.toArray(new URL[list.size()]);

        // Dump the classpath
        if (log.isDebugEnabled()) {
            log.debug("Classpath:");
            for (int i=0; i < urls.length; i++) {
                log.debug("    " + urls[i]);
            }
        }

        return urls;
    }
    
    private Properties resolveProperties(final Properties source) {
        Properties props = new Properties(System.getProperties());
        
        // Setup the variables which should be used for resolution
        Map vars = new HashMap();
        vars.put("project", project);

        ExpressionParser parser = new ExpressionParser(vars);

        Iterator iter = source.keySet().iterator();
        while (iter.hasNext()) {
            String name = (String)iter.next();
            props.put(name, parser.parse(source.getProperty(name)));
        }

        return props;
    }
}
