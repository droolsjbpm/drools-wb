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
package org.drools.workbench.screens.scenariosimulation.client.models;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import com.google.gwt.event.shared.EventBus;
import org.drools.workbench.screens.scenariosimulation.client.events.ReloadRightPanelEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.ScenarioGridReloadEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.ScenarioNotificationEvent;
import org.drools.workbench.screens.scenariosimulation.client.metadata.ScenarioHeaderMetaData;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.drools.workbench.screens.scenariosimulation.client.values.ScenarioGridCellValue;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridCell;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridColumn;
import org.drools.workbench.screens.scenariosimulation.model.ExpressionIdentifier;
import org.drools.workbench.screens.scenariosimulation.model.FactIdentifier;
import org.drools.workbench.screens.scenariosimulation.model.FactMapping;
import org.drools.workbench.screens.scenariosimulation.model.FactMappingType;
import org.drools.workbench.screens.scenariosimulation.model.FactMappingValue;
import org.drools.workbench.screens.scenariosimulation.model.Scenario;
import org.drools.workbench.screens.scenariosimulation.model.Simulation;
import org.drools.workbench.screens.scenariosimulation.model.SimulationDescriptor;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridRow;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridData;
import org.uberfire.workbench.events.NotificationEvent;

public class ScenarioGridModel extends BaseGridData {

    public static final int HEADER_ROW_COUNT = 3;

    protected Simulation simulation;

    protected EventBus eventBus;

    protected AtomicInteger columnCounter = new AtomicInteger(0);

    protected GridColumn<?> selectedColumn = null;

    public ScenarioGridModel() {
    }

    public ScenarioGridModel(boolean isMerged) {
        super(isMerged);
        setHeaderRowCount(HEADER_ROW_COUNT);
    }

    /**
     * Method to bind the data serialized inside backend <code>ScenarioSimulationModel</code>
     * @param simulation
     */
    public void bindContent(Simulation simulation) {
        this.simulation = simulation;
        checkSimulation();
        columnCounter.set(simulation.getSimulationDescriptor().getUnmodifiableFactMappings().size());
    }

    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public int nextColumnCount() {
        return columnCounter.getAndIncrement();
    }

    public Map.Entry<String, String> getValidPlaceholders() {
        String instanceTitle;
        String propertyTitle;
        do {
            int nextColumnCount = nextColumnCount();
            instanceTitle = FactMapping.getInstancePlaceHolder(nextColumnCount);
            propertyTitle = FactMapping.getPropertyPlaceHolder(nextColumnCount);
        } while (!isNewInstanceName(instanceTitle) ||
                !isNewPropertyName(propertyTitle));
        return new AbstractMap.SimpleEntry<>(instanceTitle, propertyTitle);
    }

    /**
     * This method <i>append</i> a new row to the grid <b>and</b> to the underlying model
     * @param row
     */
    @Override
    public void appendRow(GridRow row) {
        checkSimulation();
        super.appendRow(row);
        int rowIndex = getRowCount() - 1;
        commonAddRow(rowIndex);
    }

    /**
     * This method <i>insert</i> a row to the grid and populate it with values taken from given <code>Scenario</code>
     * @param row
     */
    public void insertRowGridOnly(final int rowIndex,
                                  final GridRow row, final Scenario scenario) {
        checkSimulation();
        super.insertRow(rowIndex, row);
        scenario.getUnmodifiableFactMappingValues().forEach(value -> {
            FactIdentifier factIdentifier = value.getFactIdentifier();
            ExpressionIdentifier expressionIdentifier = value.getExpressionIdentifier();
            if (value.getRawValue() == null || value.getRawValue() instanceof String) { // Let' put a placeholder
                String stringValue = (String) value.getRawValue();
                int columnIndex = simulation.getSimulationDescriptor().getIndexByIdentifier(factIdentifier, expressionIdentifier);
                String placeHolder = ((ScenarioGridColumn) columns.get(columnIndex)).getPlaceHolder();
                setCell(rowIndex, columnIndex, () -> new ScenarioGridCell(new ScenarioGridCellValue(stringValue, placeHolder)));
            } else {
                throw new UnsupportedOperationException("Only string is supported at the moment");
            }
        });
        updateIndexColumn();
    }

