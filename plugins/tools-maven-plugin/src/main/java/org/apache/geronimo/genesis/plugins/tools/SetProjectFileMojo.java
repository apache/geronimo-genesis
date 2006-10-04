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

import org.apache.geronimo.genesis.MojoSupport;
import org.apache.maven.project.MavenProject;
import org.apache.maven.artifact.Artifact;

import java.io.File;

/**
 * Helper to install a specific file (or the projects pom) as the projects artifact file.
 *
 * <p>
 * Custom packaging will need to define a artifact handler plexus component to map desired file extention.
 * </p>
 *
 * @goal set-artifact-file
 * @phase package
 *
 * @version $Rev$ $Date$
 */
public class SetProjectFileMojo
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

    /**
     * The target file to set as the project's artifact.
     * 
     * @parameter expression="${project.file}"
     * @required
     */
    private File targetFile;

    protected void doExecute() throws Exception {
        log.info("Setting artifact file: " + targetFile);
        
        Artifact artifact = project.getArtifact();
        artifact.setFile(targetFile);
    }
}