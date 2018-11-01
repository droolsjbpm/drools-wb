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
package org.drools.workbench.screens.scenariosimulation.client.commands;

import javax.enterprise.context.Dependent;

import org.drools.workbench.screens.scenariosimulation.client.models.ScenarioGridModel;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridColumn;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridLayer;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridPanel;
import org.drools.workbench.screens.scenariosimulation.model.FactIdentifier;
import org.drools.workbench.screens.scenariosimulation.model.FactMapping;
import org.drools.workbench.screens.scenariosimulation.model.FactMappingType;

/**
 * <code>Command</code> to <b>prepend</b> (i.e. put in the first position) a column to a given <i>group</i>
 */
@Dependent
public class PrependColumnCommand extends AbstractCommand {

    private ScenarioGridModel model;
    private String columnId;
    private String columnGroup;

    public PrependColumnCommand() {
    }

    public PrependColumnCommand(ScenarioGridModel model, String columnId, String columnGroup, ScenarioGridPanel scenarioGridPanel, ScenarioGridLayer scenarioGridLayer) {
        super(scenarioGridPanel, scenarioGridLayer);
        this.model = model;
        this.columnId = columnId;
        this.columnGroup = columnGroup;
    }

    @Override
    public void execute() {
        final int index = model.getFirstIndexLeftOfGroup(columnGroup);
        FactMappingType factMappingType = FactMappingType.valueOf(columnGroup.toUpperCase());
        final int nextColumnCount = model.nextColumnCount();
        String instanceTitle = FactMapping.getInstancePlaceHolder(nextColumnCount);
        String propertyTitle = FactMapping.getPropertyPlaceHolder(nextColumnCount);
        final ScenarioGridColumn scenarioGridColumnLocal = getScenarioGridColumnLocal(instanceTitle,
                                                                                      propertyTitle,
                                                                                      columnId,
                                                                                      columnGroup,
                                                                                      factMappingType,
                                                                                      scenarioGridPanel,
                                                                                      scenarioGridLayer,
                                                                                      ScenarioSimulationEditorConstants.INSTANCE.defineValidType());
        scenarioGridColumnLocal.setFactIdentifier(FactIdentifier.EMPTY);
        model.insertColumn(index, scenarioGridColumnLocal);
    }
}
