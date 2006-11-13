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

import org.apache.geronimo.genesis.ant.MavenAntLoggerAdapter

/**
 * ???
 */
class BuildContext
    extends BuildComponent
{
    def source
    def project
    def ant = new AntBuilder()
    
    public BuildContext(source) {
        assert source != null
        
        this.source = source
        
        // Extract components from the source script class
        this.project = source.project
        
        // Drop the default listener and replace with a maven log adapter
        def p = ant.antProject
        
        p.getBuildListeners().each {
            p.removeBuildListener(it)
        }
        
        def adapter = new MavenAntLoggerAdapter(source.log)
        adapter.setEmacsMode(true)
        if (source.log.isDebugEnabled()) {
            adapter.setMessageOutputLevel(p.MSG_VERBOSE);
        }
        else {
            adapter.setMessageOutputLevel(p.MSG_INFO);
        }
        p.addBuildListener(adapter)
    }
    
    //
    // Thread access
    //
    
    private static ThreadLocal ctxHolder = new ThreadLocal()
    
    static def setContext(ctx) {
        assert ctx != null
        
        ctxHolder.set(ctx)
    }
    
    static def unsetContext() {
        ctxHolder.set(null)
    }
    
    static def getContext() {
        def ctx = ctxHolder.get()
        assert ctx != null
        return ctx
    }
    
    //
    // Project access
    //
    
    // Can't use getProperty() as that messes up the GroovyObject
    def get(name) {
        assert name != null
        
        def value = project.properties.getProperty(name)
        
        log.debug("Get property: ${name}=${value}")
        
        return value
    }
    
    def get(name, defaultValue) {
        def value = get(name)
        
        if (value == null) {
            value = defaultValue
        }
        
        return value
    }
    
    def getBoolean(name, defaultValue) {
        def value = get(name, defaultValue)
        return Boolean.valueOf("${value}");
    }
    
    def getInteger(name, defaultValue) {
        def value = get(name, defaultValue)
        return Integer.parseInt("${value}");
    }
    
    def require(name) {
        assert name != null
        
        log.debug("Require property: ${name}")
        
        //
        // NOTE: Need to check project and system properties, as when setting -Dprop=foo
        //       on the command-line m2 will set System properties not project properties.
        //
        if (!project.properties.containsKey(name) && !System.properties.containsKey(name)) {
            throw new Exception("Missing required property: ${name}")
        }
        
        //
        // NOTE: Use getProperty() so that defaults (system properties) will get applied
        //       for some reason properties[name] does not resolve defaults :-(
        //
        return get(name)
    }
    
    def requireDirectory(name) {
        def dir = require(name)
        ensureDirectory(dir)
        return dir
    }
    
    def ensureDirectory(dirname) {
        assert dirname != null
        
        def dir = new File(dirname)
        if (!dir.exists()) {
            throw new Exception("Required directory does not exist: ${dir}")
        }
        
        if (!dir.isDirectory()) {
            throw new Exception("File exists but directory was expected: ${dir}")
        }
    }
}
