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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Supplier;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.IsWidget;
import org.drools.workbench.screens.scenariosimulation.client.commands.ScenarioSimulationContext;
import org.drools.workbench.screens.scenariosimulation.client.events.RedoEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.UndoEvent;
import org.drools.workbench.screens.scenariosimulation.client.handlers.ScenarioSimulationDocksHandler;
import org.drools.workbench.screens.scenariosimulation.client.models.FactModelTree;
import org.drools.workbench.screens.scenariosimulation.client.producers.ScenarioSimulationProducer;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.RightPanelPresenter;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.RightPanelView;
import org.drools.workbench.screens.scenariosimulation.client.type.ScenarioSimulationResourceType;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridPanel;
import org.drools.workbench.screens.scenariosimulation.model.ScenarioSimulationModel;
import org.drools.workbench.screens.scenariosimulation.model.ScenarioSimulationModelContent;
import org.drools.workbench.screens.scenariosimulation.model.SimulationDescriptor;
import org.drools.workbench.screens.scenariosimulation.service.ScenarioSimulationService;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.enterprise.client.jaxrs.MarshallingWrapper;
import org.kie.soup.project.datamodel.oracle.ModelField;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracleFactory;
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilder;
import org.kie.workbench.common.widgets.configresource.client.widget.bound.ImportsWidgetPresenter;
import org.kie.workbench.common.widgets.metadata.client.KieEditor;
import org.kie.workbench.common.workbench.client.test.TestRunnerReportingScreen;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartTitleDecoration;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.client.mvp.AbstractWorkbenchActivity;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.PlaceStatus;
import org.uberfire.client.workbench.events.PlaceGainFocusEvent;
import org.uberfire.client.workbench.events.PlaceHiddenEvent;
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
import org.uberfire.mvp.impl.PathPlaceRequest;
import org.uberfire.workbench.model.menu.Menus;

import static org.drools.workbench.screens.scenariosimulation.client.editor.ScenarioSimulationEditorPresenter.IDENTIFIER;

