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

class InstallDependencies
{
    def homedir = new File(System.getProperty("user.home"))
    
    def m2RepoCache = new File(homedir, ".m2/repository")
    
    def basedir = new File(System.getenv("BASEDIR").getCanonicalFile()
    
    def depsdir = new File(basedir, "dependencies")
    
    def ant = new AntBuilder()
    
    def cleanCachedArtifacts(depdir) {
        println "Cleaning cached artifacts..."
        def scanner = ant.fileScanner {
            fileset(dir: depdir) {
                include(name: "**")
            }
        }
        
        // Strip off the base director name from all files so we can
        // re-root them to ~/.m2/repository
        
        def l = depdir.path.length() + 1
        scanner.each { file->
            def basepath = file.getPath()[l .. -1]
            def dir = new File(m2RepoCache, basepath).getParentFile()
            if (dir.exists()) {
                ant.delete(dir: dir)
            }
        }
    }
    
    def installArtifacts(depdir) {
        println "Installing dependencies from: ${depdir}"
        
        cleanCachedArtifacts(depdir)
        
        println "Copying new artifacts..."
        ant.copy(todir: m2RepoCache) {
            fileset(dir: depdir) {
                include(name: "**")
            }
        }
    }
    
    def main(args) {
        println "Installing dependencies..."
        
        for (depdir in depsdir.listFiles()) {
            installArtifacts(depdir)
        }
    }
}

new InstallDependencies().main(args)