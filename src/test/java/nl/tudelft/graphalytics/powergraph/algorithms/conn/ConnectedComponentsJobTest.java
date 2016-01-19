/*
 * Copyright 2015 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.tudelft.graphalytics.powergraph.algorithms.conn;

import nl.tudelft.graphalytics.validation.GraphStructure;
import nl.tudelft.graphalytics.validation.conn.ConnectedComponentsOutput;
import nl.tudelft.graphalytics.validation.conn.ConnectedComponentsValidationTest;

/**
 * Validation tests for the connected components implementation in PowerGraph.
 *
 * @author Stijn Heldens
 */
public class ConnectedComponentsJobTest extends ConnectedComponentsValidationTest {

	@Override
	public ConnectedComponentsOutput executeDirectedConnectedComponents(GraphStructure graph) throws Exception {
		return execute(graph, true);
	}

	@Override
	public ConnectedComponentsOutput executeUndirectedConnectedComponents(GraphStructure graph) throws Exception {
		return execute(graph, false);
	}
	
	private ConnectedComponentsOutput execute(GraphStructure graph, boolean directed) throws Exception {
		return null;
	}
}
