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

package genesis.buildharness

import genesis.buildharness.properties.*

/**
 * ???
 */
class BuildHarness
    extends BuildComponent
{
    def actions = new ListPropertyBuilder(name: 'actions', required: true).get()
    
    def createAction(name) {
        def action
        
        //
        // HACK: Not sure the best way to do this with Groovy, Class.forName()
        //       will not work here
        //
        
        name = name.trim()
        
        switch (name) {
            case "setup":
                action = new genesis.buildharness.action.setup()
                break
            
            case "build":
                action = new genesis.buildharness.action.build()
                break
            
            case "verify":
                action = new genesis.buildharness.action.verify()
                break
            
            default:
                throw new Error("Unknown action: ${name}")
        }
        
        log.info("Using action: ${action}")

        return action
    }
    
    def execute(name) {
        log.info("Executing action: ${name}")
        
        def action = createAction(name)
        
        action.execute()
    }
    
    def run() {
        log.info("Loading actions...")
        
        def list = []
        
        this.actions.each {
            list.add(createAction(it))
        }
        
        log.info("Executing actions...")
        
        list.each {
            it.execute()
        }
    }
}
