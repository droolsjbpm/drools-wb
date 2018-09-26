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

import org.drools.workbench.screens.scenariosimulation.client.rightpanel.RightPanelView;
import org.uberfire.mvp.Command;

/**
 * <code>Command</code> to <b>enable</b> the <code>RightPanelView</code>
 */
@Dependent
public class EnableRightPanelCommand implements Command {

    private RightPanelView.Presenter rightPanelPresenter;

    public EnableRightPanelCommand() {
    }

    /**
     * @param rightPanelPresenter
     */
    public EnableRightPanelCommand(RightPanelView.Presenter rightPanelPresenter) {
        this.rightPanelPresenter = rightPanelPresenter;
    }

    @Override
    public void execute() {
        rightPanelPresenter.onEnableEditorTab();
    }
}
