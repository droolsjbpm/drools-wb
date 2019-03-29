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

package org.drools.workbench.screens.scenariosimulation.client.domelements;

import java.util.Objects;

import org.drools.workbench.screens.scenariosimulation.client.events.ReloadTestToolsEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.SetHeaderCellValueEvent;
import org.drools.workbench.screens.scenariosimulation.client.metadata.ScenarioHeaderMetaData;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGrid;
import org.gwtbootstrap3.client.ui.TextArea;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;

public class ScenarioHeaderTextAreaDOMElement extends ScenarioCellTextAreaDOMElement {

    private ScenarioHeaderMetaData scenarioHeaderMetaData;

    public ScenarioHeaderTextAreaDOMElement(TextArea widget, GridLayer gridLayer, GridWidget gridWidget) {
        super(widget, gridLayer, gridWidget);
    }

    /**
     * Set the <code>ScenarioHeaderMetaData</code> this element is bind to
     * @param scenarioHeaderMetaData
     */
    public void setScenarioHeaderMetaData(ScenarioHeaderMetaData scenarioHeaderMetaData) {
        this.scenarioHeaderMetaData = scenarioHeaderMetaData;
    }

    @Override
    public void flush(final String value) {
        if (scenarioHeaderMetaData != null) {
            scenarioHeaderMetaData.setEditingMode(false);
            if (Objects.equals(value, scenarioHeaderMetaData.getTitle())) {
                return;
            }
        }
        internalFlush(value);
    }

    protected void internalFlush(final String value) {
        final int rowIndex = context.getRowIndex();
        final int columnIndex = context.getColumnIndex();
        try {
            boolean isInstanceHeader = scenarioHeaderMetaData != null && scenarioHeaderMetaData.isInstanceHeader();
            boolean isPropertyHeader = scenarioHeaderMetaData != null && scenarioHeaderMetaData.isPropertyHeader();
            ((ScenarioGrid)gridWidget).getEventBus().fireEvent(new SetHeaderCellValueEvent(rowIndex, columnIndex, value, isInstanceHeader, isPropertyHeader));
            ((ScenarioGrid)gridWidget).getEventBus().fireEvent(new ReloadTestToolsEvent(true));
        } catch (Exception e) {
            throw new IllegalArgumentException(new StringBuilder().append("Impossible to update header (").append(rowIndex)
                                                       .append(") of column ").append(columnIndex).toString(), e);
        }
    }
}
