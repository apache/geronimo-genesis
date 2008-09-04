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
 * Validate the relese=stage configuration.
 *
 * @goal validate-release-stage-configuration
 * @phase validate
 * @since 2.0
 *
 * @version $Id$
 */
class ValidateReleaseStageConfigurationMojo
    extends GroovyMojo
{
    /**
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    MavenProject project
    
    void execute() {
        // Make sure that we have a valid stage deployment URL configured,
        // and that the URL is parsable (ie. not a file reference, which won't work
        def url = project.properties['release.stageDeployUrl']
        if (url == null || url.trim() == '') {
            fail('Missing required property: release.stageDeployUrl')
        }
        try {
            new URL(url)
        }
        catch (Exception e) {
            fail("Invalid URL: $url")
        }
    }
}
