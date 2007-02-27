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

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.zip.ZipFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;

import org.apache.maven.project.MavenProject;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.artifact.Artifact;

import org.codehaus.mojo.pluginsupport.MojoSupport;

/**
 * Verify (kinda) that legal files are in all attached zip-encoded artifacts.
 *
 * @goal verify-legal-files
 * @phase verify
 *
 * @version $Rev$ $Date$
 */
public class VerifyLegalFilesMojo
    extends MojoSupport
{
    /**
     * The default required legal files.
     */
    private static final String[] DEFAULT_REQUIRED_FILES = {
        "LICENSE.txt",
        "NOTICE.txt"
    };

    /**
     * When set to true, fail the build when no legal files are found.
     *
     * @parameter default-value="false"
     */
    private boolean strict;


    /**
     * The list of required legal files.
     *
     * @parameter
     */
    private String[] requiredFiles = DEFAULT_REQUIRED_FILES;

    /**
     * The maven project.
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    protected MavenProject project = null;

    protected void doExecute() throws Exception {
        List artifacts = new ArrayList();
        artifacts.add(project.getArtifact());
        artifacts.addAll(project.getAttachedArtifacts());

        Iterator iter = artifacts.iterator();
        while (iter.hasNext()) {
            Artifact artifact = (Artifact)iter.next();
            File file = artifact.getFile();

            // Some artifacts might not have files, so skip them
            if (file == null) {
                log.debug("Skipping artifact; no attached file: " + artifact);
                continue;
            }

            try {
                ZipFile zfile = new ZipFile(file);

                log.info("Checking legal files in: " + file.getName());

                if (!containsLegalFiles(zfile)) {
                    String msg = "Artifact does not contain any legal files: " + file.getName();
                    if (strict) {
                        throw new MojoExecutionException(msg);
                    }
                    else {
                        log.warn(msg);
                    }
                }
            }
            catch (ZipException e) {
                log.debug("Failed to check file for legal muck; ignoring: " + file, e);
            }
        }
    }

    private boolean containsLegalFiles(final ZipFile file) throws IOException {
        assert file != null;

        return containsLegalFiles(file, "META-INF") ||
               containsLegalFiles(file, project.getArtifactId() + "-" + project.getVersion());
    }

    private boolean containsLegalFiles(final ZipFile file, final String basedir) {
        assert file != null;
        assert basedir != null;

        for (int i=0; i < requiredFiles.length; i++) {
            String filename = basedir + "/" + requiredFiles[i];
            log.debug("Checking for: " + filename);
            
            ZipEntry entry = file.getEntry(filename);
            if (entry == null) {
                return false;
            }
        }

        return true;
    }
}
