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

package org.drools.workbench.screens.scenariosimulation.client.commands.actualcommands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridColumn;
import org.drools.workbench.screens.scenariosimulation.model.FactMapping;
import org.drools.workbench.screens.scenariosimulation.model.FactMappingType;
import org.drools.workbench.screens.scenariosimulation.model.ScenarioSimulationModel;
import org.drools.workbench.screens.scenariosimulation.model.typedescriptor.FactModelTree;
import org.junit.Test;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public abstract class AbstractSelectedColumnCommandTest extends AbstractScenarioSimulationCommandTest {

    @Mock
    protected List<GridColumn<?>> gridColumnsMock;
    @Mock
    protected FactModelTree factModelTreeMock;

    @Mock
    protected ScenarioGridColumn createdGridColumnMock;

    public void setup() {
        super.setup();

         when(gridColumnsMock.indexOf(gridColumnMock)).thenReturn(COLUMN_INDEX);

        when(simulationDescriptorMock.getType()).thenReturn(ScenarioSimulationModel.Type.RULE);

        when(factModelTreeMock.getExpandableProperties()).thenReturn(mock(SortedMap.class));
        when(dataObjectFieldsMapMock.get(anyString())).thenReturn(factModelTreeMock);

        scenarioSimulationContextLocal.getStatus().setColumnId(COLUMN_ID);
        scenarioSimulationContextLocal.getStatus().setColumnIndex(COLUMN_INDEX);
    }

    @Test
    public void executeIfSelected() {
        command.execute(scenarioSimulationContextLocal);
        verify((AbstractSelectedColumnCommand) command, times(1)).executeIfSelectedColumn(scenarioSimulationContextLocal, gridColumnMock);
    }

    @Test
    public void executeIfSelected_NoColumn() {
        gridColumnMock = null;
        command.execute(scenarioSimulationContextLocal);
        verify((AbstractSelectedColumnCommand) command, never()).executeIfSelectedColumn(scenarioSimulationContextLocal, gridColumnMock);
    }

    @Test
    public void insertNewColumn_NotToClone() {
        insertNewColumnCommon(COLUMN_INDEX, false);
    }

    @Test
    public void insertNewColumn_ToClone() {
        this.insertNewColumnCommon(COLUMN_INDEX, true);
    }

    protected void insertNewColumnCommon( int columnIndex, boolean cloneInstance) {
        ScenarioGridColumn createdColumn = ((AbstractSelectedColumnCommand) command).insertNewColumn(scenarioSimulationContextLocal, gridColumnMock, columnIndex, cloneInstance);
        String columnGroup = gridColumnMock.getInformationHeaderMetaData().getColumnGroup();
        String originalInstanceTitle = gridColumnMock.getInformationHeaderMetaData().getTitle();
        String instanceTitle = cloneInstance ? originalInstanceTitle : scenarioGridModelMock.getValidPlaceholders().getKey();
        String propertyTitle = scenarioGridModelMock.getValidPlaceholders().getValue();
        final FactMappingType factMappingType = FactMappingType.valueOf(columnGroup.toUpperCase());
        verify(command, times(1)).getScenarioGridColumnLocal(
                                                             anyString(),
                                                             anyString(),
                                                             anyString(),
                                                             eq(columnGroup),
                                                             eq(factMappingType),
                                                             eq(scenarioHeaderTextBoxSingletonDOMElementFactoryTest),
                                                             eq(scenarioCellTextAreaSingletonDOMElementFactoryTest),
                                                             eq(ScenarioSimulationEditorConstants.INSTANCE.defineValidType()));
        if (cloneInstance) {
            verify(createdColumn, times(1)).setFactIdentifier(eq(gridColumnMock.getFactIdentifier()));
        } else {
            verify(createdColumn, never()).setFactIdentifier(any());
        }
        verify(createdColumn, times(1)).setInstanceAssigned(eq(cloneInstance));
        verify(scenarioGridModelMock, times(1)).insertColumn(COLUMN_INDEX, createdColumn);
    }

   /* This test is usable ONLY by <code>SetPropertyCommandTest</code> subclass */
   protected void navigateComplexObject() {
        FactModelTree book = new FactModelTree("Book", "com.Book", new HashMap<>(), new HashMap<>());
        book.addExpandableProperty("author", "Author");
        FactModelTree author = new FactModelTree("Author", "com.Author", new HashMap<>(), new HashMap<>());
        SortedMap<String, FactModelTree> sortedMap = spy(new TreeMap<>());
        sortedMap.put("Book", book);
        sortedMap.put("Author", author);
        List<String> elements = Arrays.asList("Book", "author", "currentlyPrinted");
        FactModelTree target = ((AbstractSelectedColumnCommand) command).navigateComplexObject(book, elements, sortedMap);
        assertEquals(target, author);
        verify(sortedMap, times(1)).get("Author");
    }

    /* This test is usable ONLY by <code>SetPropertyCommandTest</code> subclass */
    protected void getPropertyHeaderTitle() {
        String retrieved = ((AbstractSelectedColumnCommand) command).getPropertyHeaderTitle(scenarioSimulationContextLocal, VALUE, factIdentifierMock);
        assertEquals(VALUE, retrieved);
        List<FactMapping> factMappingList = new ArrayList<>();
        when(simulationDescriptorMock.getFactMappingsByFactName(FACT_IDENTIFIER_NAME)).thenReturn(factMappingList);
        retrieved = ((AbstractSelectedColumnCommand) command).getPropertyHeaderTitle(scenarioSimulationContextLocal, VALUE, factIdentifierMock);
        assertEquals(VALUE, retrieved);
        factMappingList.add(factMappingMock);
        retrieved = ((AbstractSelectedColumnCommand) command).getPropertyHeaderTitle(scenarioSimulationContextLocal, VALUE, factIdentifierMock);
        assertEquals(VALUE, retrieved);
        String EXPRESSION_ALIAS = "EXPRESSION_ALIAS";
        when(factMappingMock.getFullExpression()).thenReturn(VALUE);
        when(factMappingMock.getExpressionAlias()).thenReturn(EXPRESSION_ALIAS);
        retrieved = ((AbstractSelectedColumnCommand) command).getPropertyHeaderTitle(scenarioSimulationContextLocal, VALUE, factIdentifierMock);
        assertEquals(EXPRESSION_ALIAS, retrieved);
    }

    protected void executeKeepDataFalseDMN() {
        scenarioSimulationContextLocal.getStatus().setKeepData(false);
        when(simulationDescriptorMock.getType()).thenReturn(ScenarioSimulationModel.Type.DMN);
        ((AbstractSelectedColumnCommand) command).executeIfSelectedColumn(scenarioSimulationContextLocal, gridColumnMock);
        verify(gridColumnMock, times(1)).setEditableHeaders(eq(false));
        verify(propertyHeaderMetaDataMock, times(1)).setColumnGroup(anyString());
        verify(propertyHeaderMetaDataMock, times(1)).setTitle(VALUE);
        verify(propertyHeaderMetaDataMock, times(1)).setReadOnly(false);
        verify(scenarioGridModelMock, times(1)).updateColumnProperty(anyInt(), isA(ScenarioGridColumn.class), eq(VALUE), eq(VALUE_CLASS_NAME), eq(false));
    }

    protected void executeKeepDataFalseRule() {
        scenarioSimulationContextLocal.getStatus().setKeepData(false);
        when(simulationDescriptorMock.getType()).thenReturn(ScenarioSimulationModel.Type.RULE);
        ((AbstractSelectedColumnCommand) command).executeIfSelectedColumn(scenarioSimulationContextLocal, gridColumnMock);
        verify(gridColumnMock, times(1)).setEditableHeaders(eq(true));
        verify(propertyHeaderMetaDataMock, times(1)).setColumnGroup(anyString());
        verify(propertyHeaderMetaDataMock, times(1)).setTitle(VALUE);
        verify(propertyHeaderMetaDataMock, times(1)).setReadOnly(false);
        verify(scenarioGridModelMock, times(1)).updateColumnProperty(anyInt(), isA(ScenarioGridColumn.class), eq(VALUE), eq(VALUE_CLASS_NAME), eq(false));
    }

    protected void executeKeepDataTrue() {
        scenarioSimulationContextLocal.getStatus().setKeepData(true);
        ((AbstractSelectedColumnCommand) command).executeIfSelectedColumn(scenarioSimulationContextLocal, gridColumnMock);
        verify(propertyHeaderMetaDataMock, times(1)).setColumnGroup(anyString());
        verify(propertyHeaderMetaDataMock, times(1)).setTitle(VALUE);
        verify(propertyHeaderMetaDataMock, times(1)).setReadOnly(false);
        verify(scenarioGridModelMock, times(1)).updateColumnProperty(anyInt(), eq(gridColumnMock), eq(VALUE), eq(VALUE_CLASS_NAME), eq(true));
    }

    protected void executeWithPropertyAsCollection() {
        scenarioSimulationContextLocal.getStatus().setValueClassName(LIST_CLASS_NAME);
        ((AbstractSelectedColumnCommand) command).executeIfSelectedColumn(scenarioSimulationContextLocal, gridColumnMock);
        verify(propertyHeaderMetaDataMock, times(1)).setColumnGroup(anyString());
        verify(propertyHeaderMetaDataMock, times(1)).setTitle(VALUE);
        verify(propertyHeaderMetaDataMock, times(1)).setReadOnly(false);
        verify(scenarioGridModelMock, times(1)).updateColumnProperty(anyInt(), eq(gridColumnMock), eq(VALUE), eq(LIST_CLASS_NAME), anyBoolean());
        List<String> elements = Arrays.asList((FULL_CLASS_NAME).split("\\."));
        verify(((AbstractSelectedColumnCommand) command), times(1)).navigateComplexObject(eq(factModelTreeMock), eq(elements), eq(scenarioSimulationContextLocal.getDataObjectFieldsMap()));
    }

}
