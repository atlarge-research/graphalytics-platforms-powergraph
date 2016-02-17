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
package nl.tudelft.graphalytics.powergraph;

import nl.tudelft.graphalytics.granula.GranulaAwarePlatform;
import nl.tudelft.graphalytics.powergraph.reporting.logging.PowerGraphLogger;
import nl.tudelft.pds.granula.modeller.powergraph.job.PowerGraph;
import nl.tudelft.pds.granula.modeller.model.job.JobModel;
import java.nio.file.Path;

/**
 * PowerGraph platform integration for the Graphalytics benchmark.
 */
public final class PowerGraphGranulaPlatform extends PowerGraphPlatform implements GranulaAwarePlatform {


	public PowerGraphGranulaPlatform() {
		super();
		POWERGRAPH_BINARY_NAME = "./bin/granula/main";
	}

	@Override
	public void setBenchmarkLogDirectory(Path logDirectory) {
		PowerGraphLogger.startPlatformLogging(logDirectory.resolve("OperationLog").resolve("driver.logs"));
	}

	@Override
	public void finalizeBenchmarkLogs(Path logDirectory) {
		PowerGraphLogger.stopPlatformLogging();
	}

	@Override
	public JobModel getGranulaModel() {
		return new PowerGraph();
	}

}