    /**
     * This method <i>insert</i> a new row to the grid <b>and</b> to the underlying model
     * @param row
     */
    @Override
    public void insertRow(int rowIndex, GridRow row) {
        checkSimulation();
        super.insertRow(rowIndex, row);
        commonAddRow(rowIndex);
    }

    /**
     * This method <i>delete</i> the row at the given index from both the grid <b>and</b> the underlying model
     * @param rowIndex
     */
    @Override
    public Range deleteRow(int rowIndex) {
        checkSimulation();
        Range toReturn = super.deleteRow(rowIndex);
        simulation.removeScenarioByIndex(rowIndex);
        updateIndexColumn();
        return toReturn;
    }

    /**
     * This method <i>duplicate</i> the row at the given index from both the grid <b>and</b> the underlying model
     * and insert just below the original one
     * @param rowIndex
     */
    public void duplicateRow(int rowIndex, GridRow row) {
        checkSimulation();
        int newRowIndex = rowIndex + 1;
        final Scenario toDuplicate = simulation.cloneScenario(rowIndex, newRowIndex);
        insertRowGridOnly(newRowIndex, row, toDuplicate);
    }

    /**
     * This method <i>insert</i> a new column to the grid <b>without</b> modify underlying model
     * @param index
     * @param column
     */
    public void insertColumnGridOnly(final int index, final GridColumn<?> column) {
        checkSimulation();
        super.insertColumn(index, column);
    }

    /**
     * This method <i>insert</i> a new column to the grid <b>and</b> to the underlying model
     * @param index
     * @param column
     */
    @Override
    public void insertColumn(final int index, final GridColumn<?> column) {
        checkSimulation();
        commonAddColumn(index, column);
    }

    /**
     * This method <i>delete</i> the column at the given index from both the grid <b>and</b> the underlying model
     * @param columnIndex
     */
    public void deleteColumn(int columnIndex) {
        checkSimulation();
        final GridColumn<?> toDelete = getColumns().get(columnIndex);
        deleteColumn(toDelete);
        simulation.removeFactMappingByIndex(columnIndex);
    }

    /**
     * This method update the instance mapped inside a given column
     * @param columnIndex
     * @param column
     */
    public void updateColumnInstance(int columnIndex, final GridColumn<?> column) {
        checkSimulation();
        deleteColumn(columnIndex);
        String group = ((ScenarioGridColumn) column).getInformationHeaderMetaData().getColumnGroup();
        String columnId = ((ScenarioGridColumn) column).getInformationHeaderMetaData().getColumnId();
        ExpressionIdentifier ei = ExpressionIdentifier.create(columnId, FactMappingType.valueOf(group));
        commonAddColumn(columnIndex, column, ei);
    }

    /**
     * This method update the type mapped inside a give column and updates the underlying model
     * @param columnIndex
     * @param column
     * @param value
     * @param lastLevelClassName
     * @param keepData
     */
    public void updateColumnProperty(int columnIndex, final GridColumn<?> column, String value, String lastLevelClassName, boolean keepData) {
        checkSimulation();
        List<GridCellValue<?>> originalValues = new ArrayList<>();
        if (keepData) {
            IntStream.range(0, getRowCount())
                    .forEach(rowIndex -> originalValues.add(getCell(rowIndex, columnIndex).getValue()));
        }
        deleteColumn(columnIndex);
        String group = ((ScenarioGridColumn) column).getInformationHeaderMetaData().getColumnGroup();
        String columnId = ((ScenarioGridColumn) column).getInformationHeaderMetaData().getColumnId();
        String[] elements = value.split("\\.");
        ExpressionIdentifier ei = ExpressionIdentifier.create(columnId, FactMappingType.valueOf(group));
        commonAddColumn(columnIndex, column, ei);
        final FactMapping factMappingByIndex = simulation.getSimulationDescriptor().getFactMappingByIndex(columnIndex);
        IntStream.range(1, elements.length)
                .forEach(stepIndex -> factMappingByIndex.addExpressionElement(elements[stepIndex], lastLevelClassName));
        if (keepData) {
            IntStream.range(0, getRowCount())
                    .forEach(rowIndex -> setCellValue(rowIndex, columnIndex, originalValues.get(rowIndex)));
        }
    }

