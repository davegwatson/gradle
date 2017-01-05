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

import org.gradle.api.artifacts.component.ComponentIdentifier;

import java.util.Set;

public interface ArtifactSet {

    ComponentIdentifier getComponentIdentifier();

    long getId();

    /**
     * The id of the node that 'owns' these artifacts.
     * Note that a single node may have multiple artifact sets, when an incoming dependency explicitly list artifacts or exclusions.
     */
    long getNodeId();

    /**
     * Take a snapshot of this set, doing whatever work is required to calculate the variants of this set.
     */
    ArtifactSet snapshot();

    Set<? extends ResolvedVariant> getVariants();
}
