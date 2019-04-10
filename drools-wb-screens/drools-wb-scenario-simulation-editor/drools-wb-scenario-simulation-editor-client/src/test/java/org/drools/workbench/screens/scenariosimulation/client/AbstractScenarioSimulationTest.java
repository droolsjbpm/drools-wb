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
package org.drools.workbench.screens.scenariosimulation.client;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.stream.IntStream;

import javax.enterprise.event.Event;

import com.ait.lienzo.client.core.types.Point2D;
import com.google.gwt.event.shared.EventBus;
import org.drools.workbench.screens.scenariosimulation.client.commands.ScenarioCommandManager;
import org.drools.workbench.screens.scenariosimulation.client.commands.ScenarioCommandRegistry;
import org.drools.workbench.screens.scenariosimulation.client.commands.ScenarioSimulationContext;
import org.drools.workbench.screens.scenariosimulation.client.commands.ScenarioSimulationViolation;
import org.drools.workbench.screens.scenariosimulation.client.commands.actualcommands.AppendRowCommand;
import org.drools.workbench.screens.scenariosimulation.client.editor.ScenarioSimulationEditorPresenter;
import org.drools.workbench.screens.scenariosimulation.client.editor.ScenarioSimulationView;
import org.drools.workbench.screens.scenariosimulation.client.editor.strategies.DataManagementStrategy;
import org.drools.workbench.screens.scenariosimulation.client.factories.CollectionEditorSingletonDOMElementFactory;
import org.drools.workbench.screens.scenariosimulation.client.factories.ScenarioCellTextAreaSingletonDOMElementFactory;
import org.drools.workbench.screens.scenariosimulation.client.factories.ScenarioHeaderTextBoxSingletonDOMElementFactory;
import org.drools.workbench.screens.scenariosimulation.client.metadata.ScenarioHeaderMetaData;
import org.drools.workbench.screens.scenariosimulation.client.models.ScenarioGridModel;
import org.drools.workbench.screens.scenariosimulation.client.utils.ViewsProvider;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGrid;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridColumn;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridLayer;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridPanel;
import org.drools.workbench.screens.scenariosimulation.model.ExpressionIdentifier;
import org.drools.workbench.screens.scenariosimulation.model.FactIdentifier;
import org.drools.workbench.screens.scenariosimulation.model.FactMapping;
import org.drools.workbench.screens.scenariosimulation.model.FactMappingType;
import org.drools.workbench.screens.scenariosimulation.model.FactMappingValue;
import org.drools.workbench.screens.scenariosimulation.model.Scenario;
import org.drools.workbench.screens.scenariosimulation.model.ScenarioSimulationModel;
import org.drools.workbench.screens.scenariosimulation.model.Simulation;
import org.drools.workbench.screens.scenariosimulation.model.SimulationDescriptor;
import org.drools.workbench.screens.scenariosimulation.model.typedescriptor.FactModelTree;
import org.junit.Before;
import org.kie.workbench.common.command.client.CommandResult;
import org.kie.workbench.common.command.client.CommandResultBuilder;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.GridRow;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.workbench.events.NotificationEvent;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public abstract class AbstractScenarioSimulationTest {

    protected ScenarioGridModel scenarioGridModelMock;
    @Mock
    protected Simulation simulationMock;
    @Mock
    protected Simulation clonedSimulationMock;
    @Mock
    protected SimulationDescriptor simulationDescriptorMock;
    @Mock
    protected ScenarioGridColumn gridColumnMock;
    @Mock
    protected List<GridRow> rowsMock;
    @Mock
    protected ScenarioGridPanel scenarioGridPanelMock;
    @Mock
    protected EventBus eventBusMock;
    @Mock
    protected ScenarioSimulationEditorPresenter scenarioSimulationEditorPresenterMock;
    @Mock
    protected ScenarioSimulationView scenarioSimulationViewMock;
    @Mock
    protected List<GridColumn.HeaderMetaData> headerMetaDatasMock;

    @Mock
    protected ScenarioHeaderMetaData informationHeaderMetaDataMock;
    @Mock
    protected ScenarioHeaderMetaData propertyHeaderMetaDataMock;

    protected Event<NotificationEvent> notificationEvent = new EventSourceMock<NotificationEvent>() {
        @Override
        public void fire(final NotificationEvent event) {
            //Do nothing. Default implementation throws a RuntimeException
        }
    };

    @Mock
    protected ScenarioGridLayer scenarioGridLayerMock;

    @Mock
    protected ScenarioGrid scenarioGridMock;

    @Mock
    protected ScenarioCommandRegistry scenarioCommandRegistryMock;
    @Mock
    protected ScenarioCommandManager scenarioCommandManagerMock;
    @Mock
    protected ScenarioSimulationModel scenarioSimulationModelMock;
    @Mock
    protected Map<Integer, Scenario> scenarioMapMock;
    @Mock
    protected DataManagementStrategy dataManagementStrategyMock;
    @Mock
    protected SortedMap<String, FactModelTree> dataObjectFieldsMapMock;

    @Mock
    protected ViewsProvider viewsProviderMock;

    @Mock
    protected FactMapping factMappingMock;

    @Mock
    protected FactIdentifier factIdentifierMock;

    @Mock
    protected FactMappingValue factMappingValueMock;

    protected List<FactMappingValue> factMappingValuesLocal = new ArrayList<>();

    protected ScenarioSimulationContext scenarioSimulationContextLocal;
    protected AppendRowCommand appendRowCommandMock;
    protected CollectionEditorSingletonDOMElementFactory collectionEditorSingletonDOMElementFactoryTest;
    protected ScenarioCellTextAreaSingletonDOMElementFactory scenarioCellTextAreaSingletonDOMElementFactoryTest;
    protected ScenarioHeaderTextBoxSingletonDOMElementFactory scenarioHeaderTextBoxSingletonDOMElementFactoryTest;

    protected final int ROW_INDEX = 2;
    protected final int COLUMN_INDEX = 3;
    protected final int COLUMN_NUMBER = COLUMN_INDEX + 1;

    protected final int FIRST_INDEX_LEFT = 2;
    protected final int FIRST_INDEX_RIGHT = 4;
    protected final String COLUMN_ID = "COLUMN ID";

    protected final String COLUMN_GROUP = FactMappingType.EXPECT.name();

    protected final String FULL_PACKAGE = "test.scesim";

    protected final String VALUE = "value";

    protected final String FULL_CLASS_NAME = FULL_PACKAGE + ".testclass";

    protected final String VALUE_CLASS_NAME = String.class.getName();

    protected final String LIST_CLASS_NAME = List.class.getName();

    protected final String FACT_IDENTIFIER_NAME = "FACT_IDENTIFIER_NAME";

    protected static final String FACT_ALIAS = "FACT_ALIAS" ;

    protected final String GRID_PROPERTY_TITLE = "GRID_PROPERTY_TITLE";
    protected final String GRID_COLUMN_GROUP = "GIVEN";
    protected final String GRID_COLUMN_ID = "GRID_COLUMN_ID";

    protected final Set<FactIdentifier> factIdentifierSet = new HashSet<>();
    protected final List<FactMapping> factMappingLocal = new ArrayList<>();
    protected final FactMappingType factMappingType = FactMappingType.valueOf(COLUMN_GROUP);
    protected List<GridColumn<?>> gridColumns = new ArrayList<>();

    @Before
    public void setup() {
        when(simulationMock.getSimulationDescriptor()).thenReturn(simulationDescriptorMock);
        when(simulationMock.getScenarioMap()).thenReturn(scenarioMapMock);
        GridData.Range range = new GridData.Range(FIRST_INDEX_LEFT, FIRST_INDEX_RIGHT - 1);
        collectionEditorSingletonDOMElementFactoryTest = new CollectionEditorSingletonDOMElementFactory(scenarioGridPanelMock,
                                                                                                        scenarioGridLayerMock,
                                                                                                        scenarioGridMock,
                                                                                                        scenarioSimulationContextLocal, viewsProviderMock);
        scenarioCellTextAreaSingletonDOMElementFactoryTest = new ScenarioCellTextAreaSingletonDOMElementFactory(scenarioGridPanelMock,
                                                                                                                scenarioGridLayerMock,
                                                                                                                scenarioGridMock);
        scenarioHeaderTextBoxSingletonDOMElementFactoryTest = new ScenarioHeaderTextBoxSingletonDOMElementFactory(scenarioGridPanelMock,
                                                                                                                  scenarioGridLayerMock,
                                                                                                                  scenarioGridMock);

        scenarioGridModelMock = spy(new ScenarioGridModel(false) {
            {
                this.simulation = simulationMock;
                this.columns = gridColumns;
                this.rows = rowsMock;
                this.collectionEditorSingletonDOMElementFactory = collectionEditorSingletonDOMElementFactoryTest;
                this.scenarioCellTextAreaSingletonDOMElementFactory = scenarioCellTextAreaSingletonDOMElementFactoryTest;
                this.scenarioHeaderTextBoxSingletonDOMElementFactory = scenarioHeaderTextBoxSingletonDOMElementFactoryTest;
                this.eventBus = eventBusMock;
            }

            @Override
            protected void commonAddColumn(int index, GridColumn<?> column) {
                //
            }

            @Override
            protected void commonAddColumn(final int index, final GridColumn<?> column, ExpressionIdentifier ei) {
                //
            }

            @Override
            protected void commonAddRow(int rowIndex) {
                //
            }

            @Override
            public List<GridColumn<?>> getColumns() {
                return columns;
            }

            @Override
            public Range getInstanceLimits(int columnIndex) {
                return range;
            }

            @Override
            public int getFirstIndexLeftOfGroup(String groupName) {
                return FIRST_INDEX_LEFT;
            }

            @Override
            public int getFirstIndexRightOfGroup(String groupName) {
                return FIRST_INDEX_RIGHT;
            }

            @Override
            public GridColumn<?> getSelectedColumn() {
                return gridColumnMock;
            }

            @Override
            public void deleteColumn(final GridColumn<?> column) {
                //
            }

            @Override
            public Range deleteRow(int rowIndex) {
                return range;
            }

            @Override
            public void insertRowGridOnly(final int rowIndex,
                                          final GridRow row, final Scenario scenario) {
                //
            }

            @Override
            public void insertRow(int rowIndex, GridRow row) {

            }

            @Override
            public List<GridRow> getRows() {
                return rowsMock;
            }

            @Override
            public Range setCellValue(int rowIndex, int columnIndex, GridCellValue<?> value) {
                return range;
            }

            @Override
            public boolean validateInstanceHeaderUpdate(String value, int columnIndex, boolean isADataType) {
                return true;
            }

            @Override
            public boolean validatePropertyHeaderUpdate(String value, int columnIndex, boolean isPropertyType) {
                return true;
            }
        });
        when(scenarioGridMock.getEventBus()).thenReturn(eventBusMock);
        when(scenarioGridMock.getModel()).thenReturn(scenarioGridModelMock);
        final Point2D computedLocation = mock(Point2D.class);
        when(computedLocation.getX()).thenReturn(0.0);
        when(computedLocation.getY()).thenReturn(0.0);
        when(scenarioGridMock.getComputedLocation()).thenReturn(computedLocation);

        when(scenarioGridLayerMock.getScenarioGrid()).thenReturn(scenarioGridMock);

        when(scenarioGridPanelMock.getScenarioGridLayer()).thenReturn(scenarioGridLayerMock);
        when(scenarioGridPanelMock.getScenarioGrid()).thenReturn(scenarioGridMock);

        scenarioSimulationContextLocal = new ScenarioSimulationContext(scenarioGridPanelMock);
        scenarioSimulationContextLocal.setScenarioSimulationEditorPresenter(scenarioSimulationEditorPresenterMock);
        scenarioSimulationContextLocal.getStatus().setSimulation(simulationMock);
        scenarioSimulationContextLocal.setScenarioSimulationEditorPresenter(scenarioSimulationEditorPresenterMock);
        scenarioSimulationContextLocal.setDataObjectFieldsMap(dataObjectFieldsMapMock);
        when(scenarioSimulationEditorPresenterMock.getView()).thenReturn(scenarioSimulationViewMock);
        when(scenarioSimulationEditorPresenterMock.getModel()).thenReturn(scenarioSimulationModelMock);
        scenarioSimulationContextLocal.setScenarioSimulationEditorPresenter(scenarioSimulationEditorPresenterMock);
        when(scenarioSimulationEditorPresenterMock.getDataManagementStrategy()).thenReturn(dataManagementStrategyMock);

        when(simulationMock.cloneSimulation()).thenReturn(clonedSimulationMock);
        scenarioSimulationContextLocal.getStatus().setSimulation(simulationMock);

        when(scenarioSimulationModelMock.getSimulation()).thenReturn(simulationMock);

        when(scenarioCommandRegistryMock.undo(scenarioSimulationContextLocal)).thenReturn(CommandResultBuilder.SUCCESS);
        when(scenarioCommandRegistryMock.redo(scenarioSimulationContextLocal)).thenReturn(CommandResultBuilder.SUCCESS);

        appendRowCommandMock = spy(new AppendRowCommand() {

            {
                this.restorableStatus = scenarioSimulationContextLocal.getStatus();
            }

            @Override
            public CommandResult<ScenarioSimulationViolation> execute(ScenarioSimulationContext context) {
                return CommandResultBuilder.SUCCESS;
            }

            @Override
            public CommandResult<ScenarioSimulationViolation> undo(ScenarioSimulationContext context) {
                return CommandResultBuilder.SUCCESS;
            }
        });
        when(informationHeaderMetaDataMock.getTitle()).thenReturn(VALUE);
        when(informationHeaderMetaDataMock.getColumnGroup()).thenReturn(COLUMN_GROUP);
        when(propertyHeaderMetaDataMock.getMetadataType()).thenReturn(ScenarioHeaderMetaData.MetadataType.PROPERTY);
        when(propertyHeaderMetaDataMock.getTitle()).thenReturn(GRID_PROPERTY_TITLE);
        when(propertyHeaderMetaDataMock.getColumnGroup()).thenReturn(GRID_COLUMN_GROUP);
        when(propertyHeaderMetaDataMock.getColumnId()).thenReturn(GRID_COLUMN_ID);
        when(headerMetaDatasMock.get(anyInt())).thenReturn(informationHeaderMetaDataMock);
        when(gridColumnMock.getHeaderMetaData()).thenReturn(headerMetaDatasMock);
        when(gridColumnMock.getInformationHeaderMetaData()).thenReturn(informationHeaderMetaDataMock);
        when(gridColumnMock.getPropertyHeaderMetaData()).thenReturn(propertyHeaderMetaDataMock);
        when(gridColumnMock.getFactIdentifier()).thenReturn(factIdentifierMock);
        when(simulationDescriptorMock.getType()).thenReturn(ScenarioSimulationModel.Type.RULE);
        IntStream.range(0, COLUMN_NUMBER).forEach(columnIndex -> {
            gridColumns.add(gridColumnMock);
            factMappingValuesLocal.add(factMappingValueMock);
            factIdentifierSet.add(factIdentifierMock);
            factMappingLocal.add(factMappingMock);
            when(simulationDescriptorMock.getFactMappingByIndex(columnIndex)).thenReturn(factMappingMock);
        });
        when(factIdentifierMock.getClassName()).thenReturn(FULL_CLASS_NAME);
        when(factIdentifierMock.getName()).thenReturn(FACT_IDENTIFIER_NAME);
        when(simulationDescriptorMock.getFactIdentifiers()).thenReturn(factIdentifierSet);
        when(simulationDescriptorMock.getUnmodifiableFactMappings()).thenReturn(factMappingLocal);
        scenarioGridModelMock.bindContent(simulationMock);
        when(factMappingMock.getFactIdentifier()).thenReturn(factIdentifierMock);
        when(factMappingMock.getFactAlias()).thenReturn(FACT_ALIAS);
        doReturn(factMappingMock).when(simulationDescriptorMock).addFactMapping(anyInt(), anyString(), anyObject(), anyObject());
    }
}
