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
package org.drools.workbench.screens.scenariosimulation.client.widgets;

import org.drools.workbench.screens.scenariosimulation.client.values.ScenarioGridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCell;

public class ScenarioGridCell extends BaseGridCell<String> {

    // true if this cell is already in editing mode
    private boolean isEditing = false;
    // true if this cell has to show errors
    private boolean isError = false;

    public ScenarioGridCell(ScenarioGridCellValue value) {
        super(value);
    }

    public boolean isEditing() {
        return isEditing;
    }

    public void setEditing(boolean editing) {
        isEditing = editing;
    }

    public boolean isError() {
        return isError;
    }

    public void setError(boolean error) {
        isError = error;
    }
}