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

package org.drools.workbench.screens.scenariosimulation.client.commands;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class ReloadRightPanelCommandTest extends AbstractScenarioSimulationCommandTest {

    private ReloadRightPanelCommand reloadRightPanelCommand;

    @Before
    public void setup() {
        super.setup();
        /*
        ScenarioSimulationEditorPresenter scenarioSimulationEditorPresenter, boolean disable, boolean openDock
         */
        reloadRightPanelCommand = new ReloadRightPanelCommand();
        scenarioSimulationContext.setScenarioSimulationEditorPresenter(scenarioSimulationEditorPresenterMock);
    }

    @Test
    public void executeDisableOpen() {
//        reloadRightPanelCommand = new ReloadRightPanelCommand(scenarioSimulationEditorPresenterMock, true, true);
        scenarioSimulationContext.setDisable(true);
        scenarioSimulationContext.setOpenDock(true);
        reloadRightPanelCommand.execute(scenarioSimulationContext);
        verify(scenarioSimulationEditorPresenterMock, times(1)).expandToolsDock();
        verify(scenarioSimulationEditorPresenterMock, times(1)).reloadRightPanel(eq(true));
    }

    @Test
    public void executeNotDisableOpen() {
//        reloadRightPanelCommand = new ReloadRightPanelCommand(scenarioSimulationEditorPresenterMock, false, true);
        scenarioSimulationContext.setDisable(false);
        scenarioSimulationContext.setOpenDock(true);
        reloadRightPanelCommand.execute(scenarioSimulationContext);
        verify(scenarioSimulationEditorPresenterMock, times(1)).expandToolsDock();
        verify(scenarioSimulationEditorPresenterMock, times(1)).reloadRightPanel(eq(false));
    }

    @Test
    public void executeDisableNotOpen() {
//        reloadRightPanelCommand = new ReloadRightPanelCommand(scenarioSimulationEditorPresenterMock, true, false);
        scenarioSimulationContext.setDisable(true);
        scenarioSimulationContext.setOpenDock(false);
        reloadRightPanelCommand.execute(scenarioSimulationContext);
        verify(scenarioSimulationEditorPresenterMock, never()).expandToolsDock();
        verify(scenarioSimulationEditorPresenterMock, times(1)).reloadRightPanel(eq(true));
    }
}