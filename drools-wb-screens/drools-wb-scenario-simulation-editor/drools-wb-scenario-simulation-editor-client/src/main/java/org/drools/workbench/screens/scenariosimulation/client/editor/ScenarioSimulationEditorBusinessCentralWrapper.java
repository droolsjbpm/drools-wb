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

package org.drools.workbench.screens.scenariosimulation.client.editor;

import java.util.Map;
import java.util.function.Supplier;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import elemental2.promise.Promise;
import org.drools.workbench.screens.scenariosimulation.client.editor.strategies.DMNDataManagementStrategy;
import org.drools.workbench.screens.scenariosimulation.client.editor.strategies.DMODataManagementStrategy;
import org.drools.workbench.screens.scenariosimulation.client.editor.strategies.DataManagementStrategy;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.TestToolsPresenter;
import org.drools.workbench.screens.scenariosimulation.model.Scenario;
import org.drools.workbench.screens.scenariosimulation.model.ScenarioSimulationModel;
import org.drools.workbench.screens.scenariosimulation.model.ScenarioSimulationModelContent;
import org.drools.workbench.screens.scenariosimulation.model.SimulationDescriptor;
import org.drools.workbench.screens.scenariosimulation.service.DMNTypeService;
import org.drools.workbench.screens.scenariosimulation.service.ScenarioSimulationService;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracleFactory;
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilder;
import org.kie.workbench.common.widgets.configresource.client.widget.bound.ImportsWidgetPresenter;
import org.kie.workbench.common.widgets.metadata.client.KieEditor;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartTitleDecoration;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.ext.editor.commons.service.support.SupportsCopy;
import org.uberfire.ext.editor.commons.service.support.SupportsDelete;
import org.uberfire.ext.editor.commons.service.support.SupportsRename;
import org.uberfire.ext.editor.commons.service.support.SupportsSaveAndRename;
import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnMayClose;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.menu.Menus;

import static org.drools.workbench.screens.scenariosimulation.client.editor.ScenarioSimulationEditorPresenter.IDENTIFIER;

@Dependent
@WorkbenchScreen(identifier = IDENTIFIER)
/**
 * Wrapper to be used inside Business Central
 */
public class ScenarioSimulationEditorBusinessCentralWrapper extends KieEditor<ScenarioSimulationModel> implements ScenarioSimulationEditorWrapper {

    protected ScenarioSimulationEditorPresenter scenarioSimulationEditorPresenter;

    private ImportsWidgetPresenter importsWidget;
    private AsyncPackageDataModelOracleFactory oracleFactory;
    private Caller<ScenarioSimulationService> service;
    private Caller<DMNTypeService> dmnTypeService;

    public ScenarioSimulationEditorBusinessCentralWrapper() {
        //Zero-parameter constructor for CDI proxies
    }

    @Inject
    public ScenarioSimulationEditorBusinessCentralWrapper(final Caller<ScenarioSimulationService> service,
                                                          final ScenarioSimulationEditorPresenter scenarioSimulationEditorPresenter,
                                                          final ImportsWidgetPresenter importsWidget,
                                                          final AsyncPackageDataModelOracleFactory oracleFactory,
                                                          final PlaceManager placeManager,
                                                          final Caller<DMNTypeService> dmnTypeService) {
        super(scenarioSimulationEditorPresenter.getView());
        this.scenarioSimulationEditorPresenter = scenarioSimulationEditorPresenter;
        this.dmnTypeService = dmnTypeService;
        this.service = service;
        this.importsWidget = importsWidget;
        this.oracleFactory = oracleFactory;
        this.placeManager = placeManager;
    }

    @OnStartup
    public void onStartup(final ObservablePath path,
                          final PlaceRequest place) {
        super.init(path,
                   place,
                   type);
        scenarioSimulationEditorPresenter.init(this, path);
    }

    @OnClose
    @Override
    public void onClose() {
        this.versionRecordManager.clear();
        scenarioSimulationEditorPresenter.onClose();
        super.onClose();
    }

    @OnMayClose
    public boolean mayClose() {
        return !scenarioSimulationEditorPresenter.isDirty();
    }

    @WorkbenchPartTitle
    public String getTitleText() {
        return super.getTitleText();
    }

    @WorkbenchPartTitleDecoration
    public IsWidget getTitle() {
        return super.getTitle();
    }

    @WorkbenchPartView
    public IsWidget getWidget() {
        return super.getWidget();
    }

    @WorkbenchMenu
    public Menus getMenus() {
        return menus;
    }

    @Override
    public void showDocks() {
        super.showDocks();
        final DefaultPlaceRequest placeRequest = new DefaultPlaceRequest(TestToolsPresenter.IDENTIFIER);
        scenarioSimulationEditorPresenter.showDocks(placeManager.getStatus(placeRequest));
        registerTestToolsCallback();
    }

    @Override
    public void hideDocks() {
        super.hideDocks();
        scenarioSimulationEditorPresenter.hideDocks();
        unRegisterTestToolsCallback();
    }

