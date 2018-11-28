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

import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.screens.scenariosimulation.client.commands.ScenarioSimulationContext;
import org.drools.workbench.screens.scenariosimulation.client.utils.ScenarioSimulationBuilders;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridColumn;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class SetPropertyHeaderCommandTest extends AbstractScenarioSimulationCommandTest {

    private SetPropertyHeaderCommand setPropertyHeaderCommand;

    @Mock
    private List<GridColumn<?>> mockGridColumns;

    @Before
    public void setup() {
        super.setup();
        when(mockGridColumns.indexOf(gridColumnMock)).thenReturn(COLUMN_INDEX);
        setPropertyHeaderCommand = spy(new SetPropertyHeaderCommand() {

            @Override
            protected ScenarioGridColumn getScenarioGridColumnLocal(ScenarioSimulationBuilders.HeaderBuilder headerBuilder, ScenarioSimulationContext context) {
                return gridColumnMock;
            }
        });

        scenarioSimulationContext.getStatus().setFullPackage(FULL_PACKAGE);
        scenarioSimulationContext.getStatus().setValue(VALUE);
        scenarioSimulationContext.getStatus().setValueClassName(VALUE_CLASS_NAME);
    }

    @Test
    public void executeFalse() {
        scenarioSimulationContext.getStatus().setKeepData(false);
        setPropertyHeaderCommand.execute(scenarioSimulationContext);
        verify(propertyHeaderMetaDataMock, times(1)).setColumnGroup(anyString());
        verify(propertyHeaderMetaDataMock, times(1)).setTitle(VALUE);
        verify(propertyHeaderMetaDataMock, times(1)).setReadOnly(false);
        verify(scenarioGridModelMock, times(1)).updateColumnProperty(anyInt(), isA(ScenarioGridColumn.class), eq(VALUE), eq(VALUE_CLASS_NAME), eq(false));
    }

    @Test
    public void executeTrue() {
        scenarioSimulationContext.getStatus().setKeepData(true);
        setPropertyHeaderCommand.execute(scenarioSimulationContext);
        verify(propertyHeaderMetaDataMock, times(1)).setColumnGroup(anyString());
        verify(propertyHeaderMetaDataMock, times(1)).setTitle(VALUE);
        verify(propertyHeaderMetaDataMock, times(1)).setReadOnly(false);
        verify(scenarioGridModelMock, times(1)).updateColumnProperty(anyInt(), eq(gridColumnMock), eq(VALUE), eq(VALUE_CLASS_NAME), eq(true));
    }
}