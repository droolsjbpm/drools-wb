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

import org.kie.workbench.common.command.client.CommandResult;
import org.uberfire.client.mvp.PlaceStatus;

@Dependent
public class RightPanelMenuCommand extends AbstractScenarioSimulationCommand {

//    private PlaceManager placeManager;
//
//    PlaceRequest rightPanelRequest;
//
//    public RightPanelMenuCommand() {
//    }
//
//    @Inject
//    public RightPanelMenuCommand(PlaceManager placeManager) {
//        this.placeManager = placeManager;
//    }

//    public void init(PlaceRequest rightPanelRequest) {
//        this.rightPanelRequest = rightPanelRequest;
//    }

    @Override
    public CommandResult<ScenarioSimulationViolation> execute(ScenarioSimulationContext context) {
        if (PlaceStatus.OPEN.equals(context.getPlaceManager().getStatus(context.getRightPanelRequest()))) {
            context.getPlaceManager().closePlace(context.getRightPanelRequest());
        } else {
            context.getPlaceManager().goTo(context.getRightPanelRequest());
        }
        return commonExecution(context);
    }

}
