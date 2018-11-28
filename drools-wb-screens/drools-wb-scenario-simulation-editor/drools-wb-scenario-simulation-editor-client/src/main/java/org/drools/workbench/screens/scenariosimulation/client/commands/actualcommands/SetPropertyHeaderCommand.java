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

import java.util.stream.IntStream;

import javax.enterprise.context.Dependent;

import org.drools.workbench.screens.scenariosimulation.client.commands.ScenarioSimulationContext;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridColumn;
import org.drools.workbench.screens.scenariosimulation.model.FactIdentifier;
import org.uberfire.ext.wires.core.grids.client.model.GridData;

import static org.drools.workbench.screens.scenariosimulation.client.utils.ScenarioSimulationUtils.getPropertyMetaDataGroup;

/**
 * <code>Command</code> to to set the <i>property</i> level header for a given column
 */
@Dependent
public class SetPropertyHeaderCommand extends AbstractScenarioSimulationCommand {

    @Override
    protected void internalExecute(ScenarioSimulationContext context) {
        ScenarioGridColumn selectedColumn = (ScenarioGridColumn) context.getModel().getSelectedColumn();
        if (selectedColumn == null) {
            return;
        }
        int columnIndex = context.getModel().getColumns().indexOf(selectedColumn);
        String value = context.getStatus().getValue();
        String title = value.substring(value.indexOf(".") + 1);
        String className = value.split("\\.")[0];
        String fullPackage = context.getStatus().getFullPackage();
        if (!fullPackage.endsWith(".")) {
            fullPackage += ".";
        }
        String canonicalClassName = fullPackage + className;
        FactIdentifier factIdentifier = getFactIdentifierByColumnTitle(className, context).orElse(FactIdentifier.create(selectedColumn.getInformationHeaderMetaData().getColumnId(), canonicalClassName));
        final GridData.Range instanceLimits = context.getModel().getInstanceLimits(columnIndex);
        IntStream.range(instanceLimits.getMinRowIndex(), instanceLimits.getMaxRowIndex() + 1)
                .forEach(index -> {
                    final ScenarioGridColumn scenarioGridColumn = (ScenarioGridColumn) context.getModel().getColumns().get(index);
                    if (!scenarioGridColumn.isInstanceAssigned()) { // We have not defined the instance, yet
                        scenarioGridColumn.getInformationHeaderMetaData().setTitle(className);
                        scenarioGridColumn.setInstanceAssigned(true);
                        scenarioGridColumn.setFactIdentifier(factIdentifier);
                    }
                });
        String placeHolder = ScenarioSimulationEditorConstants.INSTANCE.insertValue();
        selectedColumn.setPlaceHolder(placeHolder);
        selectedColumn.getPropertyHeaderMetaData().setColumnGroup(getPropertyMetaDataGroup(selectedColumn.getInformationHeaderMetaData().getColumnGroup()));
        selectedColumn.getPropertyHeaderMetaData().setTitle(title);
        selectedColumn.getPropertyHeaderMetaData().setReadOnly(false);
        selectedColumn.setPropertyAssigned(true);
        context.getModel().updateColumnProperty(columnIndex,
                                                selectedColumn,
                                                value,
                                                context.getStatus().getValueClassName(), context.getStatus().isKeepData());
        if (context.getScenarioSimulationEditorPresenter() != null) {
            context.getScenarioSimulationEditorPresenter().reloadRightPanel(false);
        }
    }
}
