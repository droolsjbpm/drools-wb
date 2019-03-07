/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.workbench.screens.scenariosimulation.backend.server.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.drools.workbench.screens.scenariosimulation.model.ExpressionIdentifier;
import org.drools.workbench.screens.scenariosimulation.model.FactIdentifier;
import org.drools.workbench.screens.scenariosimulation.model.FactMapping;
import org.drools.workbench.screens.scenariosimulation.model.FactMappingType;
import org.drools.workbench.screens.scenariosimulation.model.Scenario;
import org.drools.workbench.screens.scenariosimulation.model.ScenarioSimulationModel;
import org.drools.workbench.screens.scenariosimulation.model.Simulation;
import org.drools.workbench.screens.scenariosimulation.model.SimulationDescriptor;
import org.drools.workbench.screens.scenariosimulation.model.typedescriptor.FactModelTree;
import org.drools.workbench.screens.scenariosimulation.model.typedescriptor.FactModelTuple;
import org.drools.workbench.screens.scenariosimulation.service.DMNTypeService;
import org.drools.workbench.screens.scenariosimulation.utils.ScenarioSimulationSharedUtils;
import org.uberfire.backend.vfs.Path;

import static org.drools.workbench.screens.scenariosimulation.model.typedescriptor.FactModelTree.Type;

@ApplicationScoped
public class DMNSimulationCreationStrategy implements SimulationCreationStrategy {

    @Inject
    protected DMNTypeService dmnTypeService;

    @Override
    public Simulation createSimulation(Path context, String dmnFilePath) throws Exception {
        final FactModelTuple factModelTuple = getFactModelTuple(context, dmnFilePath);
        Simulation toReturn = new Simulation();
        SimulationDescriptor simulationDescriptor = toReturn.getSimulationDescriptor();
        simulationDescriptor.setType(ScenarioSimulationModel.Type.DMN);
        simulationDescriptor.setDmnFilePath(dmnFilePath);
        Scenario scenario = createScenario(toReturn, simulationDescriptor);

        int row = toReturn.getUnmodifiableScenarios().indexOf(scenario);
        AtomicInteger id = new AtomicInteger(1);
        final Collection<FactModelTree> visibleFactTrees = factModelTuple.getVisibleFacts().values();
        final Map<String, FactModelTree> hiddenValues = factModelTuple.getHiddenFacts();

        visibleFactTrees.stream().sorted((a, b) -> {
            Type aType = a.getType();
            Type bType = b.getType();
            return aType.equals(bType) ? 0 : (Type.INPUT.equals(aType) ? -1 : 1);
        }).forEach(factModelTree -> {
            FactIdentifier factIdentifier = new FactIdentifier(factModelTree.getFactName(), factModelTree.getFactName());
            FactMappingExtractor factMappingExtractor = new FactMappingExtractor(factIdentifier, row, id, convert(factModelTree.getType()), simulationDescriptor, scenario);
            addToScenario(factMappingExtractor, factModelTree, new ArrayList<>(), hiddenValues);
        });

        return toReturn;
    }

    // Indirection for test
    protected FactModelTuple getFactModelTuple(Path context, String dmnFilePath) throws Exception {
        return dmnTypeService.retrieveType(context, dmnFilePath);
    }

    private void addToScenario(FactMappingExtractor factMappingExtractor, FactModelTree factModelTree, List<String> previousSteps, Map<String, FactModelTree> hiddenValues) {
        // if is a simple type it generates a single column
        if (factModelTree.isSimple()) {

            String factType = factModelTree.getSimpleProperties().get("value");
            factMappingExtractor.getFactMapping(factModelTree, "value", previousSteps, factType);
        }
        // otherwise it adds a column for each simple properties direct or nested
        else {
            for (Map.Entry<String, String> entry : factModelTree.getSimpleProperties().entrySet()) {
                String factName = entry.getKey();
                String factType = entry.getValue();

                FactMapping factMapping = factMappingExtractor.getFactMapping(factModelTree, factName, previousSteps, factType);

                if (ScenarioSimulationSharedUtils.isList(factType)) {
                    factMapping.setGenericTypes(factModelTree.getGenericTypeInfo(factName));
                }
                factMapping.addExpressionElement(factName, factType);
            }

            for (Map.Entry<String, String> entry : factModelTree.getExpandableProperties().entrySet()) {
                String factType = entry.getValue();
                FactModelTree nestedModelTree = hiddenValues.get(factType);

                if (previousSteps.isEmpty()) {
                    previousSteps.add(factModelTree.getFactName());
                }
                previousSteps.add(entry.getKey());
                addToScenario(factMappingExtractor, nestedModelTree, previousSteps, hiddenValues);
            }
        }
    }

    static private class FactMappingExtractor {

        private final FactIdentifier factIdentifier;
        private final int row;
        private final AtomicInteger id;
        private final FactMappingType type;
        private final SimulationDescriptor simulationDescriptor;
        private final Scenario scenario;

        public FactMappingExtractor(FactIdentifier factIdentifier, int row, AtomicInteger id, FactMappingType type, SimulationDescriptor simulationDescriptor, Scenario scenario) {
            this.factIdentifier = factIdentifier;
            this.row = row;
            this.id = id;
            this.type = type;
            this.simulationDescriptor = simulationDescriptor;
            this.scenario = scenario;
        }

        public FactMapping getFactMapping(FactModelTree factModelTree, String propertyName, List<String> previousSteps, String factType) {

            String factAlias = previousSteps.size() > 0 ? previousSteps.get(0) : factModelTree.getFactName();

            ExpressionIdentifier expressionIdentifier = ExpressionIdentifier.create(row + "|" + id.getAndIncrement(), type);
            final FactMapping factMapping = simulationDescriptor.addFactMapping(factAlias, factIdentifier, expressionIdentifier);

            List<String> localPreviousStep = new ArrayList<>(previousSteps);
            localPreviousStep.add(propertyName);
            String expressionAlias = String.join(".",
                                                 localPreviousStep.size() > 1 ?
                                                         localPreviousStep.subList(1, localPreviousStep.size()) :
                                                         localPreviousStep);
            factMapping.setExpressionAlias(expressionAlias);

            previousSteps.forEach(step -> factMapping.addExpressionElement(step, factType));

            if (previousSteps.isEmpty()) {
                factMapping.addExpressionElement(factModelTree.getFactName(), factType);
            }
            scenario.addMappingValue(factIdentifier, expressionIdentifier, null);

            return factMapping;
        }
    }

    static private FactMappingType convert(Type modelTreeType) {
        switch (modelTreeType) {
            case INPUT:
                return FactMappingType.GIVEN;
            case DECISION:
                return FactMappingType.EXPECT;
            default:
                throw new IllegalArgumentException("Impossible to map");
        }
    }
}
