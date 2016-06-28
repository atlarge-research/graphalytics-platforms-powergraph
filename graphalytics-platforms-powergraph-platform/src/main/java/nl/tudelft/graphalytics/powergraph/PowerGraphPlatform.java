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
package nl.tudelft.graphalytics.powergraph;

import java.io.File;
import java.io.IOException;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nl.tudelft.graphalytics.Platform;
import nl.tudelft.graphalytics.PlatformExecutionException;
import nl.tudelft.graphalytics.domain.Benchmark;
import nl.tudelft.graphalytics.domain.Graph;
import nl.tudelft.graphalytics.domain.NestedConfiguration;
import nl.tudelft.graphalytics.domain.PlatformBenchmarkResult;
import nl.tudelft.graphalytics.domain.algorithms.BreadthFirstSearchParameters;
import nl.tudelft.graphalytics.domain.algorithms.CommunityDetectionLPParameters;
import nl.tudelft.graphalytics.domain.algorithms.PageRankParameters;
import nl.tudelft.graphalytics.domain.algorithms.SingleSourceShortestPathsParameters;
import nl.tudelft.graphalytics.powergraph.algorithms.bfs.BreadthFirstSearchJob;
import nl.tudelft.graphalytics.powergraph.algorithms.cd.CommunityDetectionJob;
import nl.tudelft.graphalytics.powergraph.algorithms.conn.ConnectedComponentsJob;
import nl.tudelft.graphalytics.powergraph.algorithms.pr.PageRankJob;
import nl.tudelft.graphalytics.powergraph.algorithms.sssp.SingleSourceShortestPathsJob;
import nl.tudelft.graphalytics.powergraph.algorithms.stats.LocalClusteringCoefficientJob;

/**
 * PowerGraph implementation of the Graphalytics benchmark.
 *
 * @author Stijn Heldens
 */
public class PowerGraphPlatform implements Platform {
	private static final Logger LOG = LogManager.getLogger();

	/**
	 * File name for the file storing configuration options
	 */
	public static final String POWERGRAPH_PROPERTIES_FILE = "powergraph.properties";

	public static String POWERGRAPH_BINARY_NAME = "bin/standard/main";

	private boolean graphDirected;
	private String edgeFilePath;
	private String vertexFilePath;
	private Configuration config;

	public PowerGraphPlatform() {
		try {
			config = new PropertiesConfiguration(POWERGRAPH_PROPERTIES_FILE);
		} catch(ConfigurationException e) {
			LOG.warn("failed to load " + POWERGRAPH_PROPERTIES_FILE, e);
			config = new PropertiesConfiguration();
		}

	}

	@Override
	public void uploadGraph(Graph graph) throws Exception {
		graphDirected = graph.getGraphFormat().isDirected();
		edgeFilePath = graph.getEdgeFilePath();
		vertexFilePath = graph.getVertexFilePath();
	}

	@Override
	public PlatformBenchmarkResult executeAlgorithmOnGraph(Benchmark benchmark) throws PlatformExecutionException {
		PowerGraphJob job;
		Object params = benchmark.getAlgorithmParameters();

		switch(benchmark.getAlgorithm()) {
			case BFS:
				job = new BreadthFirstSearchJob(config, vertexFilePath, edgeFilePath,
						graphDirected, (BreadthFirstSearchParameters) params, benchmark.getId());
				break;
			case WCC:
				job = new ConnectedComponentsJob(config, vertexFilePath, edgeFilePath,
						graphDirected, benchmark.getId());
				break;
			case LCC:
				job = new LocalClusteringCoefficientJob(config, vertexFilePath, edgeFilePath,
						graphDirected, benchmark.getId());
				break;
			case CDLP:
				job = new CommunityDetectionJob(config, vertexFilePath, edgeFilePath,
						graphDirected, (CommunityDetectionLPParameters) params, benchmark.getId());
				break;
			case PR:
				job = new PageRankJob(config, vertexFilePath, edgeFilePath,
						graphDirected, (PageRankParameters) params, benchmark.getId());
				break;
			case SSSP:
				job = new SingleSourceShortestPathsJob(config, vertexFilePath, edgeFilePath,
						graphDirected, (SingleSourceShortestPathsParameters) params, benchmark.getId());
				break;
			default:
				throw new PlatformExecutionException("Unsupported algorithm");
		}

		if (benchmark.isOutputRequired()) {
			job.setOutputFile(new File(benchmark.getOutputPath()));
		}

		try {
			job.run();
		} catch (IOException|InterruptedException e) {
			throw new PlatformExecutionException("failed to execute command", e);
		}

		return new PlatformBenchmarkResult(NestedConfiguration.empty());
	}

	@Override
	public void deleteGraph(String graphName) {
		//
	}

	@Override
	public String getName() {
		return "powergraph";
	}

	@Override
	public NestedConfiguration getPlatformConfiguration() {
		return NestedConfiguration.empty();
	}

}