    /**
     * This method <i>set</i> a cell value to the grid <b>without</b> modify underlying model
     * @param rowIndex
     * @param columnIndex
     * @param cellSupplier
     */
    public Range setCellGridOnly(int rowIndex, int columnIndex, Supplier<GridCell<?>> cellSupplier) {
        checkSimulation();
        return super.setCell(rowIndex, columnIndex, cellSupplier);
    }

    /**
     * This method <i>set</i> a cell value to the grid <b>and</b> to the underlying model
     * @param rowIndex
     * @param columnIndex
     * @param cellSupplier
     */
    @Override
    public Range setCell(int rowIndex, int columnIndex, Supplier<GridCell<?>> cellSupplier) {
        checkSimulation();
        Range toReturn = super.setCell(rowIndex, columnIndex, cellSupplier);
        try {
            Optional<?> optionalValue = getCellValue(getCell(rowIndex, columnIndex));
            Object rawValue = optionalValue.orElse(null);
            String cellValue = (rawValue instanceof String) ? (String) rawValue : null;
            Scenario scenarioByIndex = simulation.getScenarioByIndex(rowIndex);
            FactMapping factMappingByIndex = simulation.getSimulationDescriptor().getFactMappingByIndex(columnIndex);
            FactIdentifier factIdentifier = factMappingByIndex.getFactIdentifier();
            ExpressionIdentifier expressionIdentifier = factMappingByIndex.getExpressionIdentifier();
            scenarioByIndex.addOrUpdateMappingValue(factIdentifier, expressionIdentifier, cellValue);
        } catch (Throwable t) {
            toReturn = super.deleteCell(rowIndex, columnIndex);
            eventBus.fireEvent(new ScenarioGridReloadEvent());
        }
        return toReturn;
    }

    @Override
    public Range setCellValue(int rowIndex, int columnIndex, GridCellValue<?> value) {
        return setCell(rowIndex, columnIndex, () -> new ScenarioGridCell((ScenarioGridCellValue) value));
    }

    @Override
    public Range deleteCell(int rowIndex, int columnIndex) {
        FactMapping factMapping = simulation.getSimulationDescriptor().getFactMappingByIndex(columnIndex);
        simulation.getScenarioByIndex(rowIndex)
                .removeFactMappingValueByIdentifiers(factMapping.getFactIdentifier(), factMapping.getExpressionIdentifier());
        return super.deleteCell(rowIndex, columnIndex);
    }

    /**
     * This methods returns the <code>Range</code> of a <b>single</b> block of columns of the same instance/data object.
     * A <code>single</code> block is made of all the columns immediately to the left and right of the selected one with the same "label".
     * If there is another column with the same "label" but separated by a different column, it is not part of the group.
     * @param columnIndex
     * @return
     */
    public Range getInstanceLimits(int columnIndex) {
        final ScenarioGridColumn selectedColumn = (ScenarioGridColumn) columns.get(columnIndex);
        final String originalColumnGroup = selectedColumn.getInformationHeaderMetaData().getColumnGroup();
        final ScenarioHeaderMetaData selectedInformationHeaderMetaData = selectedColumn.getInformationHeaderMetaData();
        String originalColumnTitle = selectedInformationHeaderMetaData.getTitle();
        int leftPosition = columnIndex;
        while (leftPosition > 1 && ((ScenarioGridColumn) columns.get(leftPosition - 1)).getInformationHeaderMetaData().getColumnGroup().equals(originalColumnGroup) && ((ScenarioGridColumn) columns.get(leftPosition - 1)).getInformationHeaderMetaData().getTitle().equals(originalColumnTitle)) {
            leftPosition--;
        }
        int rightPosition = columnIndex;
        while (rightPosition < columns.size() - 1 && ((ScenarioGridColumn) columns.get(rightPosition + 1)).getInformationHeaderMetaData().getColumnGroup().equals(originalColumnGroup) && ((ScenarioGridColumn) columns.get(rightPosition + 1)).getInformationHeaderMetaData().getTitle().equals(originalColumnTitle)) {
            rightPosition++;
        }
        return new Range(leftPosition, rightPosition);
    }

