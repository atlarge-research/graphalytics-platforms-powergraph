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
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import science.atlarge.granula.archiver.PlatformArchive;
import science.atlarge.granula.modeller.job.JobModel;
import science.atlarge.granula.modeller.platform.Powergraph;
import science.atlarge.granula.util.FileUtil;
import org.apache.commons.io.output.TeeOutputStream;
import science.atlarge.graphalytics.configuration.ConfigurationUtil;
import science.atlarge.graphalytics.configuration.InvalidConfigurationException;
import science.atlarge.graphalytics.domain.graph.FormattedGraph;
import science.atlarge.graphalytics.report.result.BenchmarkMetric;
import science.atlarge.graphalytics.report.result.BenchmarkMetrics;
import science.atlarge.graphalytics.report.result.BenchmarkRunResult;
import science.atlarge.graphalytics.domain.benchmark.BenchmarkRun;
import science.atlarge.graphalytics.granula.GranulaAwarePlatform;
import org.apache.commons.configuration.Configuration;
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
	private static PrintStream sysOut;
	private static PrintStream sysErr;

	public static final String BENCHMARK_PROPERTIES_FILE = "benchmark.properties";
	private static final String GRANULA_PROPERTIES_FILE = "granula.properties";

	public static final String GRANULA_ENABLE_KEY = "benchmark.run.granula.enabled";
	public static String POWERGRAPH_BINARY_NAME = "bin/standard/main";

	private boolean graphDirected;
	private String edgeFilePath;
	private String vertexFilePath;
	private Configuration benchmarkConfig;


	public PowergraphPlatform() {

		Configuration granulaConfig;
		try {
			benchmarkConfig = ConfigurationUtil.loadConfiguration(BENCHMARK_PROPERTIES_FILE);
			granulaConfig = ConfigurationUtil.loadConfiguration(GRANULA_PROPERTIES_FILE);
		} catch(InvalidConfigurationException e) {
			LOG.warn("failed to load " + BENCHMARK_PROPERTIES_FILE, e);
			LOG.warn("Could not find or load \"{}\"", GRANULA_PROPERTIES_FILE);
			benchmarkConfig = new PropertiesConfiguration();
			granulaConfig = new PropertiesConfiguration();
		}

		boolean granulaEnabled = granulaConfig.getBoolean(GRANULA_ENABLE_KEY, false);
		POWERGRAPH_BINARY_NAME = granulaEnabled ? "./bin/granula/main": POWERGRAPH_BINARY_NAME;
	}

	@Override
	public void verifySetup() {

	}

	@Override
	public void loadGraph(FormattedGraph formattedGraph) throws Exception {
		graphDirected = formattedGraph.isDirected();
		edgeFilePath = formattedGraph.getEdgeFilePath();
		vertexFilePath = formattedGraph.getVertexFilePath();
	}

	@Override
	public void deleteGraph(FormattedGraph formattedGraph) {
		//
	}

	private void setupGraphPath(FormattedGraph formattedGraph) {
		graphDirected = formattedGraph.isDirected();
		edgeFilePath = formattedGraph.getEdgeFilePath();
		vertexFilePath = formattedGraph.getVertexFilePath();
	}

	@Override
	public void prepare(BenchmarkRun benchmarkRun) {

	}

	@Override
	public void run(BenchmarkRun benchmarkRun) throws PlatformExecutionException {
		PowergraphJob job;
		Object params = benchmarkRun.getAlgorithmParameters();

		String logPath = benchmarkRun.getLogDir().resolve("platform").toString();

		setupGraphPath(benchmarkRun.getFormattedGraph());

		switch(benchmarkRun.getAlgorithm()) {
			case BFS:
				job = new BreadthFirstSearchJob(benchmarkConfig, vertexFilePath, edgeFilePath,
						graphDirected, (BreadthFirstSearchParameters) params, benchmarkRun.getId(), logPath);
				break;
			case WCC:
				job = new ConnectedComponentsJob(benchmarkConfig, vertexFilePath, edgeFilePath,
						graphDirected, benchmarkRun.getId(), logPath);
				break;
			case LCC:
				job = new LocalClusteringCoefficientJob(benchmarkConfig, vertexFilePath, edgeFilePath,
						graphDirected, benchmarkRun.getId(), logPath);
				break;
			case CDLP:
				job = new CommunityDetectionJob(benchmarkConfig, vertexFilePath, edgeFilePath,
						graphDirected, (CommunityDetectionLPParameters) params, benchmarkRun.getId(), logPath);
				break;
			case PR:
				job = new PageRankJob(benchmarkConfig, vertexFilePath, edgeFilePath,
						graphDirected, (PageRankParameters) params, benchmarkRun.getId(), logPath);
				break;
			case SSSP:
				job = new SingleSourceShortestPathsJob(benchmarkConfig, vertexFilePath, edgeFilePath,
						graphDirected, (SingleSourceShortestPathsParameters) params, benchmarkRun.getId(), logPath);
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

	}

	@Override
	public void startup(BenchmarkRun benchmarkRun) {
		startPlatformLogging(benchmarkRun.getLogDir().resolve("platform").resolve("driver.logs"));
	}


	@Override
	public BenchmarkMetrics finalize(BenchmarkRun benchmarkRun) {
		stopPlatformLogging();

		Path platformLogPath = benchmarkRun.getLogDir().resolve("platform");

		final List<Double> superstepTimes = new ArrayList<>();

		try {
			Files.walkFileTree(platformLogPath, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					String logs = FileUtil.readFile(file);
					for (String line : logs.split("\n")) {
						if (line.contains("- run algorithm:")) {
							Pattern regex = Pattern.compile(
									".* - run algorithm: ([+-]?([0-9]*[.])?[0-9]+) sec.*");
							Matcher matcher = regex.matcher(line);
							matcher.find();
							superstepTimes.add(Double.parseDouble(matcher.group(1)));
						}
					}
					return FileVisitResult.CONTINUE;
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (superstepTimes.size() != 0) {
			Double procTime = 0.0;
			for (Double superstepTime : superstepTimes) {
				procTime += superstepTime;
			}

			BenchmarkMetrics metrics = new BenchmarkMetrics();
			BigDecimal procTimeS = (new BigDecimal(procTime)).setScale(3, RoundingMode.CEILING);
			metrics.setProcessingTime(new BenchmarkMetric(procTimeS, "s"));

			return metrics;
		} else {
			LOG.error("Failed to find any metrics regarding superstep runtime.");
			return new BenchmarkMetrics();
		}
	}

	@Override
	public void enrichMetrics(BenchmarkRunResult benchmarkRunResult, Path arcDirectory) {
		try {
			PlatformArchive platformArchive = PlatformArchive.readArchive(arcDirectory);
			JSONObject processGraph = platformArchive.operation("ProcessGraph");
			BenchmarkMetrics metrics = benchmarkRunResult.getMetrics();

			Integer procTimeMS = Integer.parseInt(platformArchive.info(processGraph, "Duration"));
			BigDecimal procTimeS = (new BigDecimal(procTimeMS)).divide(new BigDecimal(1000), 3, BigDecimal.ROUND_CEILING);
			metrics.setProcessingTime(new BenchmarkMetric(procTimeS, "s"));

		} catch(Exception e) {
			LOG.error("Failed to enrich metrics.");
		}
	}

	@Override
	public void terminate(BenchmarkRun benchmarkRun) {

	}

	private static void startPlatformLogging(Path fileName) {
		sysOut = System.out;
		sysErr = System.err;
		try {
			File file = null;
			file = fileName.toFile();
			file.getParentFile().mkdirs();
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);
			TeeOutputStream bothStream =new TeeOutputStream(System.out, fos);
			PrintStream ps = new PrintStream(bothStream);
			System.setOut(ps);
			System.setErr(ps);
		} catch(Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException("cannot redirect to output file");
		}
		System.out.println("StartTime: " + System.currentTimeMillis());
	}

	private static void stopPlatformLogging() {
		System.out.println("EndTime: " + System.currentTimeMillis());
		System.setOut(sysOut);
		System.setErr(sysErr);
	}

	@Override
	public JobModel getJobModel() {
		return new JobModel(new Powergraph());
	}

	@Override
	public String getPlatformName() {
		return "powergraph";
	}

}
