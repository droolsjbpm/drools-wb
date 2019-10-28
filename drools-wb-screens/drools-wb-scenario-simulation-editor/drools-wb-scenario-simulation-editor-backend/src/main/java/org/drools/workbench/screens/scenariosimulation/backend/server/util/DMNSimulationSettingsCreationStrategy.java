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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.drools.scenariosimulation.api.model.AbstractScesimData;
import org.drools.scenariosimulation.api.model.Background;
import org.drools.scenariosimulation.api.model.BackgroundDataWithIndex;
import org.drools.scenariosimulation.api.model.ExpressionIdentifier;
import org.drools.scenariosimulation.api.model.FactIdentifier;
import org.drools.scenariosimulation.api.model.FactMapping;
import org.drools.scenariosimulation.api.model.FactMappingType;
import org.drools.scenariosimulation.api.model.ScenarioSimulationModel;
import org.drools.scenariosimulation.api.model.ScenarioWithIndex;
import org.drools.scenariosimulation.api.model.Settings;
import org.drools.scenariosimulation.api.model.Simulation;
import org.drools.scenariosimulation.api.model.SimulationDescriptor;
import org.drools.scenariosimulation.api.utils.ScenarioSimulationSharedUtils;
import org.drools.workbench.screens.scenariosimulation.model.typedescriptor.FactModelTree;
import org.drools.workbench.screens.scenariosimulation.model.typedescriptor.FactModelTuple;
import org.drools.workbench.screens.scenariosimulation.service.DMNTypeService;
import org.uberfire.backend.vfs.Path;

import static org.drools.scenariosimulation.api.model.FactMappingType.EXPECT;
import static org.drools.scenariosimulation.api.model.FactMappingType.GIVEN;
import static org.drools.workbench.screens.scenariosimulation.model.typedescriptor.FactModelTree.Type;

@ApplicationScoped
public class DMNSimulationSettingsCreationStrategy implements SimulationSettingsCreationStrategy {

    @Inject
    protected DMNTypeService dmnTypeService;

    @Override
    public Simulation createSimulation(Path context, String dmnFilePath) throws Exception {
        final FactModelTuple factModelTuple = getFactModelTuple(context, dmnFilePath);
        Simulation toReturn = new Simulation();
        SimulationDescriptor simulationDescriptor = toReturn.getSimulationDescriptor();
        ScenarioWithIndex scenarioWithIndex = createScesimDataWithIndex(toReturn, simulationDescriptor, ScenarioWithIndex.class);

        AtomicInteger id = new AtomicInteger(1);
        final Collection<FactModelTree> visibleFactTrees = factModelTuple.getVisibleFacts().values();
        final Map<String, FactModelTree> hiddenValues = factModelTuple.getHiddenFacts();

        visibleFactTrees.stream().sorted((a, b) -> {
            Type aType = a.getType();
            Type bType = b.getType();
            return aType.equals(bType) ? 0 : (Type.INPUT.equals(aType) ? -1 : 1);
        }).forEach(factModelTree -> {
            FactIdentifier factIdentifier = new FactIdentifier(factModelTree.getFactName(), factModelTree.getFactName());
            FactMappingExtractor factMappingExtractor = new FactMappingExtractor(factIdentifier, scenarioWithIndex.getIndex(), id, convert(factModelTree.getType()), simulationDescriptor, scenarioWithIndex.getScesimData());
            addFactMapping(factMappingExtractor, factModelTree, new ArrayList<>(), hiddenValues);
        });

        addEmptyColumnsIfNeeded(toReturn, scenarioWithIndex);

        return toReturn;
    }

    @Override
    public Background createBackground(Path context, String dmnFilePath) throws Exception {
        final FactModelTuple factModelTuple = getFactModelTuple(context, dmnFilePath);
        Background toReturn = new Background();
        SimulationDescriptor simulationDescriptor = toReturn.getSimulationDescriptor();
        BackgroundDataWithIndex backgroundDataWithIndex = createScesimDataWithIndex(toReturn, simulationDescriptor, BackgroundDataWithIndex.class);

        AtomicInteger id = new AtomicInteger(1);
        final Collection<FactModelTree> visibleFactTrees = factModelTuple.getVisibleFacts().values();
        final Map<String, FactModelTree> hiddenValues = factModelTuple.getHiddenFacts();

        visibleFactTrees.stream().sorted((a, b) -> {
            Type aType = a.getType();
            Type bType = b.getType();
            return aType.equals(bType) ? 0 : (Type.INPUT.equals(aType) ? -1 : 1);
        }).forEach(factModelTree -> {
            FactIdentifier factIdentifier = new FactIdentifier(factModelTree.getFactName(), factModelTree.getFactName());
            FactMappingExtractor factMappingExtractor = new FactMappingExtractor(factIdentifier, backgroundDataWithIndex.getIndex(), id, convert(factModelTree.getType()), simulationDescriptor, backgroundDataWithIndex.getScesimData());
            addFactMapping(factMappingExtractor, factModelTree, new ArrayList<>(), hiddenValues);
        });
        return toReturn;
    }