    /**
     * Return the first index to the left of the given group, i.e. <b>excluded</b> the left-most index of <b>that</b> group
     * @param groupName
     * @return
     */
    public int getFirstIndexLeftOfGroup(String groupName) {
        // HORRIBLE TRICK BECAUSE gridColumn.getIndex() DOES NOT REFLECT ACTUAL POSITION, BUT ONLY ORDER OF INSERTION
        final List<GridColumn<?>> columns = this.getColumns();
        final Optional<Integer> first = columns    // Retrieving the column list
                .stream()  // streaming
                .filter(gridColumn -> ((ScenarioGridColumn) gridColumn).getInformationHeaderMetaData().getColumnGroup().equals(groupName))  // filtering by group name
                .findFirst()
                .map(gridColumn -> {
                    int indexOfColumn = columns.indexOf(gridColumn);
                    return indexOfColumn > -1 ? indexOfColumn : 0;   // mapping the retrieved column to its index inside the list, or 0
                });
        return first.orElseGet(() -> 0); // returning the retrieved value or, if null, 0
    }

    /**
     * Return the first index to the right of the given group, i.e. <b>excluded</b> the right-most index of <b>that</b> group
     * @param groupName
     * @return
     */
    public int getFirstIndexRightOfGroup(String groupName) {
        // HORRIBLE TRICK BECAUSE gridColumn.getIndex() DOES NOT REFLECT ACTUAL POSITION, BUT ONLY ORDER OF INSERTION
        final Optional<Integer> last = this.getColumns()    // Retrieving the column list
                .stream()  // streaming
                .filter(gridColumn -> ((ScenarioGridColumn) gridColumn).getInformationHeaderMetaData().getColumnGroup().equals(groupName))  // filtering by group name
                .reduce((first, second) -> second)  // reducing to have only the last element
                .map(gridColumn -> {
                    int indexOfColumn = this.getColumns().indexOf(gridColumn);
                    return indexOfColumn > -1 ? indexOfColumn + 1 : getColumnCount();   // mapping the retrieved column to its index inside the list +1, or to the total number of columns
                });
        return last.orElseGet(this::getColumnCount); // returning the retrieved value or, if null, the total number of columns
    }

    /**
     * Returns how many columns are already in place for the given group
     * @param groupName
     * @return
     */
    public long getGroupSize(String groupName) {
        return this.getColumns()
                .stream()
                .filter(gridColumn -> ((ScenarioGridColumn) gridColumn).getInformationHeaderMetaData().getColumnGroup().equals(groupName))
                .count();
    }

    public void updateHeader(int columnIndex, int headerRowIndex, String value) {
        final ScenarioHeaderMetaData editedMetadata = (ScenarioHeaderMetaData) getColumns().get(columnIndex).getHeaderMetaData().get(headerRowIndex);
        // do not update if old and new value are the same
        if (Objects.equals(editedMetadata.getTitle(), value)) {
            return;
        }
        SimulationDescriptor simulationDescriptor = simulation.getSimulationDescriptor();
        FactMapping factMappingToEdit = simulationDescriptor.getFactMappingByIndex(columnIndex);
        final FactIdentifier factIdentifierToEdit = factMappingToEdit.getFactIdentifier();
        if (editedMetadata.isInstanceHeader()) { // we have to update title and value for every column of the group
            IntStream.range(0, getColumnCount()).forEach(index -> {
                FactMapping factMappingToCheck = simulationDescriptor.getFactMappingByIndex(index);
                final FactIdentifier factIdentifierToCheck = factMappingToCheck.getFactIdentifier();
                if (Objects.equals(FactIdentifier.EMPTY, factIdentifierToEdit)) {
                    if  (Objects.equals(factIdentifierToCheck, factIdentifierToEdit) && Objects.equals(factMappingToEdit.getFactAlias(), factMappingToCheck.getFactAlias())) {
                        ((ScenarioGridColumn) columns.get(index)).getInformationHeaderMetaData().setTitle(value);
                        factMappingToCheck.setFactAlias(value);
                    }
                } else if (Objects.equals(factIdentifierToCheck, factIdentifierToEdit)) {
                    ((ScenarioGridColumn) columns.get(index)).getInformationHeaderMetaData().setTitle(value);
                    factMappingToCheck.setFactAlias(value);
                }
            });
            eventBus.fireEvent(new ReloadRightPanelEvent(false));
        } else {
            editedMetadata.setTitle(value);
            factMappingToEdit.setExpressionAlias(value);
        }
    }

