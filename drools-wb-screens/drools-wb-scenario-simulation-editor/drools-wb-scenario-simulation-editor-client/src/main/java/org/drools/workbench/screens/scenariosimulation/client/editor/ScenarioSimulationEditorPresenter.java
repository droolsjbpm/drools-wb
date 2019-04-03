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

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.IsWidget;
import elemental2.dom.DomGlobal;
import org.drools.workbench.screens.scenariosimulation.client.commands.ScenarioSimulationContext;
import org.drools.workbench.screens.scenariosimulation.client.editor.strategies.DMNDataManagementStrategy;
import org.drools.workbench.screens.scenariosimulation.client.editor.strategies.DMODataManagementStrategy;
import org.drools.workbench.screens.scenariosimulation.client.editor.strategies.DataManagementStrategy;
import org.drools.workbench.screens.scenariosimulation.client.events.RedoEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.ReloadSimulationEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.UndoEvent;
import org.drools.workbench.screens.scenariosimulation.client.handlers.ScenarioSimulationDocksHandler;
import org.drools.workbench.screens.scenariosimulation.client.popup.CustomBusyPopup;
import org.drools.workbench.screens.scenariosimulation.client.producers.ScenarioSimulationProducer;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.RightPanelPresenter;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.RightPanelView;
import org.drools.workbench.screens.scenariosimulation.client.type.ScenarioSimulationResourceType;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridPanel;
import org.drools.workbench.screens.scenariosimulation.model.Scenario;
import org.drools.workbench.screens.scenariosimulation.model.ScenarioSimulationModel;
import org.drools.workbench.screens.scenariosimulation.model.ScenarioSimulationModelContent;
import org.drools.workbench.screens.scenariosimulation.model.Simulation;
import org.drools.workbench.screens.scenariosimulation.service.DMNTypeService;
import org.drools.workbench.screens.scenariosimulation.service.ImportExportService;
import org.drools.workbench.screens.scenariosimulation.service.ImportExportType;
import org.drools.workbench.screens.scenariosimulation.service.ScenarioSimulationService;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.jboss.errai.bus.client.api.base.DefaultErrorCallback;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.enterprise.client.jaxrs.MarshallingWrapper;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracleFactory;
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilder;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.kie.workbench.common.widgets.configresource.client.widget.bound.ImportsWidgetPresenter;
import org.kie.workbench.common.widgets.metadata.client.KieEditor;
import org.kie.workbench.common.workbench.client.test.TestRunnerReportingScreen;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartTitleDecoration;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.mvp.AbstractWorkbenchActivity;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.PlaceStatus;
import org.uberfire.ext.editor.commons.service.support.SupportsCopy;
import org.uberfire.ext.editor.commons.service.support.SupportsDelete;
import org.uberfire.ext.editor.commons.service.support.SupportsRename;
import org.uberfire.ext.editor.commons.service.support.SupportsSaveAndRename;
import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnMayClose;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.menu.Menus;

import static org.drools.workbench.screens.scenariosimulation.client.editor.ScenarioSimulationEditorPresenter.IDENTIFIER;

