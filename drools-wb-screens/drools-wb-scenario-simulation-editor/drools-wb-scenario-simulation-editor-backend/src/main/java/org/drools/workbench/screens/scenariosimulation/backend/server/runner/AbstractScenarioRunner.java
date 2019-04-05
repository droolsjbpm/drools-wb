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

package org.drools.workbench.screens.scenariosimulation.backend.server.runner;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.drools.workbench.screens.scenariosimulation.backend.server.expression.ExpressionEvaluator;
import org.drools.workbench.screens.scenariosimulation.backend.server.runner.model.ScenarioResultMetadata;
import org.drools.workbench.screens.scenariosimulation.backend.server.runner.model.ScenarioRunnerData;
import org.drools.workbench.screens.scenariosimulation.model.Scenario;
import org.drools.workbench.screens.scenariosimulation.model.ScenarioWithIndex;
import org.drools.workbench.screens.scenariosimulation.model.Simulation;
import org.drools.workbench.screens.scenariosimulation.model.SimulationDescriptor;
import org.drools.workbench.screens.scenariosimulation.model.SimulationRunMetadata;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.kie.api.runtime.KieContainer;

import static org.drools.workbench.screens.scenariosimulation.model.ScenarioSimulationModel.Type;

public abstract class AbstractScenarioRunner extends Runner {

    protected final ClassLoader classLoader;
    protected final Function<ClassLoader, ExpressionEvaluator> expressionEvaluatorFactory;
    protected final Description desc;
    protected final KieContainer kieContainer;
    protected final SimulationDescriptor simulationDescriptor;
    protected List<ScenarioWithIndex> scenarios;
    protected String fileName;
    protected SimulationRunMetadataBuilder simulationRunMetadataBuilder;

    public AbstractScenarioRunner(KieContainer kieContainer,
                                  Simulation simulation,
                                  String fileName,
                                  Function<ClassLoader, ExpressionEvaluator> expressionEvaluatorFactory) {
        this(kieContainer, simulation.getSimulationDescriptor(), simulation.getScenarioWithIndex(), fileName, expressionEvaluatorFactory);
    }

    public AbstractScenarioRunner(KieContainer kieContainer,
                                  SimulationDescriptor simulationDescriptor,
                                  List<ScenarioWithIndex> scenarios,
                                  String fileName,
                                  Function<ClassLoader, ExpressionEvaluator> expressionEvaluatorFactory) {
        this.kieContainer = kieContainer;
        this.simulationDescriptor = simulationDescriptor;
        this.scenarios = scenarios;
        this.fileName = fileName;
        this.desc = getDescriptionForSimulation(getFileName(), simulationDescriptor, scenarios);
        this.classLoader = kieContainer.getClassLoader();
        this.expressionEvaluatorFactory = expressionEvaluatorFactory;
        this.simulationRunMetadataBuilder = SimulationRunMetadataBuilder.create();
    }

    @Override
    public void run(RunNotifier notifier) {

        notifier.fireTestStarted(getDescription());
        for (ScenarioWithIndex scenarioWithIndex : scenarios) {
            Scenario scenario = scenarioWithIndex.getScenario();
            Integer index = scenarioWithIndex.getIndex();
            singleRunScenario(index, scenario, notifier)
                    .ifPresent(simulationRunMetadataBuilder::addScenarioResultMetadata);
        }
        notifier.fireTestStarted(getDescription());
    }

    @Override
    public Description getDescription() {
        return this.desc;
    }

    protected Optional<ScenarioResultMetadata> singleRunScenario(int index, Scenario scenario, RunNotifier runNotifier) {
        ScenarioRunnerData scenarioRunnerData = new ScenarioRunnerData();

        Description descriptionForScenario = getDescriptionForScenario(getFileName(), index, scenario);
        runNotifier.fireTestStarted(descriptionForScenario);

        try {
            internalRunScenario(scenario, scenarioRunnerData);
        } catch (ScenarioException e) {
            IndexedScenarioException indexedScenarioException = new IndexedScenarioException(index, e);
            indexedScenarioException.setFileName(fileName);
            runNotifier.fireTestFailure(new Failure(descriptionForScenario, indexedScenarioException));
        } catch (Throwable e) {
            IndexedScenarioException indexedScenarioException = new IndexedScenarioException(index, new StringBuilder().append("Unexpected test error in scenario '")
                    .append(scenario.getDescription()).append("'").toString(), e);
            indexedScenarioException.setFileName(fileName);
            runNotifier.fireTestFailure(new Failure(descriptionForScenario, indexedScenarioException));
        }

        runNotifier.fireTestFinished(descriptionForScenario);

        return scenarioRunnerData.getMetadata();
    }

    protected void internalRunScenario(Scenario scenario, ScenarioRunnerData scenarioRunnerData) {
        ExpressionEvaluator expressionEvaluator = createExpressionEvaluator();
        newRunnerHelper(getSimulationDescriptor()).run(getKieContainer(),
                                                       getSimulationDescriptor(),
                                                       scenario,
                                                       expressionEvaluator,
                                                       getClassLoader(),
                                                       scenarioRunnerData);
    }

    public ExpressionEvaluator createExpressionEvaluator() {
        return expressionEvaluatorFactory.apply(classLoader);
    }

    public Optional<String> getFileName() {
        return Optional.ofNullable(fileName);
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public KieContainer getKieContainer() {
        return kieContainer;
    }

    public SimulationDescriptor getSimulationDescriptor() {
        return simulationDescriptor;
    }

    // FIXME
    public void resetResult() {
        this.simulationRunMetadataBuilder = SimulationRunMetadataBuilder.create();
    }

    // FIXME
    public SimulationRunMetadata getResultMetadata() {
        return this.simulationRunMetadataBuilder.build();
    }

    public static Description getDescriptionForSimulation(Optional<String> filename, Simulation simulation) {
        return getDescriptionForSimulation(filename, simulation.getSimulationDescriptor(), simulation.getScenarioWithIndex());
    }

    public static Description getDescriptionForSimulation(Optional<String> filename, SimulationDescriptor simulationDescriptor, List<ScenarioWithIndex> scenarios) {
        Description suiteDescription = Description.createSuiteDescription("Test Scenarios (Preview) tests");
        scenarios.forEach(scenarioWithIndex -> suiteDescription.addChild(
                getDescriptionForScenario(filename, scenarioWithIndex.getIndex(), scenarioWithIndex.getScenario())));
        return suiteDescription;
    }

    public static Description getDescriptionForScenario(Optional<String> className, int index, Scenario scenario) {
        return Description.createTestDescription(className.orElse(AbstractScenarioRunner.class.getCanonicalName()),
                                                 String.format("#%d: %s", index, scenario.getDescription()));
    }

    public static ScenarioRunnerProvider getSpecificRunnerProvider(SimulationDescriptor simulationDescriptor) {
        if (Type.RULE.equals(simulationDescriptor.getType())) {
            return RuleScenarioRunner::new;
        } else if (Type.DMN.equals(simulationDescriptor.getType())) {
            return DMNScenarioRunner::new;
        } else {
            throw new IllegalArgumentException("Impossible to run simulation of type " + simulationDescriptor.getType());
        }
    }

    protected abstract AbstractRunnerHelper newRunnerHelper(SimulationDescriptor simulationDescriptor);
}
