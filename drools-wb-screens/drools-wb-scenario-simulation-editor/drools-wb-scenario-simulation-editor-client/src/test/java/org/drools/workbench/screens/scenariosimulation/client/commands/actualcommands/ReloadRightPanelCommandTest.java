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

package org.drools.workbench.screens.scenariosimulation.client.commands.actualcommands;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class ReloadRightPanelCommandTest extends AbstractScenarioSimulationCommandTest {

    @Before
    public void setup() {
        super.setup();
        command = spy(new ReloadRightPanelCommand());
        scenarioSimulationContextLocal.setScenarioSimulationEditorPresenter(scenarioSimulationEditorPresenterMock);
        assertFalse(command.isUndoable());
    }

    @Test
    public void executeDisableOpen() {
        scenarioSimulationContextLocal.getStatus().setDisable(true);
        scenarioSimulationContextLocal.getStatus().setOpenDock(true);
        command.execute(scenarioSimulationContextLocal);
        verify(scenarioSimulationEditorPresenterMock, times(1)).expandToolsDock();
        verify(scenarioSimulationEditorPresenterMock, times(1)).reloadRightPanel(eq(true));
    }

    @Test
    public void executeNotDisableOpen() {
        scenarioSimulationContextLocal.getStatus().setDisable(false);
        scenarioSimulationContextLocal.getStatus().setOpenDock(true);
        command.execute(scenarioSimulationContextLocal);
        verify(scenarioSimulationEditorPresenterMock, times(1)).expandToolsDock();
        verify(scenarioSimulationEditorPresenterMock, times(1)).reloadRightPanel(eq(false));
    }

    @Test
    public void executeDisableNotOpen() {
        scenarioSimulationContextLocal.getStatus().setDisable(true);
        scenarioSimulationContextLocal.getStatus().setOpenDock(false);
        command.execute(scenarioSimulationContextLocal);
        verify(scenarioSimulationEditorPresenterMock, never()).expandToolsDock();
        verify(scenarioSimulationEditorPresenterMock, times(1)).reloadRightPanel(eq(true));
    }
}