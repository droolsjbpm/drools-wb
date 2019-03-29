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
package org.drools.workbench.screens.scenariosimulation.client.handlers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.CheatSheetPresenter;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.SettingsPresenter;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.TestToolsPresenter;
import org.kie.workbench.common.widgets.client.docks.AbstractWorkbenchDocksHandler;
import org.kie.workbench.common.workbench.client.docks.AuthoringWorkbenchDocks;
import org.kie.workbench.common.workbench.client.resources.i18n.DefaultWorkbenchConstants;
import org.uberfire.client.workbench.docks.UberfireDock;
import org.uberfire.client.workbench.docks.UberfireDockPosition;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

@ApplicationScoped
public class ScenarioSimulationDocksHandler
        extends AbstractWorkbenchDocksHandler {

    public static final String SCESIM_PATH = "scesimpath";

    @Inject
    private AuthoringWorkbenchDocks authoringWorkbenchDocks;

    private UberfireDock settingsDock;
    private UberfireDock toolsDock;
    private UberfireDock cheatSheetDock;
    private UberfireDock reportDock;

    @Override
    public Collection<UberfireDock> provideDocks(final String perspectiveIdentifier) {
        List<UberfireDock> result = new ArrayList<>();
        settingsDock = new UberfireDock(UberfireDockPosition.EAST,
                                     "SLIDERS",
                                     new DefaultPlaceRequest(SettingsPresenter.IDENTIFIER),
                                     perspectiveIdentifier);
        result.add(settingsDock.withSize(450).withLabel(ScenarioSimulationEditorConstants.INSTANCE.settings()));
        toolsDock = new UberfireDock(UberfireDockPosition.EAST,
                                     "INFO_CIRCLE",
                                     new DefaultPlaceRequest(TestToolsPresenter.IDENTIFIER),
                                     perspectiveIdentifier);
        result.add(toolsDock.withSize(450).withLabel(ScenarioSimulationEditorConstants.INSTANCE.testTools()));
        cheatSheetDock = new UberfireDock(UberfireDockPosition.EAST,
                                          "FILE_TEXT",
                                          new DefaultPlaceRequest(CheatSheetPresenter.IDENTIFIER),
                                          perspectiveIdentifier);
        result.add(cheatSheetDock.withSize(450).withLabel(ScenarioSimulationEditorConstants.INSTANCE.scenarioCheatSheet()));
        reportDock = new UberfireDock(UberfireDockPosition.EAST,
                                      "PLAY_CIRCLE",
                                      new DefaultPlaceRequest("org.kie.guvnor.TestResults"),
                                      perspectiveIdentifier);
        result.add(reportDock.withSize(450).withLabel(DefaultWorkbenchConstants.INSTANCE.TestReport()));

        return result;
    }

    public void addDocks() {
        refreshDocks(true,
                     false);
    }

    public void removeDocks() {
        refreshDocks(true,
                     true);
    }

    public void expandToolsDock() {
        authoringWorkbenchDocks.expandAuthoringDock(toolsDock);
    }

    public void expandTestResultsDock() {
        authoringWorkbenchDocks.expandAuthoringDock(reportDock);
    }

    public void setScesimPath(String scesimPath) {
        cheatSheetDock.getPlaceRequest().addParameter(SCESIM_PATH, scesimPath);
        settingsDock.getPlaceRequest().addParameter(SCESIM_PATH, scesimPath);
    }
}
