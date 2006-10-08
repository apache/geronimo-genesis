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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.ArtifactUtils;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuildingException;

import org.apache.geronimo.genesis.dependency.DependencyResolutionListener;

//
// NOTE: Lifetd from the maven-project-info-plugin
//

/**
 * ???
 *
 * @version $Rev$ $Date$
 */
public class Dependencies
{
    private List projectDependencies;

    private DependencyResolutionListener resolvedDependencies;

    public Dependencies(MavenProject project, DependencyResolutionListener listener) {
        this.projectDependencies = listener.getRootNode().getChildren();
        this.resolvedDependencies = listener;

        //
        // Workaround to ensure proper File objects in the Artifacts from the DependencyResolutionListener
        //
        Map projectMap = new HashMap();
        Iterator it = project.getArtifacts().iterator();
        while (it.hasNext()) {
            Artifact artifact = (Artifact) it.next();
            projectMap.put(ArtifactUtils.versionlessKey(artifact), artifact);
        }

        mapArtifactFiles(listener.getRootNode(), projectMap);
    }

    public static Map getManagedVersionMap(MavenProject project, ArtifactFactory factory) throws ProjectBuildingException {
        DependencyManagement dependencyManagement = project.getDependencyManagement();
        Map managedVersionMap;

        if (dependencyManagement != null && dependencyManagement.getDependencies() != null) {
            managedVersionMap = new HashMap();
            for (Iterator i = dependencyManagement.getDependencies().iterator(); i.hasNext();) {
                Dependency d = (Dependency) i.next();

                try {
                    VersionRange versionRange = VersionRange.createFromVersionSpec(d.getVersion());
                    Artifact artifact = factory.createDependencyArtifact(
                            d.getGroupId(),
                            d.getArtifactId(),
                            versionRange,
                            d.getType(),
                            d.getClassifier(),
                            d.getScope());
                    managedVersionMap.put(d.getManagementKey(), artifact);
                }
                catch (InvalidVersionSpecificationException e) {
                    throw new ProjectBuildingException(project.getId(),
                            "Unable to parse version '" + d.getVersion() +
                            "' for dependency '" + d.getManagementKey() + "': " + e.getMessage(), e);
                }
            }
        }
        else {
            managedVersionMap = Collections.EMPTY_MAP;
        }

        return managedVersionMap;
    }

    private void mapArtifactFiles(DependencyResolutionListener.Node node, Map projectMap) {
        List childs = node.getChildren();
        if ((childs == null) || childs.isEmpty()) {
            return;
        }

        Iterator it = childs.iterator();
        while (it.hasNext()) {
            DependencyResolutionListener.Node anode = (DependencyResolutionListener.Node) it.next();
            String key = ArtifactUtils.versionlessKey(anode.getArtifact());
            Artifact projartifact = (Artifact) projectMap.get(key);
            if (projartifact != null) {
                anode.getArtifact().setFile(projartifact.getFile());
            }

            mapArtifactFiles(anode, projectMap);
        }
    }

    public boolean hasDependencies() {
        return (projectDependencies != null) && (!this.projectDependencies.isEmpty());
    }

    public List getProjectDependencies() {
        return new ArrayList(projectDependencies);
    }

    public List getTransitiveDependencies() {
        List deps = new ArrayList(resolvedDependencies.getArtifacts());
        deps.removeAll(projectDependencies);
        return deps;
    }

    public List getAllDependencies() {
        List deps = new ArrayList();

        for (Iterator it = resolvedDependencies.getArtifacts().iterator(); it.hasNext();) {
            DependencyResolutionListener.Node node = (DependencyResolutionListener.Node) it.next();
            Artifact artifact = node.getArtifact();
            deps.add(artifact);
        }
        return deps;
    }

    public Map getDependenciesByScope() {
        Map dependenciesByScope = new HashMap();
        for (Iterator i = getAllDependencies().iterator(); i.hasNext();) {
            Artifact artifact = (Artifact) i.next();

            List multiValue = (List) dependenciesByScope.get(artifact.getScope());
            if (multiValue == null) {
                multiValue = new ArrayList();
            }
            multiValue.add(artifact);
            dependenciesByScope.put(artifact.getScope(), multiValue);
        }
        return dependenciesByScope;
    }

    public DependencyResolutionListener.Node getResolvedRoot() {
        return resolvedDependencies.getRootNode();
    }
}