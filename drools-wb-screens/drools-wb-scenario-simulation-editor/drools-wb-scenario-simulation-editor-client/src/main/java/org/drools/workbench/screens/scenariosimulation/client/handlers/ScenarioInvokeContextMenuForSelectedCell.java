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

package org.drools.workbench.screens.scenariosimulation.client.handlers;

import com.ait.lienzo.client.core.types.Point2D;
import org.drools.workbench.screens.scenariosimulation.client.menu.ScenarioContextMenuRegistry;
import org.drools.workbench.screens.scenariosimulation.client.utils.ScenarioSimulationUtils;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGrid;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.util.ColumnIndexUtilities;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.impl.KeyboardOperationInvokeContextMenuForSelectedCell;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;

import static org.drools.workbench.screens.scenariosimulation.client.utils.ScenarioSimulationUtils.getXYCell;

public class ScenarioInvokeContextMenuForSelectedCell extends KeyboardOperationInvokeContextMenuForSelectedCell {

    private ScenarioContextMenuRegistry scenarioContextMenuRegistry;

    public ScenarioInvokeContextMenuForSelectedCell(final GridLayer gridLayer, final ScenarioContextMenuRegistry
            scenarioContextMenuRegistry) {
        super(gridLayer);
        this.scenarioContextMenuRegistry = scenarioContextMenuRegistry;
    }

    @Override
    public boolean isExecutable(final GridWidget gridWidget) {
        final GridData model = gridWidget.getModel();
        if (model.getSelectedHeaderCells().size() == 1 && model.getSelectedCells().size() == 0) {
            return true;
        }
        if (model.getSelectedHeaderCells().size() == 0 && model.getSelectedCells().size() == 1) {
            return true;
        }
        return false;
    }

    @Override
    public boolean perform(final GridWidget gridWidget, final boolean isShiftKeyDown, final boolean isControlKeyDown) {
        final GridData model = gridWidget.getModel();
        GridData.SelectedCell origin = null;
        boolean isHeader = false;
        if (model.getSelectedHeaderCells().size() == 1) {
            origin = model.getSelectedHeaderCells().get(0);
            isHeader = true;
        } else if (model.getSelectedCells().size() == 1) {
            origin = model.getSelectedCellsOrigin();
            isHeader = false;
        }
        final int uiRowIndex = origin.getRowIndex();
        final int uiColumnIndex = ColumnIndexUtilities.findUiColumnIndex(model.getColumns(),
                                                                         origin.getColumnIndex());
        final GridColumn<?> column = model.getColumns().get(uiColumnIndex);
        if (column instanceof ScenarioGridColumn) {
            final Point2D middleXYCell = getXYCell(gridWidget, column, ScenarioSimulationUtils.PositionX.MIDDLE, isHeader, uiRowIndex, gridLayer);
            return scenarioContextMenuRegistry.manageRightClick((ScenarioGrid) gridWidget,
                                                                (int) middleXYCell.getX(),
                                                                (int) middleXYCell.getY(),
                                                                uiRowIndex,
                                                                uiColumnIndex,
                                                                isHeader);
        }
        return false;
    }
}
