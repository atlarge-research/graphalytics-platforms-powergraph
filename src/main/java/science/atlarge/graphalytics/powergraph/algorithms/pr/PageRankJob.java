package science.atlarge.graphalytics.powergraph.algorithms.pr;

import java.util.List;

import org.apache.commons.configuration.Configuration;

import science.atlarge.graphalytics.domain.algorithms.PageRankParameters;
import science.atlarge.graphalytics.powergraph.PowergraphJob;

public class PageRankJob extends PowergraphJob {
	PageRankParameters params;

	public PageRankJob(Configuration config, String verticesPath, String edgesPath,
					   boolean graphDirected, PageRankParameters params, String jobId) {
		super(config, verticesPath, edgesPath, graphDirected, jobId);
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
