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
package org.drools.workbench.screens.scenariosimulation.client.producers;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.drools.workbench.screens.scenariosimulation.client.commands.ScenarioSimulationContext;
import org.drools.workbench.screens.scenariosimulation.client.factories.CollectionEditorSingletonDOMElementFactory;
import org.drools.workbench.screens.scenariosimulation.client.factories.ScenarioCellTextAreaSingletonDOMElementFactory;
import org.drools.workbench.screens.scenariosimulation.client.factories.ScenarioHeaderTextBoxSingletonDOMElementFactory;
import org.drools.workbench.screens.scenariosimulation.client.menu.ScenarioContextMenuRegistry;
import org.drools.workbench.screens.scenariosimulation.client.models.ScenarioGridModel;
import org.drools.workbench.screens.scenariosimulation.client.renderers.ScenarioGridRenderer;
import org.drools.workbench.screens.scenariosimulation.client.utils.ViewsProvider;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGrid;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridLayer;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridPanel;

/**
 * <code>@Dependent</code> <i>Producer</i> for a given {@link ScenarioGridPanel}
 */
@Dependent
public class ScenarioGridPanelProducer {

    @Inject
    protected ScenarioContextMenuRegistry scenarioContextMenuRegistry;

    @Inject
    protected ScenarioGridLayer scenarioMainGridLayer;

    @Inject
    protected ScenarioGridPanel scenarioMainGridPanel;

    @Inject
    protected ScenarioGridLayer scenarioBackgroundGridLayer;

    @Inject
    protected ScenarioGridPanel scenarioBackgroundGridPanel;

    @Inject
    protected ViewsProvider viewsProvider;

    @PostConstruct
    public void init() {
        initializeGrid(scenarioMainGridLayer, scenarioMainGridPanel);
        initializeGrid(scenarioBackgroundGridLayer, scenarioBackgroundGridPanel);
    }

    private void initializeGrid(ScenarioGridLayer scenarioGridLayer, ScenarioGridPanel scenarioGridPanel) {
        ScenarioGridModel scenarioGridModel = new ScenarioGridModel(false);
        final ScenarioGrid scenarioGrid = new ScenarioGrid(scenarioGridModel,
                                                           scenarioGridLayer,
                                                           new ScenarioGridRenderer(false),
                                                           scenarioContextMenuRegistry);
        scenarioGrid.setDraggable(false);
        scenarioGridLayer.addScenarioGrid(scenarioGrid);
        scenarioGridPanel.add(scenarioGridLayer);
        ScenarioSimulationContext scenarioSimulationContext = new ScenarioSimulationContext(scenarioGridPanel);
        scenarioGrid.setScenarioSimulationContext(scenarioSimulationContext);
        scenarioGridModel.setCollectionEditorSingletonDOMElementFactory(
                new CollectionEditorSingletonDOMElementFactory(scenarioGridPanel,
                                                               scenarioGridLayer,
                                                               scenarioGridLayer.getScenarioGrid(),
                                                               scenarioSimulationContext,
                                                               viewsProvider));
        scenarioGridModel.setScenarioCellTextAreaSingletonDOMElementFactory(
                new ScenarioCellTextAreaSingletonDOMElementFactory(scenarioGridPanel,
                                                                   scenarioGridLayer,
                                                                   scenarioGridLayer.getScenarioGrid()));
        scenarioGridModel.setScenarioHeaderTextBoxSingletonDOMElementFactory(
                new ScenarioHeaderTextBoxSingletonDOMElementFactory(scenarioGridPanel,
                                                                    scenarioGridLayer,
                                                                    scenarioGridLayer.getScenarioGrid()));
    }

    public ScenarioGridPanel getScenarioMainGridPanel() {
        return scenarioMainGridPanel;
    }

    public ScenarioGridPanel getScenarioBackgroundGridPanel() {
        return scenarioBackgroundGridPanel;
    }

    public ScenarioContextMenuRegistry getScenarioContextMenuRegistry() {
        return scenarioContextMenuRegistry;
    }
}