    @Override
    public void onRunScenario(RemoteCallback<Map<Integer, Scenario>> refreshModelCallback, HasBusyIndicatorDefaultErrorCallback hasBusyIndicatorDefaultErrorCallback, SimulationDescriptor simulationDescriptor, Map<Integer, Scenario> scenarioMap) {
        service.call(refreshModelCallback, hasBusyIndicatorDefaultErrorCallback)
                .runScenario(versionRecordManager.getCurrentPath(),
                             simulationDescriptor,
                             scenarioMap);
    }

    @Override
    public void wrappedSave(String commitMessage) {
        save(commitMessage);
    }

    @Override
    public Integer getOriginalHash() {
        return originalHash;
    }

    @Override
    public void addDownloadMenuItem(FileMenuBuilder fileMenuBuilder) {
        scenarioSimulationEditorPresenter.addDownloadMenuItem(fileMenuBuilder, getPathSupplier());
    }

    protected void registerTestToolsCallback() {
        placeManager.registerOnOpenCallback(new DefaultPlaceRequest(TestToolsPresenter.IDENTIFIER), scenarioSimulationEditorPresenter.getPopulateTestToolsCommand());
    }

    protected void unRegisterTestToolsCallback() {
        placeManager.getOnOpenCallbacks(new DefaultPlaceRequest(TestToolsPresenter.IDENTIFIER)).remove(scenarioSimulationEditorPresenter.getPopulateTestToolsCommand());
    }

    /**
     * If you want to customize the menu override this method.
     */
    @Override
    protected Promise<Void> makeMenuBar() {
        scenarioSimulationEditorPresenter.makeMenuBar(fileMenuBuilder);
        return super.makeMenuBar();
    }

    @Override
    protected Supplier<ScenarioSimulationModel> getContentSupplier() {
        return scenarioSimulationEditorPresenter.getContentSupplier();
    }

    @Override
    protected void save(final String commitMessage) {
        final ScenarioSimulationModel model = scenarioSimulationEditorPresenter.getModel();
        RemoteCallback<Path> saveSuccessCallback = getSaveSuccessCallback(scenarioSimulationEditorPresenter.getJsonModel(model).hashCode());
        service.call(saveSuccessCallback,
                     new HasBusyIndicatorDefaultErrorCallback(scenarioSimulationEditorPresenter.getView())).save(versionRecordManager.getCurrentPath(),
                                                                                                                 model,
                                                                                                                 metadata,
                                                                                                                 commitMessage);
    }

    @Override
    protected void addCommonActions(final FileMenuBuilder fileMenuBuilder) {
        scenarioSimulationEditorPresenter.addCommonActions(fileMenuBuilder, versionRecordManager.buildMenu(), alertsButtonMenuItemBuilder.build());
    }

    @Override
    protected void loadContent() {
        scenarioSimulationEditorPresenter.loadContent();
        service.call(getModelSuccessCallback(),
                     getNoSuchFileExceptionErrorCallback()).loadContent(versionRecordManager.getCurrentPath());
    }

    @Override
    protected Caller<? extends SupportsDelete> getDeleteServiceCaller() {
        return service;
    }

    @Override
    protected Caller<? extends SupportsRename> getRenameServiceCaller() {
        return service;
    }

    @Override
    protected Caller<? extends SupportsCopy> getCopyServiceCaller() {
        return service;
    }

    @Override
    protected Caller<? extends SupportsSaveAndRename<ScenarioSimulationModel, Metadata>> getSaveAndRenameServiceCaller() {
        return service;
    }

    @Override
    protected String getEditorIdentifier() {
        return IDENTIFIER;
    }

    protected void getModelSuccessCallbackMethod(ScenarioSimulationModelContent content) {
        //Path is set to null when the Editor is closed (which can happen before async calls complete).
        if (versionRecordManager.getCurrentPath() == null) {
            return;
        }
        scenarioSimulationEditorPresenter.setPackageName(content.getDataModel().getPackageName());
        resetEditorPages(content.getOverview());
        DataManagementStrategy dataManagementStrategy;
        if (ScenarioSimulationModel.Type.RULE.equals(content.getModel().getSimulation().getSimulationDescriptor().getType())) {
            dataManagementStrategy = new DMODataManagementStrategy(oracleFactory, scenarioSimulationEditorPresenter.getContext());
        } else {
            dataManagementStrategy = new DMNDataManagementStrategy(dmnTypeService, scenarioSimulationEditorPresenter.getContext(), scenarioSimulationEditorPresenter.getEventBus());
        }
        dataManagementStrategy.manageScenarioSimulationModelContent(versionRecordManager.getCurrentPath(), content);
        ScenarioSimulationModel model = content.getModel();
        if (dataManagementStrategy instanceof DMODataManagementStrategy) {
            importsWidget.setContent(((DMODataManagementStrategy) dataManagementStrategy).getOracle(),
                                     model.getImports(),
                                     isReadOnly);
            addImportsTab(importsWidget);
        }
        baseView.hideBusyIndicator();
        setOriginalHash(scenarioSimulationEditorPresenter.getJsonModel(model).hashCode());
        scenarioSimulationEditorPresenter.getModelSuccessCallbackMethod(dataManagementStrategy, model);
    }

    private RemoteCallback<ScenarioSimulationModelContent> getModelSuccessCallback() {
        return this::getModelSuccessCallbackMethod;
    }
}
