package nl.tudelft.graphalytics.powergraph;

import java.io.File;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.Configuration;

abstract public class PowerGraphJob {

	private String verticesPath;
	private String edgesPath;
	private boolean graphDirected;
	private File outputFile;
	private Configuration config;
	
	public PowerGraphJob(Configuration config, String verticesPath, String edgesPath, boolean graphDirected) {
		this.config = config;
		this.verticesPath = verticesPath;
		this.edgesPath = edgesPath;
		this.graphDirected = graphDirected;
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
		
		String argsString = "";
		
		for (String arg: args) {
			argsString += arg += " ";
		}
		
		String cmdFormat = config.getString("powergraph.command", "%s %s");
		String cmd = String.format(cmdFormat,"./" + PowerGraphPlatform.POWERGRAPH_BINARY_NAME, argsString);
		
		ProcessBuilder pb = new ProcessBuilder(cmd.split(" "));
		pb.redirectError(Redirect.INHERIT);
		pb.redirectOutput(Redirect.INHERIT);

		Process proc = pb.start();
		int exit = proc.waitFor();
		
		if (exit != 0) {
			throw new IOException("unexpected error code");
		}
	}
}
