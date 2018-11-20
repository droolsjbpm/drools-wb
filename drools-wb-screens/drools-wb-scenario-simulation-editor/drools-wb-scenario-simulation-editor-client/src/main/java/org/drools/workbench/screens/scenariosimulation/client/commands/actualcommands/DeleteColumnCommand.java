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

import java.util.Date;
import java.util.Map;

import javax.enterprise.context.Dependent;

import org.drools.workbench.screens.scenariosimulation.client.commands.ScenarioSimulationContext;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.drools.workbench.screens.scenariosimulation.model.FactMappingType;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;

/**
 * <code>Command</code> to <b>delete</b> a column. <b>Eventually</b> add a ne column if the deleted one is the last of its group.
 */
@Dependent
public class DeleteColumnCommand extends AbstractScenarioSimulationCommand {

    @Override
    protected void internalExecute(ScenarioSimulationContext context) {
        context.getModel().deleteColumn(context.getColumnIndex());
        if (context.getModel().getGroupSize(context.getColumnGroup()) < 1) {
            FactMappingType factMappingType = FactMappingType.valueOf(context.getColumnGroup().toUpperCase());
            Map.Entry<String, String> validPlaceholders = context.getModel().getValidPlaceholders();
            String instanceTitle = validPlaceholders.getKey();
            String propertyTitle = validPlaceholders.getValue();
            context.getModel().insertColumn(context.getColumnIndex(), getScenarioGridColumnLocal(instanceTitle,
                                                                                                 propertyTitle,
                                                                                                 String.valueOf(new Date().getTime()),
                                                                                                 context.getColumnGroup(),
                                                                                                 factMappingType,
                                                                                                 context.getScenarioGridPanel(),
                                                                                                 context.getScenarioGridLayer(),
                                                                                                 ScenarioSimulationEditorConstants.INSTANCE.defineValidType()));
        }
        // TODO CHECK POSITION
        GridColumn<?> selectedColumn = context.getModel().getSelectedColumn();
        boolean toDisable = selectedColumn == null || context.getModel().getColumns().indexOf(selectedColumn) == context.getColumnIndex();
        if (context.getRightPanelPresenter() != null && toDisable) {
            new DisableRightPanelCommand().execute(context);
        }
    }
}