    public void clear() {
        // Deleting rows
        int to = getRowCount();
        IntStream.range(0, to)
                .map(i -> to - i - 1)
                .forEach(super::deleteRow);
        List<GridColumn<?>> copyList = new ArrayList<>(getColumns());
        copyList.forEach(super::deleteColumn);
        // clear can be called before bind
        if (simulation != null) {
            simulation.clear();
        }
    }

    @Override
    public void clearSelections() {
        super.clearSelections();
        selectedColumn = null;
    }

    /**
     * Select all the cells of the given column
     * @param columnIndex
     */
    public void selectColumn(int columnIndex) {
        if (columnIndex > getColumnCount() - 1) {
            return;
        }
        if (!selectedHeaderCells.isEmpty()) {
            final SelectedCell selectedHeaderCell = selectedHeaderCells.get(0);
            selectedHeaderCells.clear();
            selectHeaderCell(selectedHeaderCell.getRowIndex(), columnIndex);
        }
        selectedColumn = getColumns().get(columnIndex);
    }

    /**
     * Select all the cells of the given row
     * @param rowIndex
     */
    public void selectRow(int rowIndex) {
        if (rowIndex > getRowCount() - 1) {
            return;
        }
        int columns = getColumnCount();
        IntStream.range(0, columns).forEach(columnIndex -> selectCell(rowIndex, columnIndex));
    }

    public GridColumn<?> getSelectedColumn() {
        return selectedColumn;
    }

    public Optional<Simulation> getSimulation() {
        return Optional.ofNullable(simulation);
    }

    /**
     * Returns <code>true</code> if all the grid cells of the selected column are empty, i.e. the GridCell.getValue() == null OR
     * GridCell.getValue().getValue() == null
     * @return
     */
    public boolean isSelectedColumnEmpty() {
        return selectedColumn == null || isColumnEmpty(getColumns().indexOf(selectedColumn));
    }

    /**
     * Returns <code>true</code> if all the grid cells of the column at given index are empty, i.e. the GridCell.getValue() == null OR
     * GridCell.getValue().getValue() == null
     * @param columnIndex
     * @return
     */
    public boolean isColumnEmpty(int columnIndex) {
        return !IntStream.range(0, getRowCount())
                .filter(rowIndex -> getCellValue(getCell(rowIndex, columnIndex)).isPresent())
                .findFirst()
                .isPresent();
    }

    /**
     * Returns <code>true</code> if property mapped to the selected column is the same as the provided one
     * @param propertyName
     * @return
     */
    public boolean isSameSelectedColumnProperty(String propertyName) {
        return selectedColumn == null || isSameSelectedColumnProperty(getColumns().indexOf(selectedColumn), propertyName);
    }

    /**
     * Returns <code>true</code> if property mapped to the column at given index is the same as the provided one
     * @param columnIndex
     * @param propertyName
     * @return
     */
    public boolean isSameSelectedColumnProperty(int columnIndex, String propertyName) {
        SimulationDescriptor simulationDescriptor = simulation.getSimulationDescriptor();
        final FactMapping factMappingByIndex = simulationDescriptor.getFactMappingByIndex(columnIndex);
        return factMappingByIndex.getExpressionAlias().equals(propertyName);
    }

    /**
     * Returns <code>true</code> if type mapped to the selected column is the same as the provided one
     * @param className
     * @return
     */
    public boolean isSameSelectedColumnType(String className) {
        return selectedColumn == null || isSameSelectedColumnType(getColumns().indexOf(selectedColumn), className);
    }

    /**
     * Returns <code>true</code> if type mapped to the column at given index is the same as the provided one
     * @param columnIndex
     * @param className
     * @return
     */
    public boolean isSameSelectedColumnType(int columnIndex, String className) {
        SimulationDescriptor simulationDescriptor = simulation.getSimulationDescriptor();
        final FactMapping factMappingByIndex = simulationDescriptor.getFactMappingByIndex(columnIndex);
        return factMappingByIndex.getClassName().equals(className);
    }

