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

import org.apache.geronimo.genesis.MojoSupport;

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
     * The list of legal files under META-INF we look for.
     */
    private static final String[] LEGAL_FILES = {
        "LICENSE.txt",
        "LICENSE",
        "NOTICE.txt",
        "NOTICE",
        "DISCLAIMER.txt",
        "DISCLAIMER"
    };

    /**
     * When set to true, fail the build when no legal files are found.
     *
     * @parameter default-value="false"
     */
    private boolean strict;

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

            log.info("Verifying legal files: " + file.getName());
            
            try {
                ZipFile zfile = new ZipFile(file);
                if (!containsLegalFiles(zfile)) {
                    if (strict) {
                        throw new MojoExecutionException("Artifact does not contain any legal files: " + file);
                    }
                    else {
                        log.warn("Artifact does not contain any legal files: " + file);
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

        for (int i=0; i < LEGAL_FILES.length; i++) {
            ZipEntry entry = file.getEntry("META-INF/" + LEGAL_FILES[i]);
            if (entry != null) {
                // found one, thats all we need
                return true;
            }
        }

        return false;
    }
}
