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
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.CheatSheetPresenter;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.SettingsPresenter;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.TestToolsPresenter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.widgets.client.docks.AuthoringEditorDock;
import org.mockito.Mock;
import org.uberfire.client.workbench.docks.UberfireDock;
import org.uberfire.client.workbench.docks.UberfireDockPosition;

import static org.drools.workbench.screens.scenariosimulation.client.handlers.AbstractScenarioSimulationDocksHandler.SCESIMEDITOR_ID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class AbstractScenarioSimulationDocksHandlerTest {

    @Mock
    private AuthoringEditorDock authoringWorkbenchDocksMock;

    private AbstractScenarioSimulationDocksHandler abstractScenarioSimulationDocksHandlerSpy;

    private enum MANAGED_DOCKS {
        SETTINGS,
        TOOLS,
        CHEATSHEET
    }

    @Before
    public void setup() {
        abstractScenarioSimulationDocksHandlerSpy = spy(new AbstractScenarioSimulationDocksHandler() {

            {
                this.authoringWorkbenchDocks = authoringWorkbenchDocksMock;
            }

            @Override
            public void expandTestResultsDock() {
                //Do nothing
            }
        });
    }

    @Test
    public void provideDocks() {
        final Collection<UberfireDock> docks = abstractScenarioSimulationDocksHandlerSpy.provideDocks("id");
        assertEquals(MANAGED_DOCKS.values().length, docks.size());
        final UberfireDock cheetSheetDock = (UberfireDock) docks.toArray()[MANAGED_DOCKS.CHEATSHEET.ordinal()];
        assertNotNull(cheetSheetDock);
        assertEquals(CheatSheetPresenter.IDENTIFIER, cheetSheetDock.getPlaceRequest().getIdentifier());
        assertEquals(ScenarioSimulationEditorConstants.INSTANCE.scenarioCheatSheet(), cheetSheetDock.getLabel());
        assertEquals(UberfireDockPosition.EAST, cheetSheetDock.getDockPosition());
        assertEquals("FILE_TEXT", cheetSheetDock.getIconType());
        final UberfireDock settingsDock = (UberfireDock) docks.toArray()[MANAGED_DOCKS.SETTINGS.ordinal()];
        assertNotNull(settingsDock);
        assertEquals(SettingsPresenter.IDENTIFIER, settingsDock.getPlaceRequest().getIdentifier());
        assertEquals(ScenarioSimulationEditorConstants.INSTANCE.settings(), settingsDock.getLabel());
        assertEquals(UberfireDockPosition.EAST, settingsDock.getDockPosition());
        assertEquals("SLIDERS", settingsDock.getIconType());
        final UberfireDock testToolsDock = (UberfireDock) docks.toArray()[MANAGED_DOCKS.TOOLS.ordinal()];
        assertNotNull(testToolsDock);
        assertEquals(TestToolsPresenter.IDENTIFIER, testToolsDock.getPlaceRequest().getIdentifier());
        assertEquals(ScenarioSimulationEditorConstants.INSTANCE.testTools(), testToolsDock.getLabel());
        assertEquals(UberfireDockPosition.EAST, testToolsDock.getDockPosition());
        assertEquals("INFO_CIRCLE", testToolsDock.getIconType());    }

    @Test
    public void expandToolsDock() {
        final Collection<UberfireDock> docks = abstractScenarioSimulationDocksHandlerSpy.provideDocks("id");
        final UberfireDock toolsDock = (UberfireDock) docks.toArray()[MANAGED_DOCKS.TOOLS.ordinal()];

        abstractScenarioSimulationDocksHandlerSpy.expandToolsDock();

        verify(authoringWorkbenchDocksMock).expandAuthoringDock(eq(toolsDock));
    }

    @Test
    public void setScesimPath() {
        final Collection<UberfireDock> docks = abstractScenarioSimulationDocksHandlerSpy.provideDocks("id");
        final UberfireDock settingsDock = (UberfireDock) docks.toArray()[MANAGED_DOCKS.SETTINGS.ordinal()];
        final UberfireDock toolsDock = (UberfireDock) docks.toArray()[MANAGED_DOCKS.TOOLS.ordinal()];
        final UberfireDock cheatSheetDock = (UberfireDock) docks.toArray()[MANAGED_DOCKS.CHEATSHEET.ordinal()];
        String TEST_PATH = "TEST_PATH";
        abstractScenarioSimulationDocksHandlerSpy.setScesimEditorId(TEST_PATH);
        assertTrue(settingsDock.getPlaceRequest().getParameters().containsKey(SCESIMEDITOR_ID));
        assertEquals(TEST_PATH, settingsDock.getPlaceRequest().getParameter(SCESIMEDITOR_ID, "null"));
        assertTrue(toolsDock.getPlaceRequest().getParameters().containsKey(SCESIMEDITOR_ID));
        assertEquals(TEST_PATH, toolsDock.getPlaceRequest().getParameter(SCESIMEDITOR_ID, "null"));
        assertTrue(cheatSheetDock.getPlaceRequest().getParameters().containsKey(SCESIMEDITOR_ID));
        assertEquals(TEST_PATH, cheatSheetDock.getPlaceRequest().getParameter(SCESIMEDITOR_ID, "null"));
    }
}