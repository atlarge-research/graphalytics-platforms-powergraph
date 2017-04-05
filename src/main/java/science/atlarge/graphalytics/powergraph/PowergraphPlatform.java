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
package science.atlarge.graphalytics.powergraph;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;

import nl.tudelft.granula.archiver.PlatformArchive;
import nl.tudelft.granula.modeller.job.JobModel;
import nl.tudelft.granula.modeller.platform.Powergraph;
import science.atlarge.graphalytics.domain.graph.FormattedGraph;
import science.atlarge.graphalytics.report.result.BenchmarkMetrics;
import science.atlarge.graphalytics.report.result.BenchmarkResult;
import science.atlarge.graphalytics.domain.benchmark.BenchmarkRun;
import science.atlarge.graphalytics.granula.GranulaAwarePlatform;
import science.atlarge.graphalytics.report.result.PlatformBenchmarkResult;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import science.atlarge.graphalytics.execution.PlatformExecutionException;
import science.atlarge.graphalytics.domain.algorithms.BreadthFirstSearchParameters;
import science.atlarge.graphalytics.domain.algorithms.CommunityDetectionLPParameters;
import science.atlarge.graphalytics.domain.algorithms.PageRankParameters;
import science.atlarge.graphalytics.domain.algorithms.SingleSourceShortestPathsParameters;
import science.atlarge.graphalytics.powergraph.algorithms.bfs.BreadthFirstSearchJob;
import science.atlarge.graphalytics.powergraph.algorithms.cdlp.CommunityDetectionJob;
import science.atlarge.graphalytics.powergraph.algorithms.wcc.ConnectedComponentsJob;
import science.atlarge.graphalytics.powergraph.algorithms.pr.PageRankJob;
import science.atlarge.graphalytics.powergraph.algorithms.sssp.SingleSourceShortestPathsJob;
import science.atlarge.graphalytics.powergraph.algorithms.lcc.LocalClusteringCoefficientJob;
import org.json.simple.JSONObject;

/**
 * PowerGraph implementation of the Graphalytics benchmark.
 *
 * @author Stijn Heldens
 */
public class PowergraphPlatform implements GranulaAwarePlatform {
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

	public PowergraphPlatform() {
		try {
			config = new PropertiesConfiguration(POWERGRAPH_PROPERTIES_FILE);
		} catch(ConfigurationException e) {
			LOG.warn("failed to load " + POWERGRAPH_PROPERTIES_FILE, e);
			config = new PropertiesConfiguration();
		}
		POWERGRAPH_BINARY_NAME = "./bin/granula/main";
	}

	@Override
	public void uploadGraph(FormattedGraph formattedGraph) throws Exception {
		graphDirected = formattedGraph.isDirected();
		edgeFilePath = formattedGraph.getEdgeFilePath();
		vertexFilePath = formattedGraph.getVertexFilePath();
	}

	private void setupGraphPath(FormattedGraph formattedGraph) {
		graphDirected = formattedGraph.isDirected();
		edgeFilePath = formattedGraph.getEdgeFilePath();
		vertexFilePath = formattedGraph.getVertexFilePath();
	}

	@Override
	public boolean execute(BenchmarkRun benchmarkRun) throws PlatformExecutionException {
		PowergraphJob job;
		Object params = benchmarkRun.getAlgorithmParameters();

		setupGraphPath(benchmarkRun.getFormattedGraph());

		switch(benchmarkRun.getAlgorithm()) {
			case BFS:
				job = new BreadthFirstSearchJob(config, vertexFilePath, edgeFilePath,
						graphDirected, (BreadthFirstSearchParameters) params, benchmarkRun.getId());
				break;
			case WCC:
				job = new ConnectedComponentsJob(config, vertexFilePath, edgeFilePath,
						graphDirected, benchmarkRun.getId());
				break;
			case LCC:
				job = new LocalClusteringCoefficientJob(config, vertexFilePath, edgeFilePath,
						graphDirected, benchmarkRun.getId());
				break;
			case CDLP:
				job = new CommunityDetectionJob(config, vertexFilePath, edgeFilePath,
						graphDirected, (CommunityDetectionLPParameters) params, benchmarkRun.getId());
				break;
			case PR:
				job = new PageRankJob(config, vertexFilePath, edgeFilePath,
						graphDirected, (PageRankParameters) params, benchmarkRun.getId());
				break;
			case SSSP:
				job = new SingleSourceShortestPathsJob(config, vertexFilePath, edgeFilePath,
						graphDirected, (SingleSourceShortestPathsParameters) params, benchmarkRun.getId());
				break;
			default:
				throw new PlatformExecutionException("Unsupported algorithm");
		}

		if (benchmarkRun.isOutputRequired()) {
			Path outputFile = benchmarkRun.getOutputDir().resolve(benchmarkRun.getName());
			job.setOutputFile(outputFile.toFile());
		}

		try {
			job.run();
		} catch (IOException|InterruptedException e) {
			throw new PlatformExecutionException("failed to execute command", e);
		}

		return true;
	}

	@Override
	public void deleteGraph(FormattedGraph formattedGraph) {
		//
	}



	@Override
	public String getPlatformName() {
		return "powergraph";
	}

	@Override
	public void prepare(BenchmarkRun benchmarkRun) {

	}

	@Override
	public void preprocess(BenchmarkRun benchmarkRun) {
		startPlatformLogging(benchmarkRun.getLogDir().resolve("platform").resolve("driver.logs"));
	}

	@Override
	public void cleanup(BenchmarkRun benchmarkRun) {

	}

	@Override
	public void postprocess(BenchmarkRun benchmarkRun) {
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
