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

import com.google.gwt.event.shared.EventBus;
import org.drools.workbench.screens.scenariosimulation.client.AbstractScenarioSimulationTest;
import org.drools.workbench.screens.scenariosimulation.client.editor.ScenarioSimulationEditorPresenter;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.RightPanelPresenter;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public abstract class AbstractScenarioSimulationCommandTest extends AbstractScenarioSimulationTest {

    @Mock
    protected ScenarioSimulationEditorPresenter scenarioSimulationEditorPresenterMock;

    @Mock
    protected RightPanelPresenter rightPanelPresenterMock;

    @Mock
    protected EventBus eventBusMock;

    protected AbstractScenarioSimulationCommand command;


    @Before
    public void setup() {
        super.setup();

    }


    /*

      @Override
    public CommandResult<ScenarioSimulationViolation> undo(ScenarioSimulationContext context) throws UnsupportedOperationException {
        if (!undoable || restorableStatus == null) {
            String message = !undoable ? this.getClass().getSimpleName() + " is not undoable" : "restorableStatus status is null";
            throw new UnsupportedOperationException(message);
        }
        return setCurrentContext(context);
    }

    */

    @Test
    public void undoOnUndoable() {
        if (command.isUndoable()) {
            command.undo(scenarioSimulationContext);
            verify(command, times(1)).setCurrentContext(eq(scenarioSimulationContext));
        }
    }

    @Test(expected = UnsupportedOperationException.class)
    public void undoOnNotUndoable() {
        if (!command.isUndoable()) {
            command.undo(scenarioSimulationContext);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Test
    public void redoOnRedoable() {
        if (command.isUndoable()) {
            command.redo(scenarioSimulationContext);
            verify(command, times(1)).setCurrentContext(eq(scenarioSimulationContext));
        }
    }

    @Test(expected = UnsupportedOperationException.class)
    public void redoOnNotUndoable() {
        if (!command.isUndoable()) {
            command.redo(scenarioSimulationContext);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Test
    public void execute() {
        command.execute(scenarioSimulationContext);
        verify(command, times(1)).internalExecute(eq(scenarioSimulationContext));
    }

    @Test
    public void setCurrentContext() {
        if (command.isUndoable()) {
            command.setCurrentContext(scenarioSimulationContext);
            verify(scenarioSimulationViewMock, times(1)).setContent(eq(simulationMock));
            verify(scenarioSimulationModelMock, times(1)).setSimulation(eq(simulationMock));
        }
    }

    @Test
    public void commonExecution() {
        if (command.isUndoable()) {
            command.commonExecution(scenarioSimulationContext);
            verify(scenarioGridPanelMock, times(1)).onResize();
            verify(scenarioGridPanelMock, times(1)).select();
        }
    }

}