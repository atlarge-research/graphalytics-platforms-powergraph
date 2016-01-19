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
package nl.tudelft.graphalytics.powergraph.algorithms.cd;

import nl.tudelft.graphalytics.domain.algorithms.CommunityDetectionParameters;
import nl.tudelft.graphalytics.validation.GraphStructure;
import nl.tudelft.graphalytics.validation.cd.CommunityDetectionOutput;
import nl.tudelft.graphalytics.validation.cd.CommunityDetectionValidationTest;

/**
 * Validation tests for the community detection implementation in PowerGraph.
 *
 * @author Stijn Heldens
 */
public class CommunityDetectionJobTest extends CommunityDetectionValidationTest {

	@Override
	public CommunityDetectionOutput executeDirectedCommunityDetection(GraphStructure graph,
			CommunityDetectionParameters parameters) throws Exception {
		return execute(graph, parameters, true);
	}

	@Override
	public CommunityDetectionOutput executeUndirectedCommunityDetection(GraphStructure graph,
			CommunityDetectionParameters parameters) throws Exception {
		return execute(graph, parameters, false);
	}
	
	private CommunityDetectionOutput execute(GraphStructure graph, CommunityDetectionParameters parameters,
			boolean directed) throws Exception {
		return null;
	}
}
