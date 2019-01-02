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
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import org.drools.workbench.screens.scenariosimulation.backend.server.expression.ExpressionEvaluator;
import org.drools.workbench.screens.scenariosimulation.backend.server.fluent.RuleScenarioExecutableBuilder;
import org.drools.workbench.screens.scenariosimulation.backend.server.runner.model.ResultWrapper;
import org.drools.workbench.screens.scenariosimulation.backend.server.runner.model.ScenarioExpect;
import org.drools.workbench.screens.scenariosimulation.backend.server.runner.model.ScenarioGiven;
import org.drools.workbench.screens.scenariosimulation.backend.server.runner.model.ScenarioResult;
import org.drools.workbench.screens.scenariosimulation.backend.server.runner.model.ScenarioRunnerData;
import org.drools.workbench.screens.scenariosimulation.backend.server.util.ScenarioBeanUtil;
import org.drools.workbench.screens.scenariosimulation.backend.server.util.ScenarioBeanWrapper;
import org.drools.workbench.screens.scenariosimulation.model.ExpressionElement;
import org.drools.workbench.screens.scenariosimulation.model.ExpressionIdentifier;
import org.drools.workbench.screens.scenariosimulation.model.FactIdentifier;
import org.drools.workbench.screens.scenariosimulation.model.FactMapping;
import org.drools.workbench.screens.scenariosimulation.model.FactMappingValue;
import org.drools.workbench.screens.scenariosimulation.model.ScenarioSimulationModel;
import org.drools.workbench.screens.scenariosimulation.model.SimulationDescriptor;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.RequestContext;

import static java.util.stream.Collectors.toList;
import static org.drools.workbench.screens.scenariosimulation.backend.server.fluent.RuleScenarioExecutableBuilder.createBuilder;
import static org.drools.workbench.screens.scenariosimulation.backend.server.runner.model.ResultWrapper.createErrorResult;
import static org.drools.workbench.screens.scenariosimulation.backend.server.runner.model.ResultWrapper.createResult;
import static org.drools.workbench.screens.scenariosimulation.backend.server.util.ScenarioBeanUtil.fillBean;

public class RuleScenarioRunnerHelper extends AbstractRunnerHelper {

    private final SimulationDescriptor simulationDescriptor;

    public RuleScenarioRunnerHelper(SimulationDescriptor simulationDescriptor) {
        this.simulationDescriptor = simulationDescriptor;
    }

    @Override
    public RequestContext executeScenario(KieContainer kieContainer,
                                          ScenarioRunnerData scenarioRunnerData,
                                          ExpressionEvaluator expressionEvaluator,
                                          SimulationDescriptor simulationDescriptor) {
        if (!ScenarioSimulationModel.Type.RULE.equals(simulationDescriptor.getType())) {
            throw new ScenarioException("Impossible to run a not-RULE simulation with RULE runner");
        }
        RuleScenarioExecutableBuilder ruleScenarioExecutableBuilder = createBuilder(kieContainer);
        scenarioRunnerData.getGivens().stream().map(ScenarioGiven::getValue).forEach(ruleScenarioExecutableBuilder::insert);
        // all new facts should be verified internally to the working memory because
        scenarioRunnerData.getExpects().stream()
                .filter(ScenarioExpect::isNewFact)
                .flatMap(output -> output.getExpectedResult().stream()
                        .map(factMappingValue -> new ScenarioResult(output.getFactIdentifier(), factMappingValue)))
                .forEach(scenarioResult -> {
                    Class<?> clazz = ScenarioBeanUtil.loadClass(scenarioResult.getFactIdentifier().getClassName(), kieContainer.getClassLoader());
                    scenarioRunnerData.addResult(scenarioResult);
                    ruleScenarioExecutableBuilder.addInternalCondition(clazz,
                                                                       createExtractorFunction(expressionEvaluator, scenarioResult.getFactMappingValue(), simulationDescriptor),
                                                                       scenarioResult);
                });

        return ruleScenarioExecutableBuilder.run();
    }

    @Override
    public void verifyConditions(SimulationDescriptor simulationDescriptor,
                                 ScenarioRunnerData scenarioRunnerData,
                                 ExpressionEvaluator expressionEvaluator,
                                 RequestContext requestContext) {

        for (ScenarioGiven input : scenarioRunnerData.getGivens()) {
            FactIdentifier factIdentifier = input.getFactIdentifier();
            List<ScenarioExpect> assertionOnFact = scenarioRunnerData.getExpects().stream()
                    .filter(elem -> !elem.isNewFact())
                    .filter(elem -> Objects.equals(elem.getFactIdentifier(), factIdentifier)).collect(toList());

            // check if this fact has something to check
            if (assertionOnFact.size() < 1) {
                continue;
            }

            getScenarioResultsFromGivenFacts(simulationDescriptor, assertionOnFact, input, expressionEvaluator).forEach(scenarioRunnerData::addResult);
        }
    }

    protected List<ScenarioResult> getScenarioResultsFromGivenFacts(SimulationDescriptor simulationDescriptor,
                                                                    List<ScenarioExpect> scenarioOutputsPerFact,
                                                                    ScenarioGiven input,
                                                                    ExpressionEvaluator expressionEvaluator) {
        FactIdentifier factIdentifier = input.getFactIdentifier();
        Object factInstance = input.getValue();
        List<ScenarioResult> scenarioResults = new ArrayList<>();
        for (ScenarioExpect scenarioExpect : scenarioOutputsPerFact) {
            if (scenarioExpect.isNewFact()) {
                continue;
            }

            for (FactMappingValue expectedResult : scenarioExpect.getExpectedResult()) {

                ResultWrapper resultValue = createExtractorFunction(expressionEvaluator, expectedResult, simulationDescriptor).apply(factInstance);

                expectedResult.setError(!resultValue.isSatisfied());

                scenarioResults.add(new ScenarioResult(factIdentifier, expectedResult, resultValue.getResult()).setResult(resultValue.isSatisfied()));
            }
        }
        return scenarioResults;
    }

    protected Function<Object, ResultWrapper> createExtractorFunction(ExpressionEvaluator expressionEvaluator,
                                                                      FactMappingValue expectedResult,
                                                                      SimulationDescriptor simulationDescriptor) {
        return objectToCheck -> {

            ExpressionIdentifier expressionIdentifier = expectedResult.getExpressionIdentifier();

            FactMapping factMapping = simulationDescriptor.getFactMapping(expectedResult.getFactIdentifier(), expressionIdentifier)
                    .orElseThrow(() -> new IllegalStateException("Wrong expression, this should not happen"));

            List<String> pathToValue = factMapping.getExpressionElements().stream().map(ExpressionElement::getStep).collect(toList());
            ScenarioBeanWrapper<?> scenarioBeanWrapper = ScenarioBeanUtil.navigateToObject(objectToCheck, pathToValue, false);
            Object resultValue = scenarioBeanWrapper.getBean();

            try {
                return expressionEvaluator.evaluate(expectedResult.getRawValue(), resultValue, scenarioBeanWrapper.getBeanClass()) ?
                        createResult(resultValue) :
                        createErrorResult();
            } catch (Exception e) {
                expectedResult.setError(true);
                throw new ScenarioException(e.getMessage(), e);
            }
        };
    }

    @Override
    public Object createObject(String className, Map<List<String>, Object> params, ClassLoader classLoader) {
        return fillBean(className, params, classLoader);
    }
}
