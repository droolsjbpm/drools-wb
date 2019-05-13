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
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;

import org.drools.scenariosimulation.api.model.ExpressionElement;
import org.drools.scenariosimulation.api.model.FactMapping;
import org.drools.workbench.screens.scenariosimulation.client.commands.ScenarioSimulationContext;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridColumn;

/**
 * <code>Command</code> to <b>duplicate</b> an instance
 */
@Dependent
public class DuplicateInstanceCommand extends AbstractSelectedColumnCommand {

    public final static String COPY_LABEL = "_copy_";

    @Override
    protected void executeIfSelectedColumn(ScenarioSimulationContext context, ScenarioGridColumn selectedColumn) {
        /* Generating the new instance alias with following schema: <original instance name> + '_copy_' + <number of existing instances> */
        int instancesCount = context.getModel().getInstancesCount(selectedColumn.getFactIdentifier().getClassName());
        String alias = selectedColumn.getInformationHeaderMetaData().getTitle().split(COPY_LABEL)[0] + COPY_LABEL + instancesCount;

        /* For every columns which belongs to the selected instance, it creates a new column and assign it the duplicated instance
         * and the duplicated property, if are assigned */
        int columnPosition = context.getModel().getInstanceLimits(context.getModel().getColumns().indexOf(selectedColumn)).getMaxRowIndex() + 1;
        AtomicInteger nextColumnPosition = new AtomicInteger(columnPosition);
        context.getModel().getInstanceScenarioGridColumns(selectedColumn).forEach(
                originalColumn -> {
                    ScenarioGridColumn createdColumn = insertNewColumn(context, originalColumn, nextColumnPosition.getAndIncrement(), false);
                    if (originalColumn.isInstanceAssigned()) {
                        setInstanceHeader(context, createdColumn, alias, originalColumn.getFactIdentifier().getClassName());

                        if (originalColumn.isPropertyAssigned()) {
                            int originalColumnIndex = context.getModel().getColumns().indexOf(originalColumn);
                            int createdColumnIndex = context.getModel().getColumns().indexOf(createdColumn);
                            final FactMapping originalFactMapping = context.getModel().getSimulation().get().getSimulationDescriptor().getFactMappingByIndex(originalColumnIndex);
                            /*  Rebuilt propertyNameElements, which is composed by: factName.property . The property MUST be the original property name */
                            List<String> propertyNameElements = new ArrayList<>();
                            propertyNameElements.add(alias);
                            propertyNameElements.addAll(originalFactMapping.getExpressionElementsWithoutClass().stream().map(ExpressionElement::getStep).collect(Collectors.toList()));
                            //TODO settare qui il fullPackage in caso di DataObject
                            setPropertyHeader(context,
                                              createdColumn,
                                              propertyNameElements,
                                              originalFactMapping.getClassName(),
                                              Optional.of(originalColumn.getPropertyHeaderMetaData().getTitle()));

                            /* It copies the properties values */
                            context.getModel().duplicateColumnValues(originalColumnIndex, createdColumnIndex);
                        }
                    }
                });
    }

}
