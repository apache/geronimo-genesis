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

//
// $Rev$ $Date$
//

package genesis.buildharness.action

import org.apache.commons.lang.SystemUtils

import genesis.buildharness.*
import genesis.buildharness.properties.*

/**
 * ???
 */
class build
    extends ActionSupport
{
    def pomGlob = new PropertyBuilder(name: 'pomglob', required: true).get()
    
    def outputDir = new FilePropertyBuilder(name: 'output.dir', required: true).get()
    
    def mvn = getMavenExecutable()
    
    def execute() {
        log.info("Pom Glob: ${pomGlob}")
        
        // Find all of the poms to execute
        def scanner = ant.fileScanner {
            fileset(dir: basedir) {
                include(name: pomGlob)
            }
        }
        
        def poms = []
        
        scanner.each {
            log.info("Found pom: ${it}")
            poms.add(it)
        }
        
        if (poms.size() == 0) {
            throw new BuildHarnessException("No poms matched glob: ${pomGlob}")
        }
        
        // Build each pom
        poms.each {
            maven(it)
        }
    }
    
    def maven(pom) {
        log.info("Building: ${pom}...")
        
        // Make this configurable?! only append build-harness
        def profiles = [ 'default', 'build-harness' ]
        
        try {
            ant.exec(executable: mvn, failonerror: true) {
                arg(value: "--file")
                arg(value: pom)
                
                // Make this configurable?!
                arg(value: "deploy")
                
                // Always show stack traces
                arg(value: "-e")
                
                // Enable debug maybe
                if (log.isDebugEnabled()) {
                    arg(value: "-X")
                }
                
                // Append profiles
                arg(value: "-P" + profiles.join(","))
                
                // Need to propagate a few configuration properties
                arg(value: "-Doutput.dir=${outputDir}")
            }
        }
        catch (Exception e) {
            throw new BuildHarnessException("Maven execution failed", e)
        }
    }
    
    def getMavenExecutable() {
        def path = System.getProperty("maven.home");
        if (path == null) {
            // This should really never happen
            throw new BuildHarnessException("Missing required system property: maven.home");
        }

        def home = new File(path);
        def cmd;
        
        if (SystemUtils.IS_OS_WINDOWS) {
            cmd = new File(home, "bin/mvn.bat");
        }
        else {
            cmd = new File(home, "bin/mvn");
        }

        cmd = cmd.getCanonicalFile();
        if (!cmd.exists()) {
            throw new BuildHarnessException("Maven executable not found at: ${cmd}");
        }

        return cmd;
    }
}
