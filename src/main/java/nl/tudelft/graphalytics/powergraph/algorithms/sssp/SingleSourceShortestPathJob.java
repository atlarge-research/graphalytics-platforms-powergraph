package nl.tudelft.graphalytics.powergraph.algorithms.sssp;

import java.util.List;

import org.apache.commons.configuration.Configuration;

import nl.tudelft.graphalytics.domain.algorithms.SingleSourceShortestPathParameters;
import nl.tudelft.graphalytics.powergraph.PowerGraphJob;

public class SingleSourceShortestPathJob extends PowerGraphJob {

	SingleSourceShortestPathParameters params;

	public SingleSourceShortestPathJob(Configuration config, String verticesPath, String edgesPath, boolean graphDirected, SingleSourceShortestPathParameters params) {
		super(config, verticesPath, edgesPath, graphDirected);
		this.params = params;
	}

	@Override
	protected void addJobArguments(List<String> args) {
		args.add("sssp");
		args.add("--source-vertex");
		args.add(Long.toString(params.getSourceVertex()));
	}
}
