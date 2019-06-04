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

package org.drools.workbench.screens.scenariosimulation.submarine.client.editor;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.IsWidget;
import elemental2.promise.Promise;
import org.drools.emf.models.scesim.util.Scesim2Resource;
import org.drools.scenariosimulation.api.model.Scenario;
import org.drools.scenariosimulation.api.model.ScenarioSimulationModel;
import org.drools.scenariosimulation.api.model.ScenarioWithIndex;
import org.drools.scenariosimulation.api.model.SimulationDescriptor;
import org.drools.workbench.screens.scenariosimulation.client.editor.ScenarioSimulationEditorPresenter;
import org.drools.workbench.screens.scenariosimulation.client.editor.ScenarioSimulationEditorWrapper;
import org.drools.workbench.screens.scenariosimulation.client.editor.strategies.DataManagementStrategy;
import org.drools.workbench.screens.scenariosimulation.submarine.client.editor.strategies.SubmarineDMNDataManagementStrategy;
import org.drools.workbench.screens.scenariosimulation.submarine.client.editor.strategies.SubmarineDMODataManagementStrategy;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.submarine.client.editor.MultiPageEditorContainerPresenter;
import org.kie.workbench.common.submarine.client.editor.MultiPageEditorContainerView;
import org.kie.workbench.common.widgets.client.docks.AuthoringEditorDock;
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilder;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.backend.vfs.impl.ObservablePathImpl;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.PlaceStatus;
import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.menu.Menus;

@Dependent
/**
 * Wrapper to be used inside Submarine
 */
public class ScenarioSimulationEditorSubmarineWrapper extends MultiPageEditorContainerPresenter<ScenarioSimulationModel> implements ScenarioSimulationEditorWrapper {

    protected ScenarioSimulationEditorPresenter scenarioSimulationEditorPresenter;
//    protected Caller<ScenarioSimulationSubmarineService> service;
    private FileMenuBuilder fileMenuBuilder;
    private AuthoringEditorDock authoringWorkbenchDocks;
    private Scesim2Resource scesim2Resource = new Scesim2Resource();

    public ScenarioSimulationEditorSubmarineWrapper() {
        //Zero-parameter constructor for CDI proxies
    }

    @Inject
    public ScenarioSimulationEditorSubmarineWrapper(/*final Caller<ScenarioSimulationSubmarineService> service,*/
                                                    final ScenarioSimulationEditorPresenter scenarioSimulationEditorPresenter,
                                                    final FileMenuBuilder fileMenuBuilder,
                                                    final PlaceManager placeManager,
                                                    final MultiPageEditorContainerView multiPageEditorContainerView,
                                                    final AuthoringEditorDock authoringWorkbenchDocks) {
        super(scenarioSimulationEditorPresenter.getView(), fileMenuBuilder, placeManager, multiPageEditorContainerView);
//        this.service = service;
        this.scenarioSimulationEditorPresenter = scenarioSimulationEditorPresenter;
        this.fileMenuBuilder = fileMenuBuilder;
        this.authoringWorkbenchDocks = authoringWorkbenchDocks;
    }

    @Override
    protected void buildMenuBar() {
        GWT.log(this.toString() + " buildMenuBar");
        setMenus(fileMenuBuilder.build());
        getMenus().getItemsMap().values().forEach(menuItem -> menuItem.setEnabled(true));
    }

    @Override
    public Promise getContent() {
        GWT.log(this.toString() + " getContent");
        return null;
    }

    @Override
    public void setContent(String value) {
        GWT.log(this.toString() + " setContent ");
        // TODO WORKAROUND
        getWidget().init(this);
        Path path = new PathFactory.PathImpl("new.scesim", "file:///new.scesim");
        scenarioSimulationEditorPresenter.init(this, new ObservablePathImpl().wrap(path));
        scenarioSimulationEditorPresenter.showDocks(PlaceStatus.CLOSE);
        unmarshallContent(value);
//        getModelSuccessCallbackMethod(service.unmarshal(value));
//        service.call((RemoteCallback<ScenarioSimulationModel>) this::getModelSuccessCallbackMethod).unmarshal(value);
    }

    @Override
    public void resetContentHash() {
        GWT.log(this.toString() + " resetContentHash");
    }

    public void onStartup(final PlaceRequest place) {
        GWT.log(this.toString() + " onStartup " + place);
        super.init(place);
        authoringWorkbenchDocks.setup("AuthoringPerspective", place);
    }

