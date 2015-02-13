/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tinkerpop.gremlin.process.graph.traversal;

import org.apache.tinkerpop.gremlin.process.graph.traversal.step.sideEffect.StartStep;
import org.apache.tinkerpop.gremlin.structure.Property;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;

import java.util.Map;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public interface VertexPropertyTraversal extends ElementTraversal<VertexProperty> {

    @Override
    default GraphTraversal<VertexProperty, VertexProperty> start() {
        final GraphTraversal.Admin<VertexProperty, VertexProperty> traversal = new DefaultGraphTraversal<>(this);
        return traversal.addStep(new StartStep<>(traversal, this));
    }

    @Override
    public default <E2> GraphTraversal<VertexProperty, Property<E2>> properties(final String... propertyKeys) {
        return (GraphTraversal) this.start().properties(propertyKeys);
    }

    public default <E2> GraphTraversal<VertexProperty, Map<String, Property<E2>>> propertyMap(final String... propertyKeys) {
        return this.start().propertyMap(propertyKeys);
    }

    public default <E2> GraphTraversal<VertexProperty, Map<String, E2>> valueMap(final String... propertyKeys) {
        return this.start().valueMap(propertyKeys);
    }

    public default <E2> GraphTraversal<VertexProperty, Map<String, E2>> valueMap(final boolean includeTokens, final String... propertyKeys) {
        return this.start().valueMap(includeTokens, propertyKeys);
    }
}
