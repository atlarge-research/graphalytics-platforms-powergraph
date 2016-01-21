package nl.tudelft.graphalytics.powergraph.algorithms.cd;

import java.util.List;

import org.apache.commons.configuration.Configuration;

import nl.tudelft.graphalytics.domain.algorithms.CommunityDetectionParameters;
import nl.tudelft.graphalytics.powergraph.PowerGraphJob;

public class CommunityDetectionJob extends PowerGraphJob {
	private CommunityDetectionParameters params;
	
	public CommunityDetectionJob(Configuration config, String graphPath, boolean graphDirected, CommunityDetectionParameters params) {
		super(config, graphPath, graphDirected);
		this.params = params;
	}

	@Override
	protected void addJobArguments(List<String> args) {
		args.add("cd");
		args.add("--max-iterations");
		args.add(Integer.toString(params.getMaxIterations()));
	}
}
