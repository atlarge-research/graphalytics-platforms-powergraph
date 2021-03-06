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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

abstract public class PowergraphJob {
	private static final Logger LOG = LogManager.getLogger(PowergraphJob.class);

	private String jobId;
	private String verticesPath;
	private String edgesPath;
	private boolean graphDirected;
	private File outputFile;
	private Configuration config;
	private String logPath;

	public PowergraphJob(Configuration config, String verticesPath, String edgesPath, boolean graphDirected, String jobId, String logPath) {
		this.config = config;
		this.verticesPath = verticesPath;
		this.edgesPath = edgesPath;
		this.graphDirected = graphDirected;
		this.jobId = jobId;
		this.logPath = logPath;
	}

	abstract protected void addJobArguments(List<String> args);

	public void setOutputFile(File file) {
		outputFile = file;
	}

	public void run() throws IOException, InterruptedException {
		List<String> args = new ArrayList<>();
		args.add(verticesPath);
		args.add(edgesPath);
		args.add(graphDirected ? "1" : "0");
		addJobArguments(args);

		if (outputFile != null) {
			args.add("--output-file");
			args.add(outputFile.getAbsolutePath());
		}

		int numThreads = config.getInt("platform.powergraph.num-threads", -1);

		if (numThreads > 0) {
			args.add("--ncpus");
			args.add(String.valueOf(numThreads));
		}

		args.add("--job-id");
		args.add(jobId);

		String argsString = "";

		for (String arg: args) {
			argsString += arg += " ";
		}

		String nodes = config.getString("platform.powergraph.nodes");
		String cmd = String.format("./bin/sh/run-mpi.sh %s %s %s %s", nodes, logPath, PowergraphPlatform.POWERGRAPH_BINARY_NAME, argsString);

		LOG.info("executing command: " + cmd);

		ProcessBuilder pb = new ProcessBuilder(cmd.split(" "));
		pb.redirectErrorStream(true);


		Process process = pb.start();
		InputStreamReader isr = new InputStreamReader(process.getInputStream());
		BufferedReader br = new BufferedReader(isr);
		String line;
		while ((line = br.readLine()) != null) {
			System.out.println(line);
		}

		int exit = process.waitFor();

		if (exit != 0) {
			throw new IOException("unexpected error code");
		}
	}
}