@Dependent
@WorkbenchEditor(identifier = IDENTIFIER, supportedTypes = {ScenarioSimulationResourceType.class})
public class ScenarioSimulationEditorPresenter
        extends KieEditor<ScenarioSimulationModel> {

    public static final String IDENTIFIER = "ScenarioSimulationEditor";

    protected AsyncPackageDataModelOracle oracle;

    //Package for which this Scenario Simulation relates
    protected String packageName = "";

    protected PlaceRequest rightPanelRequest;

    protected ObservablePath path;

    protected EventBus eventBus;

    protected ScenarioGridPanel scenarioGridPanel;

    private ImportsWidgetPresenter importsWidget;

    private AsyncPackageDataModelOracleFactory oracleFactory;

    private ScenarioSimulationModel model;

    private Caller<ScenarioSimulationService> service;

    private ScenarioSimulationResourceType type;

    private ScenarioSimulationView view;

    private ScenarioSimulationContext context;

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
                                             final ScenarioSimulationDocksHandler scenarioSimulationDocksHandler) {
        super(scenarioSimulationProducer.getScenarioSimulationView());
        this.testRunnerReportingScreen = testRunnerReportingScreen;
        this.scenarioSimulationDocksHandler = scenarioSimulationDocksHandler;
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
    public void onClose() {
        this.versionRecordManager.clear();
        scenarioGridPanel.unregister();
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

    // Observing to show RightPanel when ScenarioSimulationScreen is put in foreground
    public void onPlaceGainFocusEvent(@Observes PlaceGainFocusEvent placeGainFocusEvent) {
        if (!(placeGainFocusEvent.getPlace() instanceof PathPlaceRequest)) {  // Ignoring other requests
            return;
        }
        PathPlaceRequest placeRequest = (PathPlaceRequest) placeGainFocusEvent.getPlace();
        if (placeRequest.getIdentifier().equals(ScenarioSimulationEditorPresenter.IDENTIFIER)
                && placeRequest.getPath().equals(this.path)) {
            scenarioSimulationDocksHandler.addDocks();
            expandToolsDock();
            registerRightPanelCallback();
            populateRightPanel();
        }
    }

    // Observing to hide RightPanel when ScenarioSimulationScreen is put in background
    public void onPlaceHiddenEvent(@Observes PlaceHiddenEvent placeHiddenEvent) {
        if (!(placeHiddenEvent.getPlace() instanceof PathPlaceRequest)) {  // Ignoring other requests
            return;
        }
        PathPlaceRequest placeRequest = (PathPlaceRequest) placeHiddenEvent.getPlace();
        if (placeRequest.getIdentifier().equals(ScenarioSimulationEditorPresenter.IDENTIFIER)
                && placeRequest.getPath().equals(this.path)) {
            scenarioSimulationDocksHandler.removeDocks();
            view.getScenarioGridLayer().getScenarioGrid().clearSelections();
            unRegisterRightPanelCallback();
            clearRightPanelStatus();
            testRunnerReportingScreen.reset();
        }
    }

    public void expandToolsDock() {
        scenarioSimulationDocksHandler.expandToolsDock();
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
        view.getScenarioGridPanel().getScenarioGrid().getModel().resetErrors();
        service.call(refreshModel()).runScenario(versionRecordManager.getCurrentPath(), model);
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

    protected RemoteCallback<ScenarioSimulationModel> refreshModel() {
        return newModel -> {
            this.model = newModel;
            view.refreshContent(newModel.getSimulation());
            scenarioSimulationDocksHandler.expandTestResultsDock();
        };
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

    protected void loadContent() {
        service.call(getModelSuccessCallback(),
                     getNoSuchFileExceptionErrorCallback()).loadContent(versionRecordManager.getCurrentPath());
    }

    void populateRightPanel() {
        // Execute only when RightPanelPresenter is actually available
        getRightPanelPresenter().ifPresent(presenter -> {
            context.setRightPanelPresenter(presenter);
            presenter.setEventBus(eventBus);
            populateRightPanel(presenter);
        });
    }

    void populateRightPanel(RightPanelView.Presenter rightPanelPresenter) {
        // Instantiate a container map
        SortedMap<String, FactModelTree> factTypeFieldsMap = new TreeMap<>();
        // Execute only when oracle has been set
        if (oracle == null) {
            if (rightPanelPresenter != null) {
                rightPanelPresenter.setDataObjectFieldsMap(factTypeFieldsMap);
            }
            return;
        }
        // Retrieve the relevant facttypes
        String[] factTypes = oracle.getFactTypes();
        if (factTypes.length == 0) {  // We do not have to set nothing
            if (rightPanelPresenter != null) {
                rightPanelPresenter.setDataObjectFieldsMap(factTypeFieldsMap);
            }
            return;
        }
        // Instantiate the aggregator callback
        Callback<FactModelTree> aggregatorCallback = aggregatorCallback(rightPanelPresenter, factTypes.length, factTypeFieldsMap);
        // Iterate over all facttypes to retrieve their modelfields
        for (String factType : factTypes) {
            oracle.getFieldCompletions(factType, fieldCompletionsCallback(factType, aggregatorCallback));
        }
    }

    protected void clearRightPanelStatus() {
        getRightPanelPresenter().ifPresent(RightPanelView.Presenter::onClearStatus);
    }

    protected String getJsonModel(ScenarioSimulationModel model) {
        return MarshallingWrapper.toJSON(model);
    }

    /**
     * This <code>Callback</code> will receive <code>ModelField[]</code> from <code>AsyncPackageDataModelOracleFactory.getFieldCompletions(final String,
     * final Callback&lt;ModelField[]&gt;)</code>; build a <code>FactModelTree</code> from them, and send it to the
     * given <code>Callback&lt;FactModelTree&gt;</code> aggregatorCallback
     * @param factName
     * @param aggregatorCallback
     * @return
     */
    protected Callback<ModelField[]> fieldCompletionsCallback(String factName, Callback<FactModelTree> aggregatorCallback) {
        return result -> {
            FactModelTree toSend = getFactModelTree(factName, result);
            aggregatorCallback.callback(toSend);
        };
    }

    /**
     * Create a <code>FactModelTree</code> for a given <b>factName</b> populating it with the given
     * <code>ModelField[]</code>
     * @param factName
     * @param modelFields
     * @return
     */
    protected FactModelTree getFactModelTree(String factName, ModelField[] modelFields) {
        Map<String, String> simpleProperties = new HashMap<>();
        for (ModelField modelField : modelFields) {
            if (!modelField.getName().equals("this")) {
                simpleProperties.put(modelField.getName(), modelField.getClassName());
            }
        }
        String factPackageName = packageName;
        String fullFactClassName = oracle.getFQCNByFactName(factName);
        if (fullFactClassName != null && fullFactClassName.contains(".")) {
            factPackageName = fullFactClassName.substring(0, fullFactClassName.lastIndexOf("."));
        }
        return new FactModelTree(factName, factPackageName, simpleProperties);
    }

    private RemoteCallback<ScenarioSimulationModelContent> getModelSuccessCallback() {
        return content -> {
            //Path is set to null when the Editor is closed (which can happen before async calls complete).
            if (versionRecordManager.getCurrentPath() == null) {
                return;
            }
            packageName = content.getDataModel().getPackageName();
            resetEditorPages(content.getOverview());
            model = content.getModel();
            oracle = oracleFactory.makeAsyncPackageDataModelOracle(versionRecordManager.getCurrentPath(),
                                                                   model,
                                                                   content.getDataModel());
            populateRightPanel();
            importsWidget.setContent(oracle,
                                     model.getImports(),
                                     isReadOnly);
            addImportsTab(importsWidget);
            baseView.hideBusyIndicator();
            view.setContent(model.getSimulation());
            setOriginalHash(getJsonModel(model).hashCode());
        };
    }

    /**
     * This <code>Callback</code> will receive data from other callbacks and when the retrieved results get to the
     * expected ones it will recursively elaborate the map
     * @param rightPanelPresenter
     * @param expectedElements
     * @param factTypeFieldsMap
     * @return
     */
    private Callback<FactModelTree> aggregatorCallback(final RightPanelView.Presenter rightPanelPresenter, final int expectedElements, SortedMap<String, FactModelTree> factTypeFieldsMap) {
        return result -> {
            factTypeFieldsMap.put(result.getFactName(), result);
            if (factTypeFieldsMap.size() == expectedElements) {
                factTypeFieldsMap.values().forEach(factModelTree -> populateFactModel(factModelTree, factTypeFieldsMap));
                rightPanelPresenter.setDataObjectFieldsMap(factTypeFieldsMap);
                SortedMap<String, FactModelTree> instanceFieldsMap = new TreeMap<>();
                // map instance name top data model class
                if (model != null) {
                    final SimulationDescriptor simulationDescriptor = model.getSimulation().getSimulationDescriptor();
                    simulationDescriptor.getUnmodifiableFactMappings().forEach(factMapping -> {
                        String dataObjectName = factMapping.getFactIdentifier().getClassName();
                        if (dataObjectName.contains(".")) {
                            dataObjectName = dataObjectName.substring(dataObjectName.lastIndexOf(".") + 1);
                        }
                        final String instanceName = factMapping.getFactAlias();
                        if (!instanceName.equals(dataObjectName)) {
                            final FactModelTree factModelTree = factTypeFieldsMap.get(dataObjectName);
                            if (factModelTree != null) {
                                instanceFieldsMap.put(instanceName, factModelTree);
                            }
                        }
                    });
                }
                rightPanelPresenter.setInstanceFieldsMap(instanceFieldsMap);
                Set<String> dataObjectsInstancesName = new HashSet<>(factTypeFieldsMap.keySet());
                dataObjectsInstancesName.addAll(instanceFieldsMap.keySet());
                scenarioGridPanel.getScenarioGrid().getModel().setDataObjectsInstancesName(dataObjectsInstancesName);
            }
        };
    }

    private void populateFactModel(FactModelTree toPopulate, SortedMap<String, FactModelTree> factTypeFieldsMap) {
        List<String> toRemove = new ArrayList<>();
        toPopulate.getSimpleProperties().forEach((key, value) -> {
            if (factTypeFieldsMap.containsKey(value)) {
                toRemove.add(key);
                toPopulate.addExpandableProperty(key, factTypeFieldsMap.get(value).getFactName());
            }
        });
        toRemove.forEach(toPopulate::removeSimpleProperty);
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

    boolean isDirty() {
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
}