    public boolean mayClose() {
        GWT.log(this.toString() + " mayClose");
        return !scenarioSimulationEditorPresenter.isDirty();
    }

    public IsWidget getTitle() {
        GWT.log(this.toString() + " getTitle");
        return super.getTitle();
    }

    public MultiPageEditorContainerView getWidget() {
        GWT.log(this.toString() + " getWidget");
        return super.getWidget();
    }

    public void setMenus(final Consumer<Menus> menusConsumer) {
        GWT.log(this.toString() + " setMenus " + menusConsumer);
        menusConsumer.accept(getMenus());
    }

    @Override
    public void onRunScenario(RemoteCallback<Map<Integer, Scenario>> refreshModelCallback, HasBusyIndicatorDefaultErrorCallback hasBusyIndicatorDefaultErrorCallback, SimulationDescriptor simulationDescriptor, List<ScenarioWithIndex> toRun) {
        throw new UnsupportedOperationException("Not available in Submarine");
    }

    @Override
    public void wrappedSave(String commitMessage) {
        GWT.log(this.toString() + " wrappedSave " + commitMessage);
//        save(commitMessage);
    }

    @Override
    public Integer getOriginalHash() {
        return super.getOriginalContentHash();
    }

    /**
     * If you want to customize the menu override this method.
     */
    @Override
    protected void makeMenuBar() {
        GWT.log(this.toString() + " makeMenuBar");
        scenarioSimulationEditorPresenter.makeMenuBar(fileMenuBuilder);
//        fileMenuBuilder.addNewTopLevelMenu(new ScenarioMenuItem("Load", this::loadAsset));
//        fileMenuBuilder.addNewTopLevelMenu(new ScenarioMenuItem("New", this::createNewAsset));
    }

    @Override
    protected Supplier<ScenarioSimulationModel> getContentSupplier() {
        return scenarioSimulationEditorPresenter.getContentSupplier();
    }

    protected void unmarshallContent(String toUnmarshal) {
        GWT.log(this.toString() + " unmarshallContent");
        try {
            final org.drools.emf.models.scesim.ScenarioSimulationModel unmarshall = scesim2Resource.unmarshall(toUnmarshal);
            GWT.log(unmarshall.toString());
        } catch (IOException e) {
            GWT.log(this.toString() + e.getMessage());
        }
    }

    protected void getModelSuccessCallbackMethod(ScenarioSimulationModel model) {
        scenarioSimulationEditorPresenter.setPackageName("com");
        resetEditorPages();
        DataManagementStrategy dataManagementStrategy;
        if (ScenarioSimulationModel.Type.RULE.equals(model.getSimulation().getSimulationDescriptor().getType())) {
            dataManagementStrategy = new SubmarineDMODataManagementStrategy(scenarioSimulationEditorPresenter.getContext());
        } else {
            dataManagementStrategy = new SubmarineDMNDataManagementStrategy(scenarioSimulationEditorPresenter.getContext(), scenarioSimulationEditorPresenter.getEventBus());
        }
        // TODO CHECK
//        dataManagementStrategy.manageScenarioSimulationModelContent(null, content);
        scenarioSimulationEditorPresenter.getModelSuccessCallbackMethod(dataManagementStrategy, model);
    }

//    protected void loadAsset() {
//        GWT.log(this.toString() + " loadAsset");
//        fileChooserPopupPresenter.show("Choose", "Choose", this::loadAssetCommand);
//    }
//
//    protected void loadAssetCommand() {
//        String fileName = fileChooserPopupPresenter.getFileName();
//        setContent(scesimFilesProvider.getScesimFile(fileChooserPopupPresenter.getFileName()));
//        Path path = new PathFactory.PathImpl(fileName, "file:///" + fileName);
//        scenarioSimulationEditorPresenter.init(this, new ObservablePathImpl().wrap(path));
//        scenarioSimulationEditorPresenter.showDocks(PlaceStatus.CLOSE);
//    }
//
//    protected void createNewAsset() {
//        GWT.log(this.toString() + " createNewAsset");
//        setContent(scesimFilesProvider.getNewScesimRule());
//        Path path = new PathFactory.PathImpl("new.scesim", "file:///new.scesim");
//        scenarioSimulationEditorPresenter.init(this, new ObservablePathImpl().wrap(path));
//        scenarioSimulationEditorPresenter.showDocks(PlaceStatus.CLOSE);
//    }
}
