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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.workbench.screens.scenariosimulation.backend.server.OperatorEvaluator;
import org.drools.workbench.screens.scenariosimulation.backend.server.runner.model.ScenarioInput;
import org.drools.workbench.screens.scenariosimulation.backend.server.runner.model.ScenarioOutput;
import org.drools.workbench.screens.scenariosimulation.backend.server.runner.model.ScenarioResult;
import org.drools.workbench.screens.scenariosimulation.backend.server.util.ScenarioBeanUtil;
import org.drools.workbench.screens.scenariosimulation.model.ExpressionElement;
import org.drools.workbench.screens.scenariosimulation.model.ExpressionIdentifier;
import org.drools.workbench.screens.scenariosimulation.model.FactIdentifier;
import org.drools.workbench.screens.scenariosimulation.model.FactMapping;
import org.drools.workbench.screens.scenariosimulation.model.FactMappingType;
import org.drools.workbench.screens.scenariosimulation.model.FactMappingValue;
import org.drools.workbench.screens.scenariosimulation.model.FactMappingValueOperator;
import org.drools.workbench.screens.scenariosimulation.model.Scenario;
import org.drools.workbench.screens.scenariosimulation.model.SimulationDescriptor;
import org.junit.internal.AssumptionViolatedException;
import org.junit.internal.runners.model.EachTestNotifier;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.RequestContext;

import static java.util.stream.Collectors.toList;
import static org.drools.workbench.screens.scenariosimulation.backend.server.runner.ScenarioExecutableBuilder.createBuilder;
import static org.drools.workbench.screens.scenariosimulation.backend.server.util.ScenarioBeanUtil.fillBean;

public class ScenarioRunnerHelper {

    private ScenarioRunnerHelper() {

    }

    public static List<ScenarioInput> extractGivenValues(SimulationDescriptor simulationDescriptor, List<FactMappingValue> factMappingValues) {
        List<ScenarioInput> scenarioInput = new ArrayList<>();

        Map<FactIdentifier, List<FactMappingValue>> groupByFactIdentifier =
                groupByFactIdentifierAndFilter(factMappingValues, FactMappingType.GIVEN);

        for (Map.Entry<FactIdentifier, List<FactMappingValue>> entry : groupByFactIdentifier.entrySet()) {

            FactIdentifier factIdentifier = entry.getKey();

            // for each fact, create a map of path to fields and values to set filtered by type
            Map<List<String>, Object> paramsForBean = getParamsForBean(simulationDescriptor,
                                                                       factIdentifier,
                                                                       entry.getValue());

            Object bean = fillBean(factIdentifier.getClassName(), paramsForBean);

            scenarioInput.add(new ScenarioInput(factIdentifier, bean));
        }

        return scenarioInput;
    }

    public static List<ScenarioOutput> extractExpectedValues(List<FactMappingValue> factMappingValues) {
        List<ScenarioOutput> scenarioOutput = new ArrayList<>();

        Map<FactIdentifier, List<FactMappingValue>> groupByFactIdentifier =
                groupByFactIdentifierAndFilter(factMappingValues, FactMappingType.EXPECTED);

        for (Map.Entry<FactIdentifier, List<FactMappingValue>> entry : groupByFactIdentifier.entrySet()) {

            FactIdentifier factIdentifier = entry.getKey();

            scenarioOutput.add(new ScenarioOutput(factIdentifier, entry.getValue()));
        }

        return scenarioOutput;
    }

    public static RequestContext executeScenario(KieContainer kieContainer, List<ScenarioInput> given) {
        ScenarioExecutableBuilder scenarioExecutableBuilder = createBuilder(kieContainer);
        given.stream().map(ScenarioInput::getValue).forEach(scenarioExecutableBuilder::insert);
        return scenarioExecutableBuilder.run();
    }

