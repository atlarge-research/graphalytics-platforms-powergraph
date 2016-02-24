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

import nl.tudelft.pds.granula.archiver.entity.operation.Operation;
import nl.tudelft.pds.granula.modeller.model.operation.ConcreteOperationModel;
import nl.tudelft.pds.granula.modeller.powergraph.PowerGraphType;
import nl.tudelft.pds.granula.modeller.rule.derivation.DerivationRule;
import nl.tudelft.pds.granula.modeller.rule.linking.UniqueParentLinking;
import nl.tudelft.pds.granula.modeller.rule.visual.MainInfoTableVisualization;

import java.util.ArrayList;

public class LoadGraph extends ConcreteOperationModel {

    public LoadGraph() {
        super(PowerGraphType.PowerGraph, PowerGraphType.LoadGraph);
    }

    public void loadRules() {
        super.loadRules();

        addLinkingRule(new UniqueParentLinking(PowerGraphType.PowerGraph, PowerGraphType.Job));


//        addInfoDerivation(new RecordInfoDerivation(1, "StackTrace"));
//        addInfoDerivation(new DurationDerivation(3));
//        addInfoDerivation(new RecordInfoDerivation(1, "JobName"));
//        addInfoDerivation(new SummaryDerivation(10));
//        addInfoDerivation(new ActorIdShortenerDerivation(4));

        addVisualDerivation(new MainInfoTableVisualization(1,
                new ArrayList<String>() {{
//                    add("InputMethod");
//                    add("RunTime");
//                    add("InputSize");
//                    add("InputMethod");
//                    add("ShuffleRead");
//                    add("ShuffleWrite");
                }}));
    }



    protected class ActorIdShortenerDerivation extends DerivationRule {

        public ActorIdShortenerDerivation(int level) { super(level); }

        @Override
        public boolean execute() {
            Operation operation = (Operation) entity;
            String actorId = operation.getActor().getId();

            String shortenedId = actorId.substring(0, 2) + ".." + actorId.substring(actorId.length() - 8, actorId.length());
            operation.getActor().setId(shortenedId);

            return  true;
        }
    }

}
