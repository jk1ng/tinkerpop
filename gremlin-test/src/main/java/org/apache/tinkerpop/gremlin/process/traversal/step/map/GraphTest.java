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

package org.apache.tinkerpop.gremlin.process.traversal.step.map;

import org.apache.tinkerpop.gremlin.LoadGraphWith;
import org.apache.tinkerpop.gremlin.process.AbstractGremlinProcessTest;
import org.apache.tinkerpop.gremlin.process.GremlinProcessRunner;
import org.apache.tinkerpop.gremlin.process.IgnoreEngine;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.Traversal;
import org.apache.tinkerpop.gremlin.process.traversal.TraversalEngine;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;

import static org.apache.tinkerpop.gremlin.LoadGraphWith.GraphData.GRATEFUL;
import static org.apache.tinkerpop.gremlin.LoadGraphWith.GraphData.MODERN;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
@RunWith(GremlinProcessRunner.class)
public abstract class GraphTest extends AbstractGremlinProcessTest {

    public abstract Traversal<Vertex, String> get_g_VX1X_V_valuesXnameX(final Object v1Id);

    public abstract Traversal<Vertex, String> get_g_V_hasXname_GarciaX_inXsungByX_asXsongX_V_hasXname_Willie_DixonX_inXwrittenByX_whereXeqXsongXX_name();

    @Test
    @LoadGraphWith(MODERN)
    public void g_VX1X_V_valuesXnameX() {
        final Traversal<Vertex, String> traversal = get_g_VX1X_V_valuesXnameX(convertToVertexId(graph, "marko"));
        printTraversalForm(traversal);
        checkResults(Arrays.asList("marko", "vadas", "lop", "josh", "ripple", "peter"), traversal);
    }

    @Test
    @LoadGraphWith(GRATEFUL)
    @IgnoreEngine(TraversalEngine.Type.COMPUTER)
    public void g_V_hasXname_GarciaX_inXsungByX_asXsongX_V_hasXname_Willie_DixonX_inXwrittenByX_whereXeqXsongXX_name() {
        final Traversal<Vertex, String> traversal = get_g_V_hasXname_GarciaX_inXsungByX_asXsongX_V_hasXname_Willie_DixonX_inXwrittenByX_whereXeqXsongXX_name();
        printTraversalForm(traversal);
        checkResults(Arrays.asList("MY BABE", "HOOCHIE COOCHIE MAN"), traversal);
    }

    public static class Traversals extends GraphTest {

        @Override
        public Traversal<Vertex, String> get_g_VX1X_V_valuesXnameX(final Object v1Id) {
            return g.V(v1Id).V().values("name");
        }

        @Override
        public Traversal<Vertex, String> get_g_V_hasXname_GarciaX_inXsungByX_asXsongX_V_hasXname_Willie_DixonX_inXwrittenByX_whereXeqXsongXX_name() {
            return g.V().has("artist", "name", "Garcia").in("sungBy").as("song")
                    .V().has("artist", "name", "Willie_Dixon").in("writtenBy").where(P.eq("song")).values("name");
        }
    }
}