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
package nl.tudelft.graphalytics.powergraph.algorithms.pr;

import java.io.File;

import nl.tudelft.graphalytics.domain.algorithms.PageRankParameters;
import nl.tudelft.graphalytics.powergraph.Utils;
import nl.tudelft.graphalytics.powergraph.algorithms.conn.ConnectedComponentsJob;
import nl.tudelft.graphalytics.validation.GraphStructure;
import nl.tudelft.graphalytics.validation.algorithms.cd.CommunityDetectionOutput;
import nl.tudelft.graphalytics.validation.algorithms.pr.PageRankOutput;
import nl.tudelft.graphalytics.validation.algorithms.pr.PageRankValidationTest;

/**
 * Validation tests for the PageRank implementation in PowerGraph.
 *
 * @author Stijn Heldens
 */
public class PageRankJobTest extends PageRankValidationTest {

	@Override
	public PageRankOutput executeDirectedPageRank(GraphStructure graph, PageRankParameters parameters)
			throws Exception {
		return execute(graph, parameters, true);
	}

	@Override
	public PageRankOutput executeUndirectedPageRank(GraphStructure graph, PageRankParameters parameters)
			throws Exception {
		return execute(graph, parameters, false);
	}
	
	private PageRankOutput execute(GraphStructure graph, PageRankParameters parameters, boolean directed)
			throws Exception {
		File inputFile = Utils.writeGraphToFile(graph);
		File outputFile = File.createTempFile("output.", ".txt");
		
		ConnectedComponentsJob job = new ConnectedComponentsJob(inputFile.getAbsolutePath(), directed);
		job.setOutputFile(outputFile);
		job.run();
		
		return new PageRankOutput(Utils.readResults(outputFile, Double.class));
	}

}