    /**
     * This method <i>add</i> or <i>insert</i> a new column to the grid <b>and</b> to the underlying model, depending on the index value.
     * If index == -1 -> add, otherwise insert. It automatically creates default <code>FactIdentifier</code>  (for String class) and <code>ExpressionIdentifier</code>
     * @param index
     * @param column
     */
    protected void commonAddColumn(final int index, final GridColumn<?> column) {
        String group = ((ScenarioGridColumn) column).getInformationHeaderMetaData().getColumnGroup();
        String columnId = ((ScenarioGridColumn) column).getInformationHeaderMetaData().getColumnId();
        final ExpressionIdentifier ei = ExpressionIdentifier.create(columnId, FactMappingType.valueOf(group));
        commonAddColumn(index, column, ei);
    }

    /**
     * This method <i>add</i> or <i>insert</i> a new column to the grid <b>and</b> to the underlying model, depending on the index value.
     * If index == -1 -> add, otherwise insert.
     * @param index
     * @param column
     * @param ei
     */
    protected void commonAddColumn(final int index, final GridColumn<?> column, ExpressionIdentifier ei) {
        SimulationDescriptor simulationDescriptor = simulation.getSimulationDescriptor();
        String instanceTitle = ((ScenarioGridColumn) column).getInformationHeaderMetaData().getTitle();
        String propertyTitle = ((ScenarioGridColumn) column).getPropertyHeaderMetaData().getTitle();
        final int columnIndex = index == -1 ? getColumnCount() : index;
        try {
            final FactMapping createdFactMapping = simulationDescriptor.addFactMapping(columnIndex, instanceTitle, ((ScenarioGridColumn) column).getFactIdentifier(), ei);
            createdFactMapping.setExpressionAlias(propertyTitle);
            if (index == -1) {  // This is actually an append
                super.appendColumn(column);
            } else {
                super.insertColumn(index, column);
            }
            final Range instanceLimits = getInstanceLimits(columnIndex);
            IntStream.range(instanceLimits.getMinRowIndex(), instanceLimits.getMaxRowIndex() + 1)
                    .filter(currentIndex -> currentIndex != columnIndex)
                    .forEach(currentIndex -> simulationDescriptor.getFactMappingByIndex(currentIndex).setFactAlias(createdFactMapping.getFactAlias()));
            selectColumn(columns.indexOf(column));
        } catch (Throwable t) {
            eventBus.fireEvent(new ScenarioNotificationEvent("Error during column creation: " + t.getMessage(), NotificationEvent.NotificationType.ERROR));
            eventBus.fireEvent(new ScenarioGridReloadEvent());
            return;
        }

        final List<Scenario> scenarios = simulation.getUnmodifiableScenarios();
        String placeHolder = ((ScenarioGridColumn) column).getPlaceHolder();
        IntStream.range(0, scenarios.size())
                .forEach(rowIndex -> setCell(rowIndex, columnIndex, () -> new ScenarioGridCell(new ScenarioGridCellValue(null, placeHolder))));
    }

    protected void commonAddRow(int rowIndex) {
        Scenario scenario = simulation.addScenario(rowIndex);
        final SimulationDescriptor simulationDescriptor = simulation.getSimulationDescriptor();
        IntStream.range(1, getColumnCount()).forEach(columnIndex -> {
            final FactMapping factMappingByIndex = simulationDescriptor.getFactMappingByIndex(columnIndex);
            scenario.addMappingValue(factMappingByIndex.getFactIdentifier(), factMappingByIndex.getExpressionIdentifier(), null);
            String placeHolder = FactIdentifier.EMPTY.equals(factMappingByIndex.getFactIdentifier()) ? ScenarioSimulationEditorConstants.INSTANCE.defineValidType() : ScenarioSimulationEditorConstants.INSTANCE.insertValue();
            setCell(rowIndex, columnIndex, () -> new ScenarioGridCell(new ScenarioGridCellValue(null, placeHolder)));
        });
        updateIndexColumn();
    }

    protected void updateIndexColumn() {
        final Optional<GridColumn<?>> indexColumn = this.getColumns()    // Retrieving the column list
                .stream()  // streaming
                .filter(gridColumn -> ((ScenarioGridColumn) gridColumn).getInformationHeaderMetaData().getTitle().equals(FactIdentifier.INDEX.getName()))  // filtering by group name
                .findFirst();
        indexColumn.ifPresent(column -> {
            int indexOfColumn = getColumns().indexOf(column);
            IntStream.range(0, getRowCount())
                    .forEach(rowIndex -> {
                        String value = String.valueOf(rowIndex + 1);
                        setCellValue(rowIndex, indexOfColumn, new ScenarioGridCellValue(value));
                    });
        });
    }

