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
class verify
    extends ActionSupport
{
    def outputDir = new FilePropertyBuilder(name: 'output.dir', required: true).get()
    
    def execute() {
        log.info("Checking for output files in: ${outputDir}")
        
        def scanner = ant.fileScanner {
            fileset(dir: outputDir) {
                include(name: "**")
            }
        }
        
        def files = []
        scanner.each {
            files.add(it)
        }
        
        if (files.size() == 0) {
            throw new BuildHarnessException("Build produced no output files")
        }
        
        log.info("Discovered output files:")
        files.each {
            log.info("    ${it}")
        }
        
        assert found
    }
}
