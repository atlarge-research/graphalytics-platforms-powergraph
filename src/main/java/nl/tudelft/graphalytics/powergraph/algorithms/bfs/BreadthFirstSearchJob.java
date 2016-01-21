package nl.tudelft.graphalytics.powergraph.algorithms.bfs;

import java.util.List;

import org.apache.commons.configuration.Configuration;

import nl.tudelft.graphalytics.domain.algorithms.BreadthFirstSearchParameters;
import nl.tudelft.graphalytics.powergraph.PowerGraphJob;

public class BreadthFirstSearchJob extends PowerGraphJob {
	
	BreadthFirstSearchParameters params;
	
	public BreadthFirstSearchJob(Configuration config, String graphPath, boolean graphDirected, BreadthFirstSearchParameters params) {
		super(config, graphPath, graphDirected);
		this.params = params;
	}

	@Override
	protected void addJobArguments(List<String> args) {
		args.add("bfs");
		args.add("--source-vertex");
		args.add(Long.toString(params.getSourceVertex()));
	}
}
