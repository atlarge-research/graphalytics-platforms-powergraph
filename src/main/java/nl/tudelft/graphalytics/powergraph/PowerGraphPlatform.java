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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;

import nl.tudelft.granula.archiver.PlatformArchive;
import nl.tudelft.granula.modeller.job.JobModel;
import nl.tudelft.granula.modeller.platform.Powergraph;
import nl.tudelft.graphalytics.BenchmarkMetrics;
import nl.tudelft.graphalytics.domain.*;
import nl.tudelft.graphalytics.granula.GranulaAwarePlatform;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nl.tudelft.graphalytics.Platform;
import nl.tudelft.graphalytics.PlatformExecutionException;
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
import org.json.simple.JSONObject;

/**
 * PowerGraph implementation of the Graphalytics benchmark.
 *
 * @author Stijn Heldens
 */
public class PowerGraphPlatform implements GranulaAwarePlatform {
	protected static final Logger LOG = LogManager.getLogger();

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
		POWERGRAPH_BINARY_NAME = "./bin/granula/main";
	}

	@Override
	public void uploadGraph(Graph graph) throws Exception {
		graphDirected = graph.getGraphFormat().isDirected();
		edgeFilePath = graph.getEdgeFilePath();
		vertexFilePath = graph.getVertexFilePath();
	}

	private void setupGraphPath(Graph graph) {
		graphDirected = graph.getGraphFormat().isDirected();
		edgeFilePath = graph.getEdgeFilePath();
		vertexFilePath = graph.getVertexFilePath();
	}

	@Override
	public PlatformBenchmarkResult executeAlgorithmOnGraph(Benchmark benchmark) throws PlatformExecutionException {
		PowerGraphJob job;
		Object params = benchmark.getAlgorithmParameters();

		setupGraphPath(benchmark.getGraph());

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
	public BenchmarkMetrics retrieveMetrics() {
		return new BenchmarkMetrics();
	}


	@Override
	public String getName() {
		return "powergraph";
	}

	@Override
	public NestedConfiguration getPlatformConfiguration() {
		return NestedConfiguration.empty();
	}



	@Override
	public void preBenchmark(Benchmark benchmark, Path logDirectory) {
		startPlatformLogging(logDirectory.resolve("platform").resolve("driver.logs"));
	}

	@Override
	public void postBenchmark(Benchmark benchmark, Path logDirectory) {
		stopPlatformLogging();
	}

	@Override
	public JobModel getJobModel() {
		return new JobModel(new Powergraph());
	}


	private static PrintStream console;

	public static void startPlatformLogging(Path fileName) {
		console = System.out;
		try {
			File file = null;
			file = fileName.toFile();
			file.getParentFile().mkdirs();
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);
			PrintStream ps = new PrintStream(fos);
			System.setOut(ps);
		} catch(Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException("cannot redirect to output file");
		}
		System.out.println("StartTime: " + System.currentTimeMillis());
	}

	public static void stopPlatformLogging() {
		System.out.println("EndTime: " + System.currentTimeMillis());
		System.setOut(console);
	}


	@Override
	public void enrichMetrics(BenchmarkResult benchmarkResult, Path arcDirectory) {
		try {
			PlatformArchive platformArchive = PlatformArchive.readArchive(arcDirectory);
			JSONObject processGraph = platformArchive.operation("ProcessGraph");
			Integer procTime = Integer.parseInt(platformArchive.info(processGraph, "Duration"));
			BenchmarkMetrics metrics = benchmarkResult.getMetrics();
			metrics.setProcessingTime(procTime);
		} catch(Exception e) {
			LOG.error("Failed to enrich metrics.");
		}
	}
}
