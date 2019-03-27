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

import java.util.Collection;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.workbench.client.docks.AuthoringWorkbenchDocks;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.uberfire.client.workbench.docks.UberfireDock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class ScenarioSimulationDocksHandlerTest {

    @Mock
    AuthoringWorkbenchDocks authoringWorkbenchDocks;

    @InjectMocks
    ScenarioSimulationDocksHandler scenarioSimulationDocksHandler;


    private enum MANAGED_DOCKS {
        SETTINGS(0),
        TOOLS(1),
        CHEATSHEET(2),
        REPORT(3);

        private int index;

        MANAGED_DOCKS(int index) {
            this.index = index;
        }
    }

    @Test
    public void correctAmountOfItems() {
        assertEquals(MANAGED_DOCKS.values().length, scenarioSimulationDocksHandler.provideDocks("identifier").size());
    }

    @Test
    public void expandToolsDock() {
        final Collection<UberfireDock> docks = scenarioSimulationDocksHandler.provideDocks("id");
        final UberfireDock toolsDock = (UberfireDock) docks.toArray()[MANAGED_DOCKS.TOOLS.index];

        scenarioSimulationDocksHandler.expandToolsDock();

        verify(authoringWorkbenchDocks).expandAuthoringDock(toolsDock);
    }

    @Test
    public void expandTestResultsDock() {
        final Collection<UberfireDock> docks = scenarioSimulationDocksHandler.provideDocks("id");
        final UberfireDock reportDock = (UberfireDock) docks.toArray()[MANAGED_DOCKS.REPORT.index];

        scenarioSimulationDocksHandler.expandTestResultsDock();

        verify(authoringWorkbenchDocks).expandAuthoringDock(reportDock);
    }

    @Test
    public void setScesimPath() {
        final Collection<UberfireDock> docks = scenarioSimulationDocksHandler.provideDocks("id");
        final UberfireDock cheatSheetDock = (UberfireDock) docks.toArray()[MANAGED_DOCKS.CHEATSHEET.index];
        final UberfireDock settingsDock = (UberfireDock) docks.toArray()[MANAGED_DOCKS.SETTINGS.index];
        String SCESIM_PATH = "SCESIM_PATH";
        scenarioSimulationDocksHandler.setScesimPath(SCESIM_PATH);
        assertTrue(cheatSheetDock.getPlaceRequest().getParameters().containsKey("scesimpath"));
        assertEquals(SCESIM_PATH, cheatSheetDock.getPlaceRequest().getParameter("scesimpath", "null"));
        assertTrue(settingsDock.getPlaceRequest().getParameters().containsKey("scesimpath"));
        assertEquals(SCESIM_PATH, settingsDock.getPlaceRequest().getParameter("scesimpath", "null"));
    }
}