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

package org.apache.geronimo.genesis;

import java.io.File;
import java.io.PrintStream;

import java.util.Map;
import java.util.Iterator;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.BuildLogger;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.taskdefs.Mkdir;
import org.apache.tools.ant.taskdefs.Property;

import org.apache.geronimo.plugin.MojoSupport;

/**
 * Support for Ant-based Mojos.
 *
 * @version $Rev$ $Date$
 */
public abstract class AntMojoSupport
    extends MojoSupport
{
    protected Project ant;

    protected void init() {
        super.init();

        ant = new Project();
        ant.setBaseDir(getProject().getBasedir());
        
        initAntLogger(ant);

        ant.init();

        // Inherit properties from Maven
        inheritProperties();
    }
    
    protected void initAntLogger(final Project ant) {
        AntLoggerAdapter antLogger = new AntLoggerAdapter(log);
        antLogger.setEmacsMode(true);
        antLogger.setOutputPrintStream(System.out);
        antLogger.setErrorPrintStream(System.err);
        
        if (log.isDebugEnabled()) {
            antLogger.setMessageOutputLevel(Project.MSG_VERBOSE);
        }
        else {
            antLogger.setMessageOutputLevel(Project.MSG_INFO);
        }
        
        ant.addBuildListener(antLogger);
    }
    
    protected void setProperty(final String name, Object value) {
        assert name != null;
        assert value != null;

        String valueAsString = String.valueOf(value);

        if (log.isDebugEnabled()) {
            log.debug("Setting property: " + name + "=" + valueAsString);
        }

        Property prop = (Property)createTask("property");
        prop.setName(name);
        prop.setValue(valueAsString);
        prop.execute();
    }

    protected void inheritProperties() {
        // Propagate properties
        Map props = getProject().getProperties();
        Iterator iter = props.keySet().iterator();
        while (iter.hasNext()) {
            String name = (String)iter.next();
            String value = String.valueOf(props.get(name));
            setProperty(name, value);
        }

        // Hardcode a few
        setProperty("pom.basedir", getProject().getBasedir());
    }

    protected FileSet createFileSet() {
        FileSet set = new FileSet();
        set.setProject(ant);
        return set;
    }

    protected Task createTask(final String name) throws BuildException {
        assert name != null;

        return ant.createTask(name);
    }

    protected void mkdir(final File dir) {
        assert dir != null;

        Mkdir mkdir = (Mkdir)createTask("mkdir");
        mkdir.setDir(dir);
        mkdir.execute();
    }
}