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

import javax.enterprise.context.Dependent;

import org.drools.scenariosimulation.api.model.FactMapping;
import org.drools.scenariosimulation.api.model.SimulationDescriptor;
import org.drools.workbench.screens.scenariosimulation.client.commands.ScenarioSimulationContext;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.drools.workbench.screens.scenariosimulation.client.utils.ScenarioSimulationUtils;
import org.drools.workbench.screens.scenariosimulation.client.values.ScenarioGridCellValue;

/**
 * <code>Command</code> to set the <i>value</i> of a grid' cell
 */
@Dependent
public class SetGridCellValueCommand extends AbstractScenarioSimulationCommand {

    public SetGridCellValueCommand() {
        super(true);
    }

    @Override
    protected void internalExecute(ScenarioSimulationContext context) {
        final ScenarioSimulationContext.Status status = context.getStatus();
        SimulationDescriptor simulationDescriptor = status.getSimulation().getSimulationDescriptor();
        int columnIndex = status.getColumnIndex();
        FactMapping factMapping = simulationDescriptor.getFactMappingByIndex(columnIndex);
        String placeholder = factMapping.isExpressionType() ?
                ScenarioSimulationEditorConstants.INSTANCE.insertExpression() :
                ScenarioSimulationUtils.getPlaceholder(factMapping.getClassName());
        context.getModel().setCellValue(status.getRowIndex(),
                                        columnIndex,
                                        new ScenarioGridCellValue(status.getGridCellValue(),
                                                                  placeholder));
        context.getModel().resetError(status.getRowIndex(), columnIndex);
    }
}
