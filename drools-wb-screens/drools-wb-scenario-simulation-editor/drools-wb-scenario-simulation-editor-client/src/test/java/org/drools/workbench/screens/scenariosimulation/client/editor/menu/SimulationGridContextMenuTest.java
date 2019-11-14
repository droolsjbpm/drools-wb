/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.scenariosimulation.client.editor.menu;

import com.google.gwt.dom.client.LIElement;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.web.bindery.event.shared.Event;
import org.drools.workbench.screens.scenariosimulation.client.enums.GridWidget;
import org.drools.workbench.screens.scenariosimulation.client.events.DeleteRowEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.DuplicateRowEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.InsertRowEvent;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.drools.workbench.screens.scenariosimulation.client.editor.menu.SimulationGridContextMenu.GRIDCONTEXTMENU_DELETE_ROW;
import static org.drools.workbench.screens.scenariosimulation.client.editor.menu.SimulationGridContextMenu.GRIDCONTEXTMENU_DUPLICATE_ROW;
import static org.drools.workbench.screens.scenariosimulation.client.editor.menu.SimulationGridContextMenu.GRIDCONTEXTMENU_INSERT_ROW_ABOVE;
import static org.drools.workbench.screens.scenariosimulation.client.editor.menu.SimulationGridContextMenu.GRIDCONTEXTMENU_INSERT_ROW_BELOW;
import static org.drools.workbench.screens.scenariosimulation.client.editor.menu.SimulationGridContextMenu.GRIDCONTEXTMENU_RUN_SINGLE_SCENARIO;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class SimulationGridContextMenuTest {

    @Mock
    private LIElement insertRowAboveLIElementMock;
    @Mock
    private LIElement insertRowBelowLIElementMock;
    @Mock
    private LIElement duplicateRowLIElementMock;
    @Mock
    private LIElement deleteRowLIElementMock;
    @Mock
    private LIElement runSingleScenarioElementMock;
    @Mock
    private LIElement createdElementMock;
    @Mock
    private LIElement gridTitleElementMock;

    private SimulationGridContextMenu simulationGridContextMenuSpy;

    @Before
    public void setup() {
        simulationGridContextMenuSpy = spy(new SimulationGridContextMenu() {

            {
                this.insertRowAboveLIElement = insertRowAboveLIElementMock;
                this.insertRowBelowLIElement= insertRowBelowLIElementMock;
                this.duplicateRowLIElement = duplicateRowLIElementMock;
                this.deleteRowLIElement = deleteRowLIElementMock;
                this.runSingleScenarioElement = runSingleScenarioElementMock;
                this.gridTitleElement = gridTitleElementMock;
            }

            @Override
            public LIElement addExecutableMenuItem(String id, String label, String i18n) {
                return createdElementMock;
            }

            @Override
            public void mapEvent(LIElement executableMenuItem, Event toBeMapped) {
                //Do nothing
            }

            @Override
            protected void updateExecutableMenuItemAttributes(LIElement toUpdate, String id, String label, String i18n) {
                //Do nothing
            }

            @Override
            public LIElement addMenuItem(String id, String label, String i18n) {
                return createdElementMock;
            }

            @Override
            public void removeMenuItem(LIElement toRemove) {
                //Do nothing
            }

            @Override
            public void show(GridWidget gridWidget, int mx, int my) {
                //Do nothing
            }
        });
    }

    @Test
    public void initMenu() {
        simulationGridContextMenuSpy.initMenu();
        verify(simulationGridContextMenuSpy, times(1)).addExecutableMenuItem(eq(GRIDCONTEXTMENU_INSERT_ROW_ABOVE), eq(ScenarioSimulationEditorConstants.INSTANCE.insertRowAbove()), eq("insertRowAbove"));
        verify(simulationGridContextMenuSpy, times(1)).addExecutableMenuItem(eq(GRIDCONTEXTMENU_INSERT_ROW_BELOW), eq(ScenarioSimulationEditorConstants.INSTANCE.insertRowBelow()), eq("insertRowBelow"));
        verify(simulationGridContextMenuSpy, times(1)).addExecutableMenuItem(eq(GRIDCONTEXTMENU_DUPLICATE_ROW), eq(ScenarioSimulationEditorConstants.INSTANCE.duplicateRow()), eq("duplicateRow"));
        verify(simulationGridContextMenuSpy, times(1)).addExecutableMenuItem(eq(GRIDCONTEXTMENU_DELETE_ROW), eq(ScenarioSimulationEditorConstants.INSTANCE.deleteRow()), eq("deleteRow"));
        verify(simulationGridContextMenuSpy, times(1)).addExecutableMenuItem(eq(GRIDCONTEXTMENU_RUN_SINGLE_SCENARIO), eq(ScenarioSimulationEditorConstants.INSTANCE.runSingleScenario()), eq("runSingleScenario"));
    }

    @Test
    public void show() {
        simulationGridContextMenuSpy.show(GridWidget.SIMULATION, 0, 0, 1);
        verify(simulationGridContextMenuSpy, times(1)).show(eq(GridWidget.SIMULATION), eq(0), eq(0));
        verify(simulationGridContextMenuSpy, times(1)).mapEvent(eq(insertRowAboveLIElementMock), isA(InsertRowEvent.class));
        verify(simulationGridContextMenuSpy, times(1)).mapEvent(eq(insertRowBelowLIElementMock), isA(InsertRowEvent.class));
        verify(simulationGridContextMenuSpy, times(1)).mapEvent(eq(duplicateRowLIElementMock), isA(DuplicateRowEvent.class));
        verify(simulationGridContextMenuSpy, times(1)).mapEvent(eq(deleteRowLIElementMock), isA(DeleteRowEvent.class));
}
}
