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
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.drools.workbench.screens.scenariosimulation.backend.server.expression.ExpressionEvaluator;
import org.drools.workbench.screens.scenariosimulation.backend.server.runner.model.ScenarioInput;
import org.drools.workbench.screens.scenariosimulation.backend.server.runner.model.ScenarioOutput;
import org.drools.workbench.screens.scenariosimulation.backend.server.runner.model.ScenarioResult;
import org.drools.workbench.screens.scenariosimulation.backend.server.runner.model.ScenarioRunnerData;
import org.drools.workbench.screens.scenariosimulation.model.ExpressionElement;
import org.drools.workbench.screens.scenariosimulation.model.ExpressionIdentifier;
import org.drools.workbench.screens.scenariosimulation.model.FactIdentifier;
import org.drools.workbench.screens.scenariosimulation.model.FactMapping;
import org.drools.workbench.screens.scenariosimulation.model.FactMappingType;
import org.drools.workbench.screens.scenariosimulation.model.FactMappingValue;
import org.drools.workbench.screens.scenariosimulation.model.Scenario;
import org.drools.workbench.screens.scenariosimulation.model.SimulationDescriptor;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.RequestContext;

import static java.util.stream.Collectors.toList;

public abstract class AbstractRunnerHelper {

    public void run(KieContainer kieContainer, SimulationDescriptor simulationDescriptor, Scenario scenario, ExpressionEvaluator expressionEvaluator, ClassLoader classLoader, ScenarioRunnerData scenarioRunnerData) {

        extractGivenValues(simulationDescriptor, scenario.getUnmodifiableFactMappingValues(), classLoader, expressionEvaluator)
                .forEach(scenarioRunnerData::addInput);

        extractExpectedValues(scenario.getUnmodifiableFactMappingValues()).forEach(scenarioRunnerData::addOutput);

        RequestContext requestContext = executeScenario(kieContainer,
                                                        scenarioRunnerData,
                                                        expressionEvaluator,
                                                        simulationDescriptor);

        verifyConditions(simulationDescriptor,
                         scenarioRunnerData,
                         expressionEvaluator,
                         requestContext);

        validateAssertion(scenarioRunnerData.getResultData(),
                          scenario);
    }

    public List<ScenarioInput> extractGivenValues(SimulationDescriptor simulationDescriptor,
                                                  List<FactMappingValue> factMappingValues,
                                                  ClassLoader classLoader,
                                                  ExpressionEvaluator expressionEvaluator) {
        List<ScenarioInput> scenarioInput = new ArrayList<>();

        Map<FactIdentifier, List<FactMappingValue>> groupByFactIdentifier =
                groupByFactIdentifierAndFilter(factMappingValues, FactMappingType.GIVEN);

        for (Map.Entry<FactIdentifier, List<FactMappingValue>> entry : groupByFactIdentifier.entrySet()) {

            FactIdentifier factIdentifier = entry.getKey();

            // for each fact, create a map of path to fields and values to set
            Map<List<String>, Object> paramsForBean = getParamsForBean(simulationDescriptor,
                                                                       factIdentifier,
                                                                       entry.getValue(),
                                                                       classLoader,
                                                                       expressionEvaluator);

            // FIXME to test
            Object bean = getDirectMapping(paramsForBean, entry.getKey().getClassName(), classLoader)
                    .orElse(createObject(factIdentifier.getClassName(), paramsForBean, classLoader));

            scenarioInput.add(new ScenarioInput(factIdentifier, bean));
        }

        return scenarioInput;
    }

    public Optional<Object> getDirectMapping(Map<List<String>, Object> params, String className, ClassLoader classLoader) {
        // if a direct mapping exists (no steps to reach the field) the value itself is the object
        return params.entrySet().stream()
                .filter(e -> e.getKey().isEmpty())
                .map(Map.Entry::getValue)
                .findFirst()
                .map(rawValue -> convertValue(className, rawValue, classLoader));
    }

    protected abstract Object convertValue(String className, Object rawValue, ClassLoader classLoader);