    public static List<ScenarioResult> verifyConditions(SimulationDescriptor simulationDescriptor,
                                                        List<ScenarioInput> inputData,
                                                        List<ScenarioOutput> outputData) {
        List<ScenarioResult> scenarioResult = new ArrayList<>();

        for (ScenarioInput input : inputData) {
            FactIdentifier factIdentifier = input.getFactIdentifier();
            List<ScenarioOutput> assertionOnFact = outputData.stream().filter(elem -> elem.getFactIdentifier().equals(factIdentifier)).collect(toList());

            // check if this fact has something to check
            if (assertionOnFact.size() < 1) {
                continue;
            }

            scenarioResult.addAll(getScenarioResults(simulationDescriptor, assertionOnFact, input));
        }

        return scenarioResult;
    }

    public static List<ScenarioResult> getScenarioResults(SimulationDescriptor simulationDescriptor, List<ScenarioOutput> scenarioOutputsPerFact, ScenarioInput input) {
        FactIdentifier factIdentifier = input.getFactIdentifier();
        Object factInstance = input.getValue();
        List<ScenarioResult> scenarioResults = new ArrayList<>();
        for (ScenarioOutput scenarioOutput : scenarioOutputsPerFact) {
            List<FactMappingValue> expectedResults = scenarioOutput.getExpectedResult();

            for (FactMappingValue expectedResult : expectedResults) {
                FactMappingValueOperator operator = expectedResult.getOperator();

                ExpressionIdentifier expressionIdentifier = expectedResult.getExpressionIdentifier();

                FactMapping factMapping = simulationDescriptor.getFactMapping(factIdentifier, expressionIdentifier)
                        .orElseThrow(() -> new IllegalStateException("Wrong expression, this should not happen"));

                List<String> pathToValue = factMapping.getExpressionElements().stream().map(ExpressionElement::getStep).collect(toList());
                Object expectedValue = expectedResult.getRawValue();
                Object resultValue = ScenarioBeanUtil.navigateToObject(factInstance, pathToValue);

                Boolean conditionResult = new OperatorEvaluator().evaluate(operator, resultValue, expectedValue);

                scenarioResults.add(new ScenarioResult(factIdentifier, expectedResult, conditionResult));
            }
        }
        return scenarioResults;
    }

    public static void validateAssertion(List<ScenarioResult> scenarioResults, Scenario scenario, EachTestNotifier singleNotifier) {
        boolean isScenarioSatisfied = scenarioResults.stream().allMatch(ScenarioResult::getResult);
        if (!isScenarioSatisfied) {
            scenario.getDescription();
            singleNotifier.addFailedAssumption(
                    new AssumptionViolatedException(new StringBuilder().append("Scenario '").append(scenario.getDescription())
                                                            .append("' has wrong assertion").toString()));
        }
    }

    public static Map<List<String>, Object> getParamsForBean(SimulationDescriptor simulationDescriptor, FactIdentifier factIdentifier, List<FactMappingValue> factMappingValues) {
        Map<List<String>, Object> paramsForBean = new HashMap<>();

        for (FactMappingValue factMappingValue : factMappingValues) {
            ExpressionIdentifier expressionIdentifier = factMappingValue.getExpressionIdentifier();

            FactMapping factMapping = simulationDescriptor.getFactMapping(factIdentifier, expressionIdentifier)
                    .orElseThrow(() -> new IllegalStateException("Wrong expression, this should not happen"));

            List<String> pathToField = factMapping.getExpressionElements().stream()
                    .map(ExpressionElement::getStep).collect(toList());

            paramsForBean.put(pathToField, factMappingValue.getRawValue());
        }

        return paramsForBean;
    }

    public static Map<FactIdentifier, List<FactMappingValue>> groupByFactIdentifierAndFilter(List<FactMappingValue> factMappingValues,
                                                                                             FactMappingType type) {
        Map<FactIdentifier, List<FactMappingValue>> groupByFactIdentifier = new HashMap<>();
        for (FactMappingValue factMappingValue : factMappingValues) {

            FactIdentifier factIdentifier = factMappingValue.getFactIdentifier();

            if (!factMappingValue.getExpressionIdentifier().getType().equals(type)) {
                continue;
            }

            groupByFactIdentifier.computeIfAbsent(factIdentifier, key -> new ArrayList<>())
                    .add(factMappingValue);
        }
        return groupByFactIdentifier;
    }
}
