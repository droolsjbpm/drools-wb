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

package org.drools.workbench.screens.scenariosimulation.client.rightpanel;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.user.client.ui.IsWidget;
import org.drools.workbench.screens.scenariosimulation.model.ScenarioSimulationModel;
import org.uberfire.client.mvp.HasPresenter;

public interface SettingsView
        extends IsWidget,
                HasPresenter<SettingsView.Presenter> {


    Presenter getPresenter();

    interface Presenter {
        void setScenarioType(ScenarioSimulationModel.Type scenarioType);
    }

    SpanElement getScenarioType();

    DivElement getRuleSettings();

    DivElement getDmnSettings();

}
