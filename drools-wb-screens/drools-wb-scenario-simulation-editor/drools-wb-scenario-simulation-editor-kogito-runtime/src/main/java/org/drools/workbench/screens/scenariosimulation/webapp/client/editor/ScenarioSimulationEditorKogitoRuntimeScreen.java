/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.scenariosimulation.webapp.client.editor;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.uberfire.client.annotations.WorkbenchClientEditor;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

import static org.drools.workbench.screens.scenariosimulation.webapp.client.editor.ScenarioSimulationEditorKogitoRuntimeScreen.IDENTIFIER;

/**
 * It represents the KogitoScreen implementation for Scenario Simulation runtime. In other words, this is the editor
 * entry point. Please note, the <code>IDENTIFIED</code> is the unique key to refer this editor. Please use the same
 * (eg in kogito-tooling project) to include it in external project or when calling .setContent method (refer to readme.md
 * for further information */
@ApplicationScoped
@WorkbenchClientEditor(identifier = IDENTIFIER)
public class ScenarioSimulationEditorKogitoRuntimeScreen extends AbstractScenarioSimulationEditorKogitoScreen {

    protected static final PlaceRequest SCENARIO_SIMULATION_KOGITO_RUNTIME_SCREEN_DEFAULT_REQUEST = new DefaultPlaceRequest(IDENTIFIER);

    protected PlaceManager placeManager;

    public ScenarioSimulationEditorKogitoRuntimeScreen() {
        //CDI proxy
    }

    @Inject
    public ScenarioSimulationEditorKogitoRuntimeScreen(final PlaceManager placeManager) {
        this.placeManager = placeManager;
    }

    @Override
    public PlaceRequest getPlaceRequest() {
        return SCENARIO_SIMULATION_KOGITO_RUNTIME_SCREEN_DEFAULT_REQUEST;
    }

    @OnStartup
    public void onStartup(final PlaceRequest place) {
        scenarioSimulationEditorKogitoWrapper.onStartup(place);
    }

}
