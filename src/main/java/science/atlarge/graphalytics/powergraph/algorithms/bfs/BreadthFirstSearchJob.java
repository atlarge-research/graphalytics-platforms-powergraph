package science.atlarge.graphalytics.powergraph.algorithms.bfs;

import java.util.List;

import org.apache.commons.configuration.Configuration;

import science.atlarge.graphalytics.domain.algorithms.BreadthFirstSearchParameters;
import science.atlarge.graphalytics.powergraph.PowergraphJob;

public class BreadthFirstSearchJob extends PowergraphJob {
	
	BreadthFirstSearchParameters params;

	public BreadthFirstSearchJob(Configuration config, String verticesPath, String edgesPath, boolean graphDirected,
								 BreadthFirstSearchParameters params, String jobId) {
		super(config, verticesPath, edgesPath, graphDirected, jobId);
		this.params = params;
	}

	@Override
	protected void addJobArguments(List<String> args) {
		args.add("bfs");
		args.add("--source-vertex");
		args.add(Long.toString(params.getSourceVertex()));
	}
}
