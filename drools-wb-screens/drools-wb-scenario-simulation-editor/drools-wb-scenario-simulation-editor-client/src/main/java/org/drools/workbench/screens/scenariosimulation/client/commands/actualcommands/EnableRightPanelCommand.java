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

import org.drools.workbench.screens.scenariosimulation.client.commands.ScenarioSimulationContext;
import org.drools.workbench.screens.scenariosimulation.client.commands.ScenarioSimulationViolation;
import org.kie.workbench.common.command.client.CommandResult;

/**
 * <code>Command</code> to <b>enable</b> the <code>RightPanelView</code>
 */
@Dependent
public class EnableRightPanelCommand extends AbstractScenarioSimulationCommand {

    @Override
    public CommandResult<ScenarioSimulationViolation> execute(ScenarioSimulationContext context) {
        if (context.getScenarioSimulationEditorPresenter() != null) {
            context.getScenarioSimulationEditorPresenter().expandToolsDock();
        }
        if (context.getRightPanelPresenter() != null) {
            if (context.getFilterTerm() == null) {
                context.getRightPanelPresenter().onEnableEditorTab();
            } else {
                context.getRightPanelPresenter().onEnableEditorTab(context.getFilterTerm(), context.getPropertyName(), context.isNotEqualsSearch());
            }
        }
        return commonExecution(context);
    }
}
