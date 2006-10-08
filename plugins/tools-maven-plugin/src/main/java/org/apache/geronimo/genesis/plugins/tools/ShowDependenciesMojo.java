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

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactCollector;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.project.artifact.InvalidDependencyVersionException;

import org.apache.geronimo.genesis.MojoSupport;
import org.apache.geronimo.genesis.Dependencies;
import org.apache.geronimo.genesis.DependencyResolutionListener;

/**
 * Helper to show a projects dependencies.
 *
 * @goal show-dependencies
 * 
 * @version $Rev$ $Date$
 */
public class ShowDependenciesMojo
    extends MojoSupport
{
    /**
     * Enable verbose details (version and scope).
     * 
     * @parameter expression="${verbose}"
     */
    private boolean verbose = false;

    protected void doExecute() throws Exception {
        DependencyResolutionListener listener = resolveProject();
        printDependencyListing(listener.getRootNode(), "");
    }

    private void printDependencyListing(DependencyResolutionListener.Node node, final String pad) {
        Artifact artifact = node.getArtifact();
        String id = artifact.getDependencyConflictId();

        StringBuffer buff = new StringBuffer(id);
        if (verbose) {
            buff.append(" ");
            buff.append("{ version=").append(artifact.getVersion());
            buff.append(", scope=").append(artifact.getScope());
            buff.append(" }");
        }
        log.info(pad + buff);

        if (!node.getChildren().isEmpty()) {
            for (Iterator deps = node.getChildren().iterator(); deps.hasNext();) {
                DependencyResolutionListener.Node dep = (DependencyResolutionListener.Node) deps.next();
                printDependencyListing(dep, pad + "    ");
            }
        }
    }

    private DependencyResolutionListener resolveProject()
        throws ProjectBuildingException, ArtifactResolutionException, InvalidDependencyVersionException
    {
        Map managedVersions = Dependencies.getManagedVersionMap(project, artifactFactory);
        DependencyResolutionListener listener = new DependencyResolutionListener();

        if (project.getDependencyArtifacts() == null) {
            project.setDependencyArtifacts(project.createArtifacts(artifactFactory, null, null));
        }
        
        artifactCollector.collect(
                project.getDependencyArtifacts(),
                project.getArtifact(),
                managedVersions,
                localRepository,
                project.getRemoteArtifactRepositories(),
                artifactMetadataSource,
                null,
                Collections.singletonList(listener));

        return listener;
    }

    /**
     * @parameter
     * @component
     * @required
     */
    private ArtifactMetadataSource artifactMetadataSource = null;

    /**
     * ???
     *
     * @component
     */
    private ArtifactCollector artifactCollector = null;

    /**
     * Local Repository.
     *
     * @parameter expression="${localRepository}"
     * @required
     * @readonly
     */
    protected ArtifactRepository localRepository;

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
    protected MavenProject project;

    protected MavenProject getProject() {
        return project;
    }

    /**
     * @component
     * @required
     * @readonly
     */
    private ArtifactFactory artifactFactory = null;

    protected ArtifactFactory getArtifactFactory() {
        return artifactFactory;
    }

    /**
     * @component
     * @required
     * @readonly
     */
    private ArtifactResolver artifactResolver = null;

    protected ArtifactResolver getArtifactResolver() {
        return artifactResolver;
    }

    /**
     * @parameter expression="${localRepository}"
     * @readonly
     * @required
     */
    protected ArtifactRepository artifactRepository = null;

    protected ArtifactRepository getArtifactRepository() {
        return artifactRepository;
    }
}
