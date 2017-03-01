package nl.tudelft.graphalytics.powergraph.algorithms.cd;

import java.util.List;

import org.apache.commons.configuration.Configuration;

import nl.tudelft.graphalytics.domain.algorithms.CommunityDetectionLPParameters;
import nl.tudelft.graphalytics.powergraph.PowergraphJob;

public class CommunityDetectionJob extends PowergraphJob {
	private CommunityDetectionLPParameters params;
	
	public CommunityDetectionJob(Configuration config, String verticesPath, String edgesPath, boolean graphDirected,
								 CommunityDetectionLPParameters params, String jobId) {
		super(config, verticesPath, edgesPath, graphDirected, jobId);
		this.params = params;
	}

	@Override
	protected void addJobArguments(List<String> args) {
		args.add("cd");
		args.add("--max-iterations");
		args.add(Integer.toString(params.getMaxIterations()));
	}
}
