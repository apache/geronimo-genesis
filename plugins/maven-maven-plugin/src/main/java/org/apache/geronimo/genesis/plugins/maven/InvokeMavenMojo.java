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

import java.util.Map;
import java.util.Iterator;

import org.apache.geronimo.genesis.AntMojoSupport;

import org.apache.maven.project.MavenProject;
import org.apache.maven.plugin.MojoExecutionException;

import org.codehaus.plexus.util.Os;

import org.apache.tools.ant.taskdefs.ExecTask;

/**
 * Invoke Maven in a sub-process.
 *
 * @goal invoke
 *
 * @version $Rev$ $Date$
 */
public class InvokeMavenMojo
    extends AntMojoSupport
{
    /**
     * ???
     *
     * @parameter expression="${pomFile}"
     * @required
     */
    private File pomFile = null;

    /**
     * ???
     *
     * @parameter
     */
    private String[] flags = null;

    /**
     * ???
     *
     * @parameter
     */
    private Map parameters = null;

    /**
     * ???
     *
     * @parameter
     */
    private String[] goals = null;

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

    protected void doExecute() throws Exception {
        ExecTask exec = (ExecTask)createTask("exec");

        exec.setExecutable(getMavenExecutable().getAbsolutePath());
        exec.setFailIfExecutionFails(true);
        exec.setFailonerror(true);

        if (flags != null) {
            for (int i=0; i< flags.length; i++) {
                exec.createArg().setValue(flags[i]);
            }
        }

        if (parameters != null) {
            Iterator iter = parameters.keySet().iterator();
            while (iter.hasNext()) {
                String name = (String)iter.next();
                Object value = parameters.get(name);
                exec.createArg().setValue("-D" + name + "=" + value);
            }
        }

        if (goals != null) {
            for (int i=0; i< goals.length; i++) {
                exec.createArg().setValue(goals[i]);
            }
        }

        if (!pomFile.exists()) {
            throw new MojoExecutionException("Missing pom file as: " + pomFile);
        }
        else {
            exec.createArg().setValue("-f");
            exec.createArg().setFile(pomFile);
        }

        log.info("Starting Maven...");
        
        exec.execute();
    }

    private File getMavenExecutable() throws MojoExecutionException, IOException {
        String path = System.getProperty("maven.home");
        if (path == null) {
            // This should really never happen
            throw new MojoExecutionException("Missing msaven.home system property");
        }

        File home = new File(path);
        File cmd;

        if (Os.isFamily("windows")) {
            cmd = new File(home, "bin/mvn.bat");
        }
        else {
            cmd = new File(home, "bin/mvn");
        }

        cmd = cmd.getCanonicalFile();
        if (!cmd.exists()) {
            throw new MojoExecutionException("Maven executable not found at: " + cmd);
        }

        return cmd;
    }
}