    @Override
    public Settings createSettings(String dmnFilePath) throws Exception {
        Settings toReturn = new Settings();
        toReturn.setType(ScenarioSimulationModel.Type.DMN);
        toReturn.setDmnFilePath(dmnFilePath);
        return toReturn;
    }

    /**
     * If DMN model is empty, contains only inputs or only outputs this method add one GIVEN and/or EXPECT empty column
     * @param simulation
     * @param scenarioWithIndex
     */
    protected void addEmptyColumnsIfNeeded(Simulation simulation, ScenarioWithIndex scenarioWithIndex) {
        boolean hasGiven = false;
        boolean hasExpect = false;
        SimulationDescriptor simulationDescriptor = simulation.getSimulationDescriptor();
        for (FactMapping factMapping : simulationDescriptor.getFactMappings()) {
            FactMappingType factMappingType = factMapping.getExpressionIdentifier().getType();
            if (!hasGiven && GIVEN.equals(factMappingType)) {
                hasGiven = true;
            } else if (!hasExpect && EXPECT.equals(factMappingType)) {
                hasExpect = true;
            }
        }
        if (!hasGiven) {
            createEmptyColumn(simulationDescriptor,
                              scenarioWithIndex,
                              1,
                              GIVEN,
                              findNewIndexOfGroup(simulationDescriptor, GIVEN));
        }
        if (!hasExpect) {
            createEmptyColumn(simulationDescriptor,
                              scenarioWithIndex,
                              2,
                              EXPECT,
                              findNewIndexOfGroup(simulationDescriptor, EXPECT));
        }
    }

    protected int findNewIndexOfGroup(SimulationDescriptor simulationDescriptor, FactMappingType factMappingType) {
        List<FactMapping> factMappings = simulationDescriptor.getFactMappings();
        if (GIVEN.equals(factMappingType)) {
            for (int i = 0; i < factMappings.size(); i += 1) {
                if (EXPECT.equals(factMappings.get(i).getExpressionIdentifier().getType())) {
                    return i;
                }
            }
            return factMappings.size();
        } else if (EXPECT.equals(factMappingType)) {
            return factMappings.size();
        } else {
            throw new IllegalArgumentException("This method can be invoked only with GIVEN or EXPECT as FactMappingType");
        }
    }

    // Indirection for test
    protected FactModelTuple getFactModelTuple(Path context, String dmnFilePath) throws Exception {
        return dmnTypeService.retrieveFactModelTuple(context, dmnFilePath);
    }

    protected void addFactMapping(FactMappingExtractor factMappingExtractor,
                                 FactModelTree factModelTree,
                                 List<String> previousSteps,
                                 Map<String, FactModelTree> hiddenValues) {
        internalAddToScenario(factMappingExtractor,
                              factModelTree,
                              previousSteps,
                              hiddenValues,
                              new HashSet<>());
    }

    protected void internalAddToScenario(FactMappingExtractor factMappingExtractor,
                                         FactModelTree factModelTree,
                                         List<String> readOnlyPreviousSteps,
                                         Map<String, FactModelTree> hiddenValues,
                                         Set<String> alreadyVisited) {

        List<String> previousSteps = new ArrayList<>(readOnlyPreviousSteps);
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

                if (!alreadyVisited.contains(nestedModelTree.getFactName())) {
                    alreadyVisited.add(factModelTree.getFactName());
                    internalAddToScenario(factMappingExtractor, nestedModelTree, previousSteps, hiddenValues, alreadyVisited);
                }
            }
        }
    }

    public static class FactMappingExtractor {

        private final FactIdentifier factIdentifier;
        private final int row;
        private final AtomicInteger id;
        private final FactMappingType type;
        private final SimulationDescriptor simulationDescriptor;
        private final AbstractScesimData abstractScesimData;

        public FactMappingExtractor(FactIdentifier factIdentifier, int row, AtomicInteger id, FactMappingType type, SimulationDescriptor simulationDescriptor, AbstractScesimData abstractScesimData) {
            this.factIdentifier = factIdentifier;
            this.row = row;
            this.id = id;
            this.type = type;
            this.simulationDescriptor = simulationDescriptor;
            this.abstractScesimData = abstractScesimData;
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
            factMapping.setGenericTypes(factModelTree.getGenericTypeInfo("value"));

            previousSteps.forEach(step -> factMapping.addExpressionElement(step, factType));

            if (previousSteps.isEmpty()) {
                factMapping.addExpressionElement(factModelTree.getFactName(), factType);
            }
            abstractScesimData.addMappingValue(factIdentifier, expressionIdentifier, null);

            return factMapping;
        }
    }

    static private FactMappingType convert(Type modelTreeType) {
        switch (modelTreeType) {
            case INPUT:
                return GIVEN;
            case DECISION:
                return EXPECT;
            default:
                throw new IllegalArgumentException("Impossible to map");
        }
    }
}
