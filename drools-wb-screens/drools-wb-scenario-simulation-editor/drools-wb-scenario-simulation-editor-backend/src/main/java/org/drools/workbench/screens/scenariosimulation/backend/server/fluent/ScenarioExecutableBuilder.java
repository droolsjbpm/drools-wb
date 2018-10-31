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

package org.drools.workbench.screens.scenariosimulation.backend.server.fluent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.drools.workbench.screens.scenariosimulation.backend.server.runner.model.ScenarioResult;
import org.drools.workbench.screens.scenariosimulation.model.FactIdentifier;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.api.runtime.ExecutableRunner;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.RequestContext;
import org.kie.api.runtime.builder.ExecutableBuilder;
import org.kie.api.runtime.builder.KieSessionFluent;
import org.kie.api.runtime.conf.ClockTypeOption;

public class ScenarioExecutableBuilder {

    public static String DEFAULT_APPLICATION = "defaultApplication";

    private final KieSessionFluent kieSessionFluent;
    private final ExecutableBuilder executableBuilder;
    private final Map<FactIdentifier, List<FactCheckerHandle>> internalConditions = new HashMap<>();

    private static BiFunction<String, KieContainer, KieContainer> forcePseudoClock = (sessionName, kc) -> {
        KieSessionModel kieSessionModel = kc.getKieSessionModel(sessionName);
        kieSessionModel.setClockType(ClockTypeOption.get("pseudo"));
        return kc;
    };

    private ScenarioExecutableBuilder(KieContainer kieContainer, String name) {
        executableBuilder = ExecutableBuilder.create();

        kieSessionFluent = executableBuilder.newApplicationContext(name)
                .setKieContainer(kieContainer)
                .newSessionCustomized(null, forcePseudoClock);
    }

    private ScenarioExecutableBuilder(KieContainer kieContainer) {
        this(kieContainer, DEFAULT_APPLICATION);
    }

    public static ScenarioExecutableBuilder createBuilder(KieContainer kieContainer, String name) {
        return new ScenarioExecutableBuilder(kieContainer, name);
    }

    public static ScenarioExecutableBuilder createBuilder(KieContainer kieContainer) {
        return new ScenarioExecutableBuilder(kieContainer);
    }

    public void addInternalCondition(Class<?> clazz,
                                     Function<Object, Optional<Object>> checkFunction,
                                     ScenarioResult scenarioResult) {
        internalConditions.computeIfAbsent(scenarioResult.getFactIdentifier(), key -> new ArrayList<>())
                .add(new FactCheckerHandle(clazz, checkFunction, scenarioResult));
    }

    public void insert(Object element) {
        kieSessionFluent.insert(element);
    }

    public RequestContext run() {
        Objects.requireNonNull(executableBuilder, "Executable builder is null, please invoke create(KieContainer, )");

        kieSessionFluent.fireAllRules();
        internalConditions.values()
                .forEach(factToCheck -> kieSessionFluent.addCommand(new ValidateFactCommand(factToCheck)));

        kieSessionFluent.dispose().end();

        return ExecutableRunner.create().execute(executableBuilder.getExecutable());
    }
}
