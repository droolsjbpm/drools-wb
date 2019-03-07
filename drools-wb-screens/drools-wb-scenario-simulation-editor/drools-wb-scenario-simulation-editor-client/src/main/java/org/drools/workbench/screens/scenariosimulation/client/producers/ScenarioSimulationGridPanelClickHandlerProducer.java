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
package org.drools.workbench.screens.scenariosimulation.client.producers;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.drools.workbench.screens.scenariosimulation.client.handlers.ScenarioSimulationGridPanelClickHandler;
import org.drools.workbench.screens.scenariosimulation.client.menu.ScenarioContextMenuRegistry;

/**
 * <code>@Dependent</code> <i>Producer</i> for a given {@link ScenarioSimulationGridPanelClickHandler}
 */
@Dependent
public class ScenarioSimulationGridPanelClickHandlerProducer {

    @Inject
    protected ScenarioContextMenuRegistry scenarioContextMenuRegistry;

    @Inject
    protected ScenarioSimulationGridPanelClickHandler scenarioSimulationGridPanelClickHandler;

    public ScenarioSimulationGridPanelClickHandler getScenarioSimulationGridPanelClickHandler() {
        scenarioSimulationGridPanelClickHandler.setScenarioContextMenuRegistry(scenarioContextMenuRegistry);
        return scenarioSimulationGridPanelClickHandler;
    }
}
