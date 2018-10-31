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

import java.util.function.Function;

import org.drools.workbench.screens.scenariosimulation.backend.server.runner.model.ScenarioResult;
import org.drools.workbench.screens.scenariosimulation.backend.server.runner.model.SingleFactValueResult;

public class FactCheckerHandle {

    private final Class<?> clazz;
    private final Function<Object, SingleFactValueResult> checkFuction;
    private final ScenarioResult scenarioResult;

    public FactCheckerHandle(Class<?> clazz, Function<Object, SingleFactValueResult> checkFuction, ScenarioResult scenarioResult) {
        this.clazz = clazz;
        this.checkFuction = checkFuction;
        this.scenarioResult = scenarioResult;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public Function<Object, SingleFactValueResult> getCheckFuction() {
        return checkFuction;
    }

    public ScenarioResult getScenarioResult() {
        return scenarioResult;
    }
}
