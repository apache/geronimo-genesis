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
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.project.MavenProject;

import org.apache.geronimo.genesis.MojoSupport;
import org.apache.geronimo.genesis.util.ArtifactItem;
import org.apache.geronimo.genesis.util.ExpressionParser;

/**
 * Executes a <a href="http://groovy.codehaus.org">Groovy</a> script.
 *
 * @goal groovy
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

    //
    // TODO: Make this a scriptpath
    //
    
    /**
     * @parameter expression="${basedir}/src/main/script"
     */
    private File scriptDirectory = null;

    //
    // Maven components
    //
    
    /**
     * @parameter expression="${project}"
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
        source.validate();

        ClassLoader parent = getClass().getClassLoader();
        List urls = new ArrayList();

        // Add the plugins dependencies
        List classpathFiles = project.getCompileClasspathElements();
        for (int i = 0; i < classpathFiles.size(); ++i) {
            urls.add(new File((String)classpathFiles.get(i)).toURL());
        }

        // Add custom dependencies
        if (classpath != null) {
            for (int i=0; i < classpath.length; i++) {
                Artifact artifact = getArtifact(classpath[i]);
                urls.add(artifact.getFile().toURL());
            }
        }

        URL[] _urls = (URL[])urls.toArray(new URL[urls.size()]);
        if (log.isDebugEnabled()) {
            for (int i=0; i < _urls.length; i++) {
                log.debug("URL[" + i + "]: " + _urls[i]);
            }
        }

        //
        // TODO: Investigate using GroovyScript instead of this...
        //

        URLClassLoader cl = new URLClassLoader(_urls, parent);
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

                File file = new File(scriptDirectory, resource);
                if (file.exists()) {
                    return file.toURL();
                }
                else {
                    return null;
                }
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
            log.debug("Loading source from: " + url);
            
            InputStream input = url.openConnection().getInputStream();
            groovyClass = loader.parseClass(input);
            input.close();
        }
        
        GroovyObject groovyObject = (GroovyObject)groovyClass.newInstance();

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

        groovyObject.invokeMethod("run", new Object[0]);
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
