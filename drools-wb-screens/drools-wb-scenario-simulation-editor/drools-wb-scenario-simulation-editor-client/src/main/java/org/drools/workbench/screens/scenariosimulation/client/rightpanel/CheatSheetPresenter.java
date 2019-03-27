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

package org.drools.workbench.screens.scenariosimulation.client.rightpanel;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.drools.workbench.screens.scenariosimulation.model.ScenarioSimulationModel;
import org.uberfire.client.annotations.DefaultPosition;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.workbench.model.CompassPosition;
import org.uberfire.workbench.model.Position;

import static org.drools.workbench.screens.scenariosimulation.client.rightpanel.CheatSheetPresenter.DEFAULT_PREFERRED_WIDHT;
import static org.drools.workbench.screens.scenariosimulation.client.rightpanel.CheatSheetPresenter.IDENTIFIER;

@Dependent
@WorkbenchScreen(identifier = IDENTIFIER, preferredWidth = DEFAULT_PREFERRED_WIDHT)
public class CheatSheetPresenter implements CheatSheetView.Presenter {

    public static final int DEFAULT_PREFERRED_WIDHT = 300;

    public static final String IDENTIFIER = "org.drools.scenariosimulation.CheatSheet";

    private CheatSheetView view;


    public CheatSheetPresenter() {
        //Zero argument constructor for CDI
    }

    @Inject
    public CheatSheetPresenter(CheatSheetView view) {
        this.view = view;
    }

    @PostConstruct
    public void setup() {
        view.init(this);
    }

    @DefaultPosition
    public Position getDefaultPosition() {
        return CompassPosition.EAST;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return ScenarioSimulationEditorConstants.INSTANCE.scenarioCheatSheet();
    }

    @WorkbenchPartView
    public Widget asWidget() {
        return view.asWidget();
    }

    public void initCheatSheet(ScenarioSimulationModel.Type type) {
        if (type.equals(ScenarioSimulationModel.Type.RULE)) {
            view.setRuleCheatSheetContent();
        }
        if (type.equals(ScenarioSimulationModel.Type.DMN)) {
            view.setDMNCheatSheetContent();
        }
    }
}
