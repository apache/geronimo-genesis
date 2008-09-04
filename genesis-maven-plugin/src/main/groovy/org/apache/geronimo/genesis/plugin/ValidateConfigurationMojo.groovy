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

package org.apache.geronimo.genesis.plugin

import org.codehaus.groovy.maven.mojo.GroovyMojo

import org.apache.maven.project.MavenProject

/**
 * Validate the basic project configuration.
 *
 * @goal validate-configuration
 * @phase validate
 * @since 2.0
 *
 * @version $Id$
 */
class ValidateConfigurationMojo
    extends GroovyMojo
{
    /**
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    MavenProject project
    
    void execute() {
        if (project.groupId.startsWith('org.apache.geronimo.genesis')) {
            return
        }
        
        def failIfNotConfigured = { var, value, name ->
            if (var.startsWith(value)) {
                fail("Genesis child project must configure element: $name")
            }
        }
        
        // TODO: project/url
        
        // TODO: project/distributionManagement/site
        
        failIfNotConfigured(
            project.description,
            'Genesis provides',
            'project/description')
        
        failIfNotConfigured(
            project.scm.connection,
            'scm:svn:http://svn.apache.org/repos/asf/geronimo/genesis',
            'project/scm/connection')
        
        failIfNotConfigured(
            project.scm.developerConnection,
            'scm:svn:https://svn.apache.org/repos/asf/geronimo/genesis',
            'project/scm/developerConnection')
        
        failIfNotConfigured(
            project.scm.url,
            'http://svn.apache.org/viewvc/geronimo/geronimo/genesis',
            'project/scm/url')
    }
}
