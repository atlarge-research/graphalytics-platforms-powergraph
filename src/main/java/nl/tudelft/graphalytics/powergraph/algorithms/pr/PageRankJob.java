package nl.tudelft.graphalytics.powergraph.algorithms.pr;

import java.util.List;

import org.apache.commons.configuration.Configuration;

import nl.tudelft.graphalytics.domain.algorithms.PageRankParameters;
import nl.tudelft.graphalytics.powergraph.PowerGraphJob;

public class PageRankJob extends PowerGraphJob {
	PageRankParameters params;

	public PageRankJob(Configuration config, String graphPath, boolean graphDirected, PageRankParameters params) {
		super(config, graphPath, graphDirected);
		this.params = params;
	}

	@Override
	protected void addJobArguments(List<String> args) {
		args.add("pr");
		args.add("--damping-factor");
		args.add(Float.toString(params.getDampingFactor()));
		args.add("--max-iterations");
		args.add(Integer.toString(params.getNumberOfIterations()));
	}
}
