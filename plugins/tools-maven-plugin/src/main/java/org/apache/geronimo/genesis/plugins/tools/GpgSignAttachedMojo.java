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

import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

import java.io.File;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.commons.lang.SystemUtils;
import org.apache.geronimo.plugin.MojoSupport;

import org.codehaus.plexus.util.cli.Commandline;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.DefaultConsumer;
import org.codehaus.plexus.util.cli.CommandLineException;

/**
 * Sign project attached artifacts with GnuPG.
 *
 * @goal gpg-sign-attached
 * @phase verify
 *
 * @version $Rev$ $Date$
 */
public class GpgSignAttachedMojo
    extends MojoSupport
{
    //
    // TODO: Pull the passphrase from settings
    //
    
    /**
     * The passphrase to use when signing.
     *
     * @parameter expression="${passphrase}"
     * @required
     */
    private String passphrase = null;

    /**
     * The maven project.
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    protected MavenProject project = null;

    /**
     * Maven ProjectHelper
     *
     * @component
     * @readonly
     */
    private MavenProjectHelper projectHelper = null;

    //
    // Mojo
    //

    protected void doExecute() throws Exception {
        List artifacts = new ArrayList();
        artifacts.add(project.getArtifact());
        artifacts.addAll(project.getAttachedArtifacts());

        if (log.isDebugEnabled()) {
            log.info("Artifacts to be signed: " + artifacts);
        }

        // Sign attached artifacts
        Iterator iter = artifacts.iterator();
        while (iter.hasNext()) {
            Artifact artifact = (Artifact)iter.next();
            File file = artifact.getFile();

            if (file == null) {
                log.info("No file to sign for artifact: " + artifact);
                continue;
            }

            File signature = sign(file);
            projectHelper.attachArtifact(project, artifact.getType() + ".asc", signature);
        }
    }

    private File sign(final File file) throws Exception {
        assert file != null;

        log.info("Signing artifact file: " + file);

        File signature = new File(file.getCanonicalPath() + ".asc");
        log.debug("Signature file: " + signature);

        if (signature.exists()) {
            log.debug("Signature file already exists, removing: " + signature);
            signature.delete();
        }

        Commandline cmd = new Commandline();
        cmd.setExecutable("gpg" + (SystemUtils.IS_OS_WINDOWS ? ".exe" : ""));

        cmd.createArgument().setValue("--passphrase-fd");
        cmd.createArgument().setValue("0");
        cmd.createArgument().setValue("--armor");
        cmd.createArgument().setValue("--detach-sign");
        cmd.createArgument().setFile(file);

        if (log.isDebugEnabled()) {
            log.debug(Commandline.toString(cmd.getCommandline()));
        }

        // Prepare the input stream which will be used to pass the passphrase to the executable
        InputStream in = new ByteArrayInputStream(passphrase.getBytes());

        try {
            int exitCode = CommandLineUtils.executeCommandLine(cmd, in, new DefaultConsumer(), new DefaultConsumer());

            if (exitCode != 0) {
                throw new MojoExecutionException("Exit code: " + exitCode);
            }
        }
        catch (CommandLineException e) {
            throw new MojoExecutionException("Unable to execute java command", e);
        }

        return signature;
    }
}
