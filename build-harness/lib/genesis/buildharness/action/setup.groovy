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

import genesis.buildharness.*
import genesis.buildharness.properties.*

/**
 * ???
 */
class setup
    extends ActionSupport
{
    def outputDir = new FilePropertyBuilder(name: 'output.dir', required: true).get()
    
    def execute() {
        // Install dependency artifacts into the local m2 repo
        def repos = new File(basedir, "repositories")
        
        if (repos.exists()) {
            log.info("Installing dependency artifacts from: ${repos}")
            
            for (f in repos.listFiles()) {
                log.info("Installing artifacts from: ${f}")
                
                ant.copy(todir: localRepo) {
                    fileset(dir: f) {
                        include(name: "**")
                    }
                }
            }
        }
        else {
            log.info("No dependency artifacts to be installed")
        }
        
        // Make sure the output dir is empty first
        ant.delete(dir: outputDir)
    }
}