@Dependent
@WorkbenchEditor(identifier = IDENTIFIER, supportedTypes = {ScenarioSimulationResourceType.class})
public class ScenarioSimulationEditorPresenter
        extends KieEditor<ScenarioSimulationModel> {

    public static final String IDENTIFIER = "ScenarioSimulationEditor";

    private static final NativeEvent CLICK_EVENT = Document.get().createClickEvent(0, 0, 0, 0, 0, false, false, false, false);

    //Package for which this Scenario Simulation relates
    protected String packageName = "";

    protected ObservablePath path;

    protected EventBus eventBus;

    protected ScenarioGridPanel scenarioGridPanel;

    protected DataManagementStrategy dataManagementStrategy;
    protected ScenarioSimulationContext context;
    private ImportsWidgetPresenter importsWidget;
    private AsyncPackageDataModelOracleFactory oracleFactory;
    protected ScenarioSimulationModel model;
    private Caller<ScenarioSimulationService> service;
    private Caller<DMNTypeService> dmnTypeService;
    private Caller<ImportExportService> importExportService;
    private ScenarioSimulationResourceType type;
    private ScenarioSimulationView view;
    private Command populateRightPanelCommand;

    private TestRunnerReportingScreen testRunnerReportingScreen;

    private ScenarioSimulationDocksHandler scenarioSimulationDocksHandler;

    public ScenarioSimulationEditorPresenter() {
        //Zero-parameter constructor for CDI proxies
    }

    @Inject
    public ScenarioSimulationEditorPresenter(final Caller<ScenarioSimulationService> service,
                                             final ScenarioSimulationProducer scenarioSimulationProducer,
                                             final ScenarioSimulationResourceType type,
                                             final ImportsWidgetPresenter importsWidget,
                                             final AsyncPackageDataModelOracleFactory oracleFactory,
                                             final PlaceManager placeManager,
                                             final TestRunnerReportingScreen testRunnerReportingScreen,
                                             final ScenarioSimulationDocksHandler scenarioSimulationDocksHandler,
                                             final Caller<DMNTypeService> dmnTypeService,
                                             final Caller<ImportExportService> importExportService) {
        super(scenarioSimulationProducer.getScenarioSimulationView());
        this.testRunnerReportingScreen = testRunnerReportingScreen;
        this.scenarioSimulationDocksHandler = scenarioSimulationDocksHandler;
        this.dmnTypeService = dmnTypeService;
        this.importExportService = importExportService;
        this.view = (ScenarioSimulationView) baseView;
        this.service = service;
        this.type = type;
        this.importsWidget = importsWidget;
        this.oracleFactory = oracleFactory;
        this.placeManager = placeManager;
        this.context = scenarioSimulationProducer.getScenarioSimulationContext();
        this.eventBus = scenarioSimulationProducer.getEventBus();
        scenarioGridPanel = view.getScenarioGridPanel();
        context.setScenarioSimulationEditorPresenter(this);
        view.init(this);
        populateRightPanelCommand = getPopulateRightPanelCommand();
        scenarioGridPanel.select();
    }

    @OnStartup
    public void onStartup(final ObservablePath path,
                          final PlaceRequest place) {
        super.init(path,
                   place,
                   type);
        this.path = path;
    }

    @OnClose
    @Override
    public void onClose() {
        this.versionRecordManager.clear();
        scenarioGridPanel.unregister();
        super.onClose();
    }

    @OnMayClose
    public boolean mayClose() {
        return !isDirty();
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
        scenarioSimulationDocksHandler.addDocks();
        expandToolsDock();
        registerRightPanelCallback();
        populateRightPanel();
    }

    @Override
    public void hideDocks() {
        super.hideDocks();
        scenarioSimulationDocksHandler.removeDocks();
        view.getScenarioGridLayer().getScenarioGrid().clearSelections();
        unRegisterRightPanelCallback();
        clearRightPanelStatus();
        testRunnerReportingScreen.reset();
    }

    public void expandToolsDock() {
        final DefaultPlaceRequest placeRequest = new DefaultPlaceRequest(RightPanelPresenter.IDENTIFIER);
        if (!PlaceStatus.OPEN.equals(placeManager.getStatus(placeRequest))) {
            scenarioSimulationDocksHandler.expandToolsDock();
        }
    }

    public ScenarioSimulationView getView() {
        return view;
    }

    public ScenarioSimulationModel getModel() {
        return model;
    }

    /**
     * To be called to force right panel reload
     * @param disable set this to <code>true</code> to <b>also</b> disable the panel
     */
    public void reloadRightPanel(boolean disable) {
        populateRightPanelCommand.execute();
        if (disable) {
            getRightPanelPresenter().ifPresent(RightPanelView.Presenter::onDisableEditorTab);
        }
    }

    public void onRunScenario() {
        List<Integer> indexes = IntStream.range(0, context.getStatus().getSimulation().getUnmodifiableScenarios().size())
                .boxed()
                .collect(Collectors.toList());
        onRunScenario(indexes);
    }

    public void onRunScenario(List<Integer> indexOfScenarioToRun) {
        view.getScenarioGridPanel().getScenarioGrid().getModel().resetErrors();
        model.setSimulation(context.getStatus().getSimulation());
        Simulation simulation = model.getSimulation();
        Map<Integer, Scenario> scenarioMap = indexOfScenarioToRun.stream().collect(
                Collectors.toMap(
                        index -> index + 1,
                        simulation::getScenarioByIndex
                )
        );
        service.call(getRefreshModelCallback(), new HasBusyIndicatorDefaultErrorCallback(view))
                .runScenario(versionRecordManager.getCurrentPath(),
                             simulation.getSimulationDescriptor(),
                             scenarioMap);
    }

    public void onUndo() {
        eventBus.fireEvent(new UndoEvent());
    }

    public void onRedo() {
        eventBus.fireEvent(new RedoEvent());
    }

    public void setUndoButtonEnabledStatus(boolean enabled) {
        view.getUndoMenuItem().setEnabled(enabled);
    }

    public void setRedoButtonEnabledStatus(boolean enabled) {
        view.getRedoMenuItem().setEnabled(enabled);
    }

    @Override
    public void addDownloadMenuItem(FileMenuBuilder fileMenuBuilder) {
        fileMenuBuilder.addNewTopLevelMenu(view.getDownloadMenuItem(getPathSupplier()));
    }

    public DataManagementStrategy getDataManagementStrategy() {
        return dataManagementStrategy;
    }

    protected RemoteCallback<Map<Integer, Scenario>> getRefreshModelCallback() {
        return this::refreshModelContent;
    }

    protected void refreshModelContent(Map<Integer, Scenario> newData) {
        if (this.model == null) {
            return;
        }
        Simulation simulation = this.model.getSimulation();
        for (Map.Entry<Integer, Scenario> entry : newData.entrySet()) {
            int index = entry.getKey() - 1;
            simulation.replaceScenario(index, entry.getValue());
        }
        view.refreshContent(simulation);
        context.getStatus().setSimulation(simulation);
        scenarioSimulationDocksHandler.expandTestResultsDock();
        dataManagementStrategy.setModel(model);
    }

    protected void registerRightPanelCallback() {
        placeManager.registerOnOpenCallback(new DefaultPlaceRequest(RightPanelPresenter.IDENTIFIER), populateRightPanelCommand);
    }

    protected void unRegisterRightPanelCallback() {
        placeManager.getOnOpenCallbacks(new DefaultPlaceRequest(RightPanelPresenter.IDENTIFIER)).remove(populateRightPanelCommand);
    }

    /**
     * If you want to customize the menu override this method.
     */
    @Override
    protected void makeMenuBar() {
        fileMenuBuilder.addNewTopLevelMenu(view.getRunScenarioMenuItem());
        fileMenuBuilder.addNewTopLevelMenu(view.getUndoMenuItem());
        fileMenuBuilder.addNewTopLevelMenu(view.getRedoMenuItem());
        fileMenuBuilder.addNewTopLevelMenu(view.getExportToCsvMenuItem());
        fileMenuBuilder.addNewTopLevelMenu(view.getImportFromCsvMenuItem());
        view.getUndoMenuItem().setEnabled(false);
        view.getRedoMenuItem().setEnabled(false);
        super.makeMenuBar();
    }

    @Override
    protected Supplier<ScenarioSimulationModel> getContentSupplier() {
        return () -> model;
    }

    @Override
    protected void save(final String commitMessage) {
        service.call(getSaveSuccessCallback(getJsonModel(model).hashCode()),
                     new HasBusyIndicatorDefaultErrorCallback(baseView)).save(versionRecordManager.getCurrentPath(),
                                                                              model,
                                                                              metadata,
                                                                              commitMessage);
    }

    @Override
    protected void addCommonActions(final FileMenuBuilder fileMenuBuilder) {
        fileMenuBuilder
                .addNewTopLevelMenu(versionRecordManager.buildMenu())
                .addNewTopLevelMenu(alertsButtonMenuItemBuilder.build());
    }

    @Override
    protected void loadContent() {
        CustomBusyPopup.showMessage(CommonConstants.INSTANCE.Loading());
        service.call(getModelSuccessCallback(),
                     getNoSuchFileExceptionErrorCallback()).loadContent(versionRecordManager.getCurrentPath());
    }

    protected void onDownload(final Supplier<Path> pathSupplier) {
        final String downloadURL = getFileDownloadURL(pathSupplier);
        open(downloadURL);
    }

    protected void open(final String downloadURL) {
        DomGlobal.window.open(downloadURL);
    }

    protected void onExportToCsv() {
        importExportService.call(getExportCallBack(),
                                 new DefaultErrorCallback())
                .exportSimulation(ImportExportType.CSV, context.getStatus().getSimulation());
    }

    protected RemoteCallback<Object> getExportCallBack() {
        return rawResult -> {
            final AnchorElement exportAnchorElement = view.getExportAnchorElement((String) rawResult, path.getFileName() + ".csv");
            exportAnchorElement.dispatchEvent(CLICK_EVENT);
            exportAnchorElement.removeFromParent();
        };
    }

    // FIXME to test
    protected void onImportFromCsv() {
        importExportService.call(simulation -> {
                                     eventBus.fireEvent(new ReloadSimulationEvent((Simulation) simulation));
                                 },
                                 new DefaultErrorCallback())
                .importSimulation(ImportExportType.CSV, "", context.getStatus().getSimulation());
    }

    protected void populateRightPanel() {
        // Execute only when DatamanagementStrategy already set and  RightPanelPresenter is actually available
        if (dataManagementStrategy != null) {
            getRightPanelPresenter().ifPresent(presenter -> {
                setRightPanel(presenter);
            });
        }
    }

    protected void setRightPanel(RightPanelView.Presenter presenter) {
        context.setRightPanelPresenter(presenter);
        presenter.setEventBus(eventBus);
        dataManagementStrategy.populateRightPanel(presenter, scenarioGridPanel.getScenarioGrid().getModel());
        ScenarioSimulationModel.Type type = dataManagementStrategy instanceof DMODataManagementStrategy ? ScenarioSimulationModel.Type.RULE : ScenarioSimulationModel.Type.DMN;
        presenter.initCheatSheet(type);
    }

    protected void clearRightPanelStatus() {
        getRightPanelPresenter().ifPresent(RightPanelView.Presenter::onClearStatus);
    }

    protected String getJsonModel(ScenarioSimulationModel model) {
        return MarshallingWrapper.toJSON(model);
    }

    protected boolean isDirty() {
        try {
            view.getScenarioGridPanel().getScenarioGrid().getModel().resetErrors();
            int currentHashcode = MarshallingWrapper.toJSON(model).hashCode();
            return originalHash != currentHashcode;
        } catch (Exception ignored) {
            return false;
        }
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

    protected void getModelSuccessCallbackMethod(ScenarioSimulationModelContent content) {
        //Path is set to null when the Editor is closed (which can happen before async calls complete).
        if (versionRecordManager.getCurrentPath() == null) {
            return;
        }
        packageName = content.getDataModel().getPackageName();
        resetEditorPages(content.getOverview());
        if (ScenarioSimulationModel.Type.RULE.equals(content.getModel().getSimulation().getSimulationDescriptor().getType())) {
            dataManagementStrategy = new DMODataManagementStrategy(oracleFactory, context);
        } else {
            dataManagementStrategy = new DMNDataManagementStrategy(dmnTypeService, context, eventBus);
        }
        dataManagementStrategy.manageScenarioSimulationModelContent(versionRecordManager.getCurrentPath(), content);
        populateRightPanel();
        model = content.getModel();
        if (dataManagementStrategy instanceof DMODataManagementStrategy) {
            importsWidget.setContent(((DMODataManagementStrategy) dataManagementStrategy).getOracle(),
                                     model.getImports(),
                                     isReadOnly);
            addImportsTab(importsWidget);
        }
        baseView.hideBusyIndicator();
        view.setContent(model.getSimulation());
        context.getStatus().setSimulation(model.getSimulation());
        setOriginalHash(getJsonModel(model).hashCode());
        CustomBusyPopup.close();
    }

    private String getFileDownloadURL(final Supplier<Path> pathSupplier) {
        return GWT.getModuleBaseURL() + "defaulteditor/download?path=" + pathSupplier.get().toURI();
    }

    private RemoteCallback<ScenarioSimulationModelContent> getModelSuccessCallback() {
        return this::getModelSuccessCallbackMethod;
    }

    private Optional<RightPanelView> getRightPanelView() {
        final DefaultPlaceRequest placeRequest = new DefaultPlaceRequest(RightPanelPresenter.IDENTIFIER);
        if (PlaceStatus.OPEN.equals(placeManager.getStatus(placeRequest))) {
            final AbstractWorkbenchActivity rightPanelActivity = (AbstractWorkbenchActivity) placeManager.getActivity(placeRequest);
            return Optional.of((RightPanelView) rightPanelActivity.getWidget());
        } else {
            return Optional.empty();
        }
    }

    private Optional<RightPanelView.Presenter> getRightPanelPresenter() {
        return getRightPanelView().isPresent() ? Optional.of(getRightPanelView().get().getPresenter()) : Optional.empty();
    }

    private Command getPopulateRightPanelCommand() {
        return this::populateRightPanel;
    }

    @Override
    protected String getEditorIdentifier() {
        return IDENTIFIER;
    }
}
