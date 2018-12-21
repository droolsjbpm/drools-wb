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

package org.drools.workbench.screens.scenariosimulation.client.editor.strategies;

import org.drools.workbench.screens.scenariosimulation.client.models.ScenarioGridModel;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.RightPanelView;
import org.drools.workbench.screens.scenariosimulation.model.ScenarioSimulationModelContent;
import org.drools.workbench.screens.scenariosimulation.model.typedescriptor.FactModelTuple;
import org.drools.workbench.screens.scenariosimulation.service.DMNTypeService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.mocks.CallerMock;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DMNDataManagementStrategyTest {

    @Mock
    protected DMNTypeService dmnTypeServiceMock;

    DMNDataManagementStrategy dmnDataManagementStrategy;

    @Before
    public void init() {
        when(dmnTypeServiceMock.retrieveType(any(), anyString())).thenReturn(mock(FactModelTuple.class));
        dmnDataManagementStrategy = new DMNDataManagementStrategy(new CallerMock<>(dmnTypeServiceMock));
    }

    @Test
    public void populateRightPanel() {
        dmnDataManagementStrategy.populateRightPanel(mock(RightPanelView.Presenter.class), mock(ScenarioGridModel.class));
        verify(dmnTypeServiceMock, times(1)).retrieveType(any(), anyString());
    }

    @Test
    public void manageScenarioSimulationModelContent() {
        ObservablePath observablePathMock = mock(ObservablePath.class);
        ScenarioSimulationModelContent scenarioSimulationModelContentMock = mock(ScenarioSimulationModelContent.class);
        dmnDataManagementStrategy.manageScenarioSimulationModelContent(observablePathMock, scenarioSimulationModelContentMock);
        verify(observablePathMock, times(1)).getOriginal();
        verify(scenarioSimulationModelContentMock, times(1)).getModel();
    }
}