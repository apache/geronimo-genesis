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

import org.codehaus.mojo.pluginsupport.MojoSupport;

import org.apache.maven.project.MavenProject;

import java.util.Iterator;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

/**
 * Helper to show all properties.
 *
 * @goal show-properties
 *
 * @version $Rev$ $Date$
 */
public class ShowPropertiesMojo
    extends MojoSupport
{
    /**
     * The maven project.
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    protected MavenProject project;

    protected MavenProject getProject() {
        return project;
    }
    
    protected void doExecute() throws Exception {
        showProjectProperties();
        showSystemProperties();
    }

    private void logProperties(final Map props) {
        List list = new ArrayList();
        list.addAll(props.keySet());
        Collections.sort(list);
        
        Iterator iter = list.iterator();
        while (iter.hasNext()) {
            String name = (String)iter.next();
            String value = String.valueOf(props.get(name));
            log.info("    " + name + "=" + value);
        }
    }
    
    private void showProjectProperties() {
        log.info("Project properties:");

        Map props = getProject().getProperties();
        logProperties(props);
    }

    private void showSystemProperties() {
        log.info("System properties:");

        Map props = System.getProperties();
        logProperties(props);
    }
}
