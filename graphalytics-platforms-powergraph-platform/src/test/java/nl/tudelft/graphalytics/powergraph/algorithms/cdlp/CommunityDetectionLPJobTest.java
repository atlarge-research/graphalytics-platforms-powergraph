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
package nl.tudelft.graphalytics.powergraph.algorithms.cdlp;

import java.io.File;

import nl.tudelft.graphalytics.domain.algorithms.CommunityDetectionLPParameters;
import nl.tudelft.graphalytics.powergraph.Utils;
import nl.tudelft.graphalytics.powergraph.algorithms.cd.CommunityDetectionJob;
import nl.tudelft.graphalytics.validation.GraphStructure;
import nl.tudelft.graphalytics.validation.algorithms.cdlp.CommunityDetectionLPOutput;
import nl.tudelft.graphalytics.validation.algorithms.cdlp.CommunityDetectionLPValidationTest;

/**
 * Validation tests for the community detection implementation in PowerGraph.
 *
 * @author Stijn Heldens
 */
public class CommunityDetectionLPJobTest extends CommunityDetectionLPValidationTest {

	@Override
	public CommunityDetectionLPOutput executeDirectedCommunityDetection(GraphStructure graph,
			CommunityDetectionLPParameters parameters) throws Exception {
		return execute(graph, parameters, true);
	}

	@Override
	public CommunityDetectionLPOutput executeUndirectedCommunityDetection(GraphStructure graph,
			CommunityDetectionLPParameters parameters) throws Exception {
		return execute(graph, parameters, false);
	}
	
	private CommunityDetectionLPOutput execute(GraphStructure graph, CommunityDetectionLPParameters parameters,
			boolean directed) throws Exception {
		File edgesFile = File.createTempFile("edges.", ".txt");
		File verticesFile = File.createTempFile("vertices.", ".txt");
		File outputFile = File.createTempFile("output.", ".txt");

		Utils.writeEdgeToFile(graph, directed, edgesFile);
		Utils.writeVerticesToFile(graph, verticesFile);

		String jobId = "RandomJobId";

		CommunityDetectionJob job = new CommunityDetectionJob(
				Utils.loadConfiguration(), verticesFile.getAbsolutePath(), edgesFile.getAbsolutePath(), directed, parameters, jobId);
		job.setOutputFile(outputFile);
		job.run();
		
		return new CommunityDetectionLPOutput(Utils.readResults(outputFile, Long.class));
	}
}
