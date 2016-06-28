package nl.tudelft.graphalytics.powergraph.algorithms.stats;

import java.util.List;

import org.apache.commons.configuration.Configuration;

import nl.tudelft.graphalytics.powergraph.PowerGraphJob;

public class LocalClusteringCoefficientJob extends PowerGraphJob {

	public LocalClusteringCoefficientJob(Configuration config, String verticesPath, String edgesPath, boolean graphDirected, String jobId) {
		super(config, verticesPath, edgesPath, graphDirected, jobId);
	}

	@Override
	protected void addJobArguments(List<String> args) {
		args.add("lcc");
	}
}
