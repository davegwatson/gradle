/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.api.internal.artifacts.ivyservice.resolveengine.artifact;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import org.gradle.api.Transformer;
import org.gradle.api.artifacts.component.ComponentIdentifier;
import org.gradle.api.attributes.HasAttributes;
import org.gradle.api.specs.Spec;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Maps.newLinkedHashMap;
import static com.google.common.collect.Sets.newLinkedHashSet;

public class DefaultResolvedArtifactResults implements VisitedArtifactsResults {
    private final List<ArtifactSet> artifactSets;
    private final Set<Long> buildableArtifacts;
    private final List<Long> sortedNodeIds;

    private final boolean consumerFirst = true;

    public DefaultResolvedArtifactResults(List<ArtifactSet> artifactSets, Set<Long> buildableArtifacts, List<Long> sortedNodeIds) {
        this.artifactSets = artifactSets;
        this.buildableArtifacts = buildableArtifacts;
        this.sortedNodeIds = sortedNodeIds;
    }

    @Override
    public SelectedArtifactResults select(Spec<? super ComponentIdentifier> componentFilter, Transformer<HasAttributes, Collection<? extends HasAttributes>> selector) {
        Set<ResolvedArtifactSet> allArtifactSets = newLinkedHashSet();
        final Map<Long, ResolvedArtifactSet> resolvedArtifactsById = newLinkedHashMap();

        for (ArtifactSet artifactSet : getArtifactSetsInOrder(consumerFirst)) {
            if (resolvedArtifactsById.containsKey(artifactSet.getId())) {
                continue;
            }

            if (!componentFilter.isSatisfiedBy(artifactSet.getComponentIdentifier())) {
                resolvedArtifactsById.put(artifactSet.getId(), null);
                continue;
            }
            Set<? extends ResolvedVariant> variants = artifactSet.getVariants();
            ResolvedVariant selected = (ResolvedVariant) selector.transform(variants);
            ResolvedArtifactSet resolvedArtifacts;
            if (selected == null) {
                resolvedArtifacts = ResolvedArtifactSet.EMPTY;
            } else {
                resolvedArtifacts = selected.getArtifacts();
                if (!buildableArtifacts.contains(artifactSet.getId())) {
                    resolvedArtifacts = NoBuildDependenciesArtifactSet.of(resolvedArtifacts);
                }
                allArtifactSets.add(resolvedArtifacts);
            }
            resolvedArtifactsById.put(artifactSet.getId(), resolvedArtifacts);
        }

        return new DefaultSelectedArtifactResults(CompositeArtifactSet.of(allArtifactSets), resolvedArtifactsById);
    }

    private Iterable<ArtifactSet> getArtifactSetsInOrder(boolean consumerFirst) {
        if (consumerFirst) {
            // Build a map to allow easy access to artifacts per node
            Multimap<Long, ArtifactSet> artifactSetsByNodeId = ArrayListMultimap.create();
            for (ArtifactSet artifactSet : artifactSets) {
                artifactSetsByNodeId.put(artifactSet.getNodeId(), artifactSet);
            }

            // Build list of artifacts based on sorted node list
            List<ArtifactSet> sorted = Lists.newArrayList();
            for (Long nodeId : Lists.reverse(sortedNodeIds)) {
                Collection<ArtifactSet> artifactSetsForNode = artifactSetsByNodeId.get(nodeId);
                sorted.addAll(artifactSetsForNode);
            }
            return sorted;
        }
        return artifactSets;
    }

    private static class DefaultSelectedArtifactResults implements SelectedArtifactResults {
        private final ResolvedArtifactSet allArtifacts;
        private final Map<Long, ResolvedArtifactSet> resolvedArtifactsById;

        DefaultSelectedArtifactResults(ResolvedArtifactSet allArtifacts, Map<Long, ResolvedArtifactSet> resolvedArtifactsById) {
            this.allArtifacts = allArtifacts;
            this.resolvedArtifactsById = resolvedArtifactsById;
        }

        @Override
        public ResolvedArtifactSet getArtifacts() {
            return allArtifacts;
        }

        @Override
        public ResolvedArtifactSet getArtifacts(long id) {
            return resolvedArtifactsById.get(id);
        }
    }
}
