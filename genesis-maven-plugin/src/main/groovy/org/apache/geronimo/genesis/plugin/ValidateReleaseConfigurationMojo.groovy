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
 * Validate the relese configuration.
 *
 * @goal validate-release-configuration
 * @phase validate
 * @since 2.0
 *
 * @version $Id$
 */
class ValidateReleaseConfigurationMojo
    extends GroovyMojo
{
    /**
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    MavenProject project
    
    void execute() {
        // Optionally prevent non-staged releases for projects
        if (project.properties['release.stageRequired']) {
            if (project.properties['release'] != 'stage') {
                fail('Release requires staging; use -Drelease=stage')
            }
        }
        
        // Make sure that we have a configured GPG passphrase
        def phrase = project.properties['gpg.passphrase']
        if (phrase == null || phrase.trim() == '') {
            fail('Missing required property: gpg.passphrase')
        }
    }
}