    void checkSimulation() {
        Objects.requireNonNull(simulation, "Bind a simulation to the ScenarioGridModel to use it");
    }

    public boolean validateHeaderUpdate(String value, int rowIndex, int columnIndex) {
        ScenarioHeaderMetaData headerToEdit = (ScenarioHeaderMetaData) getColumns().get(columnIndex).getHeaderMetaData().get(rowIndex);
        boolean isValid = !headerToEdit.isInstanceHeader() || isUnique(value, rowIndex, columnIndex);
        if (!isValid) {
            eventBus.fireEvent(new ScenarioNotificationEvent("Name '" + value + "' is already used",
                                                             NotificationEvent.NotificationType.ERROR));
        }
        return isValid;
    }

    boolean isUnique(String value, int rowIndex, int columnIndex) {
        Range instanceLimits = getInstanceLimits(columnIndex);
        SimulationDescriptor simulationDescriptor = simulation.getSimulationDescriptor();
        FactIdentifier factIdentifier = simulationDescriptor.getFactMappingByIndex(columnIndex).getFactIdentifier();
        return IntStream.range(0, getColumnCount())
                .filter(index -> index < instanceLimits.getMinRowIndex() || index > instanceLimits.getMaxRowIndex())
                .filter(index -> !Objects.equals(factIdentifier, simulationDescriptor.getFactMappingByIndex(index).getFactIdentifier()))
                .mapToObj(index -> getColumns().get(index))
                .filter(elem -> elem.getHeaderMetaData().size() > rowIndex)
                .map(elem -> (ScenarioHeaderMetaData) elem.getHeaderMetaData().get(rowIndex))
                .noneMatch(elem -> Objects.equals(elem.getTitle(), value));
    }

    boolean isNewInstanceName(String value) {
        return getColumns().stream()
                .map(elem -> ((ScenarioGridColumn) elem).getInformationHeaderMetaData())
                .filter(Objects::nonNull)
                .noneMatch(elem -> Objects.equals(elem.getTitle(), value));
    }

    boolean isNewPropertyName(String value) {
        return getColumns().stream()
                .map(elem -> ((ScenarioGridColumn) elem).getPropertyHeaderMetaData())
                .filter(Objects::nonNull)
                .noneMatch(elem -> Objects.equals(elem.getTitle(), value));
    }

    public void resetErrors() {
        IntStream.range(0, getRowCount()).forEach(this::resetErrors);
    }

    public void resetErrors(int rowIndex) {
        Scenario scenarioByIndex = simulation.getScenarioByIndex(rowIndex);
        scenarioByIndex.resetErrors();
        refreshErrors();
    }

    public void refreshErrors() {
        IntStream.range(0, getRowCount()).forEach(this::refreshErrorsRow);
    }

    void refreshErrorsRow(int rowIndex) {
        SimulationDescriptor simulationDescriptor = simulation.getSimulationDescriptor();
        Scenario scenarioByIndex = simulation.getScenarioByIndex(rowIndex);
        IntStream.range(0, getColumnCount()).forEach(columnIndex -> {
            ScenarioGridCell cell = (ScenarioGridCell) getCell(rowIndex, columnIndex);
            if (cell == null) {
                return;
            }
            final FactMapping factMappingByIndex = simulationDescriptor.getFactMappingByIndex(columnIndex);
            Optional<FactMappingValue> factMappingValue = scenarioByIndex.getFactMappingValue(factMappingByIndex.getFactIdentifier(), factMappingByIndex.getExpressionIdentifier());
            if (factMappingValue.isPresent()) {
                cell.setError(factMappingValue.get().isError());
            } else {
                cell.setError(false);
            }
        });
    }

    // Helper method to avoid potential NPE
    private Optional<?> getCellValue(GridCell<?> gridCell) {
        if (gridCell == null || gridCell.getValue() == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(gridCell.getValue().getValue());
    }
}