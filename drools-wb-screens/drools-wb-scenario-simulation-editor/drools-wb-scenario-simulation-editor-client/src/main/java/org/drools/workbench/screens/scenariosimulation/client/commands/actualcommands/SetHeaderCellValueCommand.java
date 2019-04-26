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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.enterprise.context.Dependent;

import org.drools.workbench.screens.scenariosimulation.client.commands.ScenarioSimulationContext;
import org.drools.workbench.screens.scenariosimulation.model.FactMapping;
import org.drools.workbench.screens.scenariosimulation.model.typedescriptor.FactModelTree;

/**
 * <code>Command</code> to to set the <i>value</i> of a header' cell
 */
@Dependent
public class SetHeaderCellValueCommand extends AbstractScenarioSimulationCommand {

    protected boolean isInstanceHeader;
    protected boolean isPropertyHeader;

    public SetHeaderCellValueCommand() {
        this(false, false);
    }

    public SetHeaderCellValueCommand(boolean isInstanceHeader, boolean isPropertyHeader) {
        super(true);
        this.isInstanceHeader = isInstanceHeader;
        this.isPropertyHeader = isPropertyHeader;
    }

    @Override
    protected void internalExecute(ScenarioSimulationContext context) throws Exception {
        final ScenarioSimulationContext.Status status = context.getStatus();
        String headerCellValue = status.getHeaderCellValue();
        boolean valid = false;
        if (isInstanceHeader) {
            valid = validateInstanceHeader(context, headerCellValue, status.getColumnIndex());
        }  else if (isPropertyHeader) {
            valid = validatePropertyHeader(context, headerCellValue, status.getColumnIndex());
        }
        if (valid) {
            context.getModel().updateHeader(status.getColumnIndex(), status.getRowIndex(), headerCellValue);
        } else {
            throw new Exception("Name \"" + String.join(".", headerCellValue) + "\" cannot be used");
        }
    }

    protected boolean validateInstanceHeader(ScenarioSimulationContext context, String headerCellValue, int columnIndex) {
        List<String> instanceNameElements = Collections.unmodifiableList(Arrays.asList(headerCellValue.split("\\.")));
        boolean isADataType = instanceNameElements.size() == 1 && context.getDataObjectFieldsMap().containsKey(instanceNameElements.get(0));
        return context.getModel().validateInstanceHeaderUpdate(headerCellValue, columnIndex, isADataType);
    }

    protected boolean validatePropertyHeader(ScenarioSimulationContext context, String headerCellValue, int columnIndex) {
        List<String> propertyNameElements = Collections.unmodifiableList(Arrays.asList(headerCellValue.split("\\.")));
        final FactMapping factMappingByIndex = context.getStatus().getSimulation().getSimulationDescriptor().getFactMappingByIndex(columnIndex);
        String className = factMappingByIndex.getFactIdentifier().getClassNameWithoutPackage();
        final FactModelTree factModelTree = context.getDataObjectFieldsMap().get(className);
        boolean isPropertyType = factModelTree != null && recursivelyFindIsPropertyType(context, factModelTree, propertyNameElements);
        return context.getModel().validatePropertyHeaderUpdate(headerCellValue, columnIndex, isPropertyType);
    }

    protected boolean recursivelyFindIsPropertyType(ScenarioSimulationContext context, FactModelTree factModelTree, List<String> propertyNameElements) {
        boolean toReturn =  propertyNameElements.size() == 1 && (factModelTree.getSimpleProperties().containsKey(propertyNameElements.get(0)) || factModelTree.getExpandableProperties().containsKey(propertyNameElements.get(0)));
        if (!toReturn && propertyNameElements.size() > 1) {
            String propertyParent = propertyNameElements.get(0);
            if (factModelTree.getExpandableProperties().containsKey(propertyParent)) {
                List<String> nestedPropertyNameElements = propertyNameElements.subList(1, propertyNameElements.size());
                String className = factModelTree.getExpandableProperties().get(propertyParent);
                final FactModelTree nestedFactModelTree = context.getDataObjectFieldsMap().get(className);
                toReturn =  recursivelyFindIsPropertyType(context, nestedFactModelTree, nestedPropertyNameElements);
            }
        }
        return toReturn;
    }

}
