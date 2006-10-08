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

package org.apache.geronimo.genesis.dependency;

import java.util.Map;
import java.util.Collections;

import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.resolver.ArtifactCollector;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.ArtifactRepositoryFactory;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.project.artifact.InvalidDependencyVersionException;

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;

/**
 * ???
 *
 * @version $Rev$ $Date$
 */
public class DependencyHelper
    implements Contextualizable
{
    private ArtifactRepositoryFactory artifactRepositoryFactory = null;

    private ArtifactMetadataSource artifactMetadataSource = null;

    private ArtifactCollector artifactCollector = null;

    private ArtifactFactory artifactFactory = null;

    private ArtifactResolver artifactResolver = null;

    private PlexusContainer container;

    //
    // TODO: Figure out how to get ${localRepository} injected so we don't need it passed in.
    //

    /**
     * ???
     *
     * @param project       The maven project
     * @param repository    The local maven repository
     * @return
     * 
     * @throws ProjectBuildingException
     * @throws ArtifactResolutionException
     * @throws InvalidDependencyVersionException
     */
    public DependencyResolutionListener resolveProject(final MavenProject project, final ArtifactRepository repository)
        throws ProjectBuildingException, ArtifactResolutionException, InvalidDependencyVersionException
    {
        assert project != null;
        assert repository != null;
        
        Map managedVersions = Dependencies.getManagedVersionMap(project, artifactFactory);
        DependencyResolutionListener listener = new DependencyResolutionListener();

        if (project.getDependencyArtifacts() == null) {
            project.setDependencyArtifacts(project.createArtifacts(artifactFactory, null, null));
        }

        artifactCollector.collect(
                project.getDependencyArtifacts(),
                project.getArtifact(),
                managedVersions,
                repository,
                project.getRemoteArtifactRepositories(),
                artifactMetadataSource,
                null,
                Collections.singletonList(listener));

        return listener;
    }

    //
    // Contextualizable
    //

    public void contextualize(final Context context) throws ContextException {
        container = (PlexusContainer) context.get(PlexusConstants.PLEXUS_KEY);
    }

    //
    // Component Access
    //
    
    public ArtifactResolver getArtifactResolver() {
        return artifactResolver;
    }

    public ArtifactRepositoryFactory getArtifactRepositoryFactory() {
        return artifactRepositoryFactory;
    }

    public ArtifactMetadataSource getArtifactMetadataSource() {
        return artifactMetadataSource;
    }

    public ArtifactCollector getArtifactCollector() {
        return artifactCollector;
    }

    public ArtifactFactory getArtifactFactory() {
        return artifactFactory;
    }
}
