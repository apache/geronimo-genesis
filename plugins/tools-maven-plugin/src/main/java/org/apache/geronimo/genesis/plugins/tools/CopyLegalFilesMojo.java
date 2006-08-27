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

import org.apache.geronimo.genesis.AntMojoSupport;
import org.apache.tools.ant.taskdefs.Copy;
import org.apache.tools.ant.types.FileSet;
import org.apache.maven.project.MavenProject;

import java.io.File;

/**
 * Copy legal files (like LICENSE.txt and NOTICE.txt) for inclusion into generated jars.
 *
 * @goal copy-legal-files
 * @phase validate
 *
 * @version $Rev$ $Date$
 */
public class CopyLegalFilesMojo
    extends AntMojoSupport
{
    /**
     * Directory to copy legal files into.
     *
     * @parameter expression="${project.build.outputDirectory}/META-INF"
     * @required
     */
    private File outputDirectory = null;

    /**
     * The basedir of the project.
     *
     * @parameter expression="${basedir}"
     * @required
     * @readonly
     */
    protected File basedir;

    //
    // MojoSupport Hooks
    //

    /**
     * The maven project.
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    protected MavenProject project = null;

    protected MavenProject getProject() {
        return project;
    }

    //
    // Mojo
    //

    protected void doExecute() throws Exception {
        // Only copy if the packaging is not pom
        if (!"pom".equals(getProject().getPackaging())) {
            mkdir(outputDirectory);

            Copy copy = (Copy)createTask("copy");
            copy.setTodir(outputDirectory);

            FileSet files = createFileSet();
            files.setDir(basedir);

            //
            // FIXME: Expose as configuration... though I'm too lazy right now, so just hardcode
            //

            files.createInclude().setName("LICENSE.txt");
            files.createInclude().setName("NOTICE.txt");
            copy.addFileset(files);

            copy.execute();
        }
    }
}
