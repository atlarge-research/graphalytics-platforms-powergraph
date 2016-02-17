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

package nl.tudelft.pds.granula.modeller.powergraph.operation;

import nl.tudelft.pds.granula.modeller.powergraph.PowerGraphType;
import nl.tudelft.pds.granula.modeller.model.operation.ConcreteOperationModel;
import nl.tudelft.pds.granula.modeller.rule.linking.UniqueParentLinking;
import nl.tudelft.pds.granula.modeller.rule.visual.MainInfoTableVisualization;

import java.util.ArrayList;

public class PowerGraphJob extends ConcreteOperationModel {

    public PowerGraphJob() {
        super(PowerGraphType.PowerGraph, PowerGraphType.Job);
    }

    public void loadRules() {
        super.loadRules();
        addLinkingRule(new UniqueParentLinking(PowerGraphType.TopActor, PowerGraphType.TopMission));


        addVisualDerivation(new MainInfoTableVisualization(1,
                new ArrayList<String>() {{
//                    add("InputMethod");
                }}));
    }

}