    public List<ScenarioOutput> extractExpectedValues(List<FactMappingValue> factMappingValues) {
        List<ScenarioOutput> scenarioOutput = new ArrayList<>();

        Map<FactIdentifier, List<FactMappingValue>> groupByFactIdentifier =
                groupByFactIdentifierAndFilter(factMappingValues, FactMappingType.EXPECT);

        Set<FactIdentifier> inputFacts = factMappingValues.stream()
                .filter(elem -> FactMappingType.GIVEN.equals(elem.getExpressionIdentifier().getType()))
                .map(FactMappingValue::getFactIdentifier)
                .collect(Collectors.toSet());

        for (Map.Entry<FactIdentifier, List<FactMappingValue>> entry : groupByFactIdentifier.entrySet()) {

            FactIdentifier factIdentifier = entry.getKey();

            scenarioOutput.add(new ScenarioOutput(factIdentifier, entry.getValue(), !inputFacts.contains(factIdentifier)));
        }

        return scenarioOutput;
    }

    public Map<FactIdentifier, List<FactMappingValue>> groupByFactIdentifierAndFilter(List<FactMappingValue> factMappingValues,
                                                                                      FactMappingType type) {
        Map<FactIdentifier, List<FactMappingValue>> groupByFactIdentifier = new HashMap<>();
        for (FactMappingValue factMappingValue : factMappingValues) {
            FactIdentifier factIdentifier = factMappingValue.getFactIdentifier();

            if (FactIdentifier.EMPTY.equals(factIdentifier)) {
                continue;
            }

            ExpressionIdentifier expressionIdentifier = factMappingValue.getExpressionIdentifier();
            if (expressionIdentifier == null) {
                throw new IllegalArgumentException("ExpressionIdentifier malformed");
            }

            if (!Objects.equals(expressionIdentifier.getType(), type)) {
                continue;
            }

            groupByFactIdentifier.computeIfAbsent(factIdentifier, key -> new ArrayList<>())
                    .add(factMappingValue);
        }
        return groupByFactIdentifier;
    }

    public Map<List<String>, Object> getParamsForBean(SimulationDescriptor simulationDescriptor,
                                                      FactIdentifier factIdentifier,
                                                      List<FactMappingValue> factMappingValues,
                                                      ClassLoader classLoader,
                                                      ExpressionEvaluator expressionEvaluator) {
        Map<List<String>, Object> paramsForBean = new HashMap<>();

        for (FactMappingValue factMappingValue : factMappingValues) {
            ExpressionIdentifier expressionIdentifier = factMappingValue.getExpressionIdentifier();

            FactMapping factMapping = simulationDescriptor.getFactMapping(factIdentifier, expressionIdentifier)
                    .orElseThrow(() -> new IllegalStateException("Wrong expression, this should not happen"));

            List<String> pathToField = factMapping.getExpressionElements().stream()
                    .map(ExpressionElement::getStep).collect(toList());

            try {
                Object value = expressionEvaluator.getValueForGiven(factMapping.getClassName(), factMappingValue.getRawValue());
                paramsForBean.put(pathToField, value);
            } catch (IllegalArgumentException e) {
                factMappingValue.setError(true);
                throw new ScenarioException(e.getMessage(), e);
            }
        }

        return paramsForBean;
    }

    public void validateAssertion(List<ScenarioResult> scenarioResults, Scenario scenario) {
        boolean scenarioFailed = false;
        for (ScenarioResult scenarioResult : scenarioResults) {
            if (!scenarioResult.getResult()) {
                scenarioFailed = true;
            }
        }

        if (scenarioFailed) {
            throw new ScenarioException("Scenario '" + scenario.getDescription() + "' failed");
        }
    }

    public abstract RequestContext executeScenario(KieContainer kieContainer,
                                                   ScenarioRunnerData scenarioRunnerData,
                                                   ExpressionEvaluator expressionEvaluator,
                                                   SimulationDescriptor simulationDescriptor);

    public abstract void verifyConditions(SimulationDescriptor simulationDescriptor,
                                          ScenarioRunnerData scenarioRunnerData,
                                          ExpressionEvaluator expressionEvaluator,
                                          RequestContext requestContext);

    public abstract Object createObject(String className, Map<List<String>, Object> params, ClassLoader classLoader);
}
