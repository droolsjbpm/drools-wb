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

package org.drools.workbench.screens.scenariosimulation.client.utils;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridColumn;
import org.drools.workbench.screens.scenariosimulation.model.ExpressionIdentifier;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(GwtMockitoTestRunner.class)
public class ScenarioSimulationUtilsTest extends AbstractUtilsTest {

    @Test
    public void getScenarioGridColumn1() {
        final ScenarioGridColumn retrieved = ScenarioSimulationUtils.getScenarioGridColumn(COLUMN_INSTANCE_TITLE_FIRST, COLUMN_PROPERTY_TITLE_FIRST, COLUMN_ID, COLUMN_GROUP_FIRST, factMappingType, mockScenarioGridPanel, mockScenarioGridLayer);
        assertNotNull(retrieved);
    }

    @Test
    public void getScenarioGridColumn2() {
        final ScenarioGridColumn retrieved = ScenarioSimulationUtils.getScenarioGridColumn(COLUMN_INSTANCE_TITLE_FIRST, COLUMN_PROPERTY_TITLE_FIRST, COLUMN_ID, COLUMN_GROUP_FIRST, factMappingType, mockScenarioGridPanel, mockScenarioGridLayer, PLACEHOLDER);
        assertNotNull(retrieved);
    }

    @Test
    public void getScenarioGridColumn3() {
        final ScenarioGridColumn retrieved = ScenarioSimulationUtils.getScenarioGridColumn(headerBuilderMock, mockScenarioGridPanel, mockScenarioGridLayer);
        assertNotNull(retrieved);
    }

    @Test
    public void getScenarioGridColumn4() {
        final ScenarioGridColumn retrieved = ScenarioSimulationUtils.getScenarioGridColumn(headerBuilderMock, mockScenarioGridPanel, mockScenarioGridLayer, false, PLACEHOLDER);
        assertNotNull(retrieved);
    }

    @Test
    public void getScenarioGridColumnBuilder() {
        final ScenarioSimulationBuilders.ScenarioGridColumnBuilder retrieved = ScenarioSimulationUtils.getScenarioGridColumnBuilder(scenarioCellTextBoxSingletonDOMElementFactoryMock, headerBuilderMock, PLACEHOLDER);
        assertNotNull(retrieved);
    }

    @Test
    public void getHeaderBuilder() {
        final ScenarioSimulationBuilders.HeaderBuilder retrieved = ScenarioSimulationUtils.getHeaderBuilder(COLUMN_INSTANCE_TITLE_FIRST, COLUMN_PROPERTY_TITLE_FIRST, COLUMN_ID, COLUMN_GROUP_FIRST, factMappingType, scenarioHeaderTextBoxSingletonDOMElementFactoryMock);
        assertNotNull(retrieved);
    }

    @Test
    public void getColumnWidth() {
        assertEquals(70, ScenarioSimulationUtils.getColumnWidth(ExpressionIdentifier.NAME.Index.name()), 0);
        assertEquals(300, ScenarioSimulationUtils.getColumnWidth(ExpressionIdentifier.NAME.Description.name()), 0);
        assertEquals(200, ScenarioSimulationUtils.getColumnWidth(ExpressionIdentifier.NAME.Given.name()), 0);
        assertEquals(200, ScenarioSimulationUtils.getColumnWidth(ExpressionIdentifier.NAME.Expected.name()), 0);
        assertEquals(200, ScenarioSimulationUtils.getColumnWidth(ExpressionIdentifier.NAME.Other.name()), 0);
    }
}