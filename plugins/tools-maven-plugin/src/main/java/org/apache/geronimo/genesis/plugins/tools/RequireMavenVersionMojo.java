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

package org.apache.geronimo.genesis.plugins.tools;

import java.util.Properties;
import java.io.InputStream;

import org.codehaus.mojo.pluginsupport.MojoSupport;

import org.apache.maven.plugin.MojoFailureException;

/**
 * Forces the build to fail if the version of Maven is not compatible.
 *
 * @goal require-maven-version
 * @phase validate
 *
 * @version $Rev$ $Date$
 */
public class RequireMavenVersionMojo
    extends MojoSupport
{
    /**
     * Specify the required version of Maven (2.0.4, 2.0.4).
     *
     * Can specify a suffix of '+' to allow any version equal to or newer or '*'
     * to allow versions in the same group.
     *
     * For example, version=2.0+ would be allowed with any Maven 2.1.x, version=2.0*
     * would allow any Maven 2.0.x, but not Maven 2.1.x.
     *
     * @parameter
     * @required
     */
    private String version = null;
    
    /**
     * Flag to skip the version check.
     *
     * @parameter expression="${requiremavenversion.skip}" default-value="false"
     */
    private boolean skip = false;
    
    private String loadMavenVersion() throws Exception {
        //
        // HACK: Not sure where is the best place to get the Maven version from, so pull it from
        //       the maven-core's pom details until we find a better way.
        //
        
        InputStream input = getClass().getClassLoader().getResourceAsStream("META-INF/maven/org.apache.maven/maven-core/pom.properties");
        if (input == null) {
            throw new MojoFailureException("Missing 'maven-core/pom.properties', can't find Maven version");
        }
        
        Properties props = new Properties();
        try {
            props.load(input);
        }
        finally {
            input.close();
        }
        
        String version = props.getProperty("version");
        if (version == null) {
            throw new MojoFailureException("Missing 'version' property in 'maven-core/pom.properties'");
        }
        
        return version;
    }
    
    /**
     * Parse a float from '1.2.3', '1.2.3', '1.2.3.4', etc.
     */
    private float parseFloat(final String input) {
        assert input != null;
        
        StringBuffer buff = new StringBuffer();
        boolean haveDot = false;
        for (int i=0; i<input.length(); i++) {
            char c = input.charAt(i);
            if (!haveDot) {
                buff.append(c);
                if (c == '.') {
                    haveDot = true;
                }
            }
            else {
                // have a dot
                if (c != '.') {
                    buff.append(c);
                }
            }
        }
        
        return Float.parseFloat(buff.toString());
    }
    
    protected void doExecute() throws Exception {
        if (skip) {
            log.warn("Skipping Maven version check");
        }
        
        String mavenVersion = loadMavenVersion();
        log.debug("Current Maven version: " + mavenVersion);
        float mavenVersionFloat = parseFloat(mavenVersion);
        
        version = version.trim();
        
        if (version.endsWith("*")) {
            version = version.substring(0, version.length() - 1).trim();
            
            log.debug("Checking Maven version is in the same group as: " + version);
            
            if (!mavenVersion.startsWith(version)) {
                throw new MojoFailureException("This build requires Maven version " + version + 
                    " or a greater version in the same group, found version: " + mavenVersion);
            }
        }
        else if (version.endsWith("+")) {
            version = version.substring(0, version.length() - 1).trim();
            
            log.debug("Checking Maven version is greater than: " + version);
            
            float tmp = parseFloat(version);
            
            if (tmp > mavenVersionFloat) {
                throw new MojoFailureException("This build requires Maven version " + version + 
                    " or greater, found version: " + mavenVersion);
            }
        }
        else {
            log.debug("Checking Maven version is equal to: " + version);
            
            float tmp = parseFloat(version);
            
            if (tmp != mavenVersionFloat) {
                throw new MojoFailureException("This build requires Maven version " + version + 
                    ", found version: " + mavenVersion);
            }
        }
    }
}
