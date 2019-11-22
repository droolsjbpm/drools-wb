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

package org.drools.workbench.screens.scenariosimulation.kogito.client.editor;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.IsWidget;
import elemental2.promise.Promise;
import org.drools.scenariosimulation.api.model.AbstractScesimData;
import org.drools.scenariosimulation.api.model.AbstractScesimModel;
import org.drools.scenariosimulation.api.model.AuditLog;
import org.drools.scenariosimulation.api.model.Background;
import org.drools.scenariosimulation.api.model.ScenarioSimulationModel;
import org.drools.scenariosimulation.api.model.ScenarioWithIndex;
import org.drools.scenariosimulation.api.model.ScesimModelDescriptor;
import org.drools.scenariosimulation.api.model.Settings;
import org.drools.scenariosimulation.api.model.Simulation;
import org.drools.workbench.scenariosimulation.kogito.marshaller.js.MainJs;
import org.drools.workbench.scenariosimulation.kogito.marshaller.js.callbacks.SCESIMMarshallCallback;
import org.drools.workbench.scenariosimulation.kogito.marshaller.js.callbacks.SCESIMUnmarshallCallback;
import org.drools.workbench.scenariosimulation.kogito.marshaller.js.model.JSIScenarioSimulationModelType;
import org.drools.workbench.scenariosimulation.kogito.marshaller.js.model.SCESIM;
import org.drools.workbench.screens.scenariosimulation.client.editor.ScenarioSimulationEditorPresenter;
import org.drools.workbench.screens.scenariosimulation.client.editor.ScenarioSimulationEditorWrapper;
import org.drools.workbench.screens.scenariosimulation.client.editor.strategies.DataManagementStrategy;
import org.drools.workbench.screens.scenariosimulation.client.handlers.ScenarioSimulationHasBusyIndicatorDefaultErrorCallback;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridWidget;
import org.drools.workbench.screens.scenariosimulation.kogito.client.editor.strategies.KogitoDMNDataManagementStrategy;
import org.drools.workbench.screens.scenariosimulation.kogito.client.editor.strategies.KogitoDMODataManagementStrategy;
import org.drools.workbench.screens.scenariosimulation.model.SimulationRunResult;
import org.gwtbootstrap3.client.ui.TabListItem;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.kogito.client.editor.MultiPageEditorContainerPresenter;
import org.kie.workbench.common.kogito.client.editor.MultiPageEditorContainerView;
import org.kie.workbench.common.widgets.client.docks.AuthoringEditorDock;
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilder;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.backend.vfs.impl.ObservablePathImpl;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.PlaceStatus;
import org.uberfire.client.views.pfly.multipage.MultiPageEditorViewImpl;
import org.uberfire.client.views.pfly.multipage.PageImpl;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.menu.Menus;

import static org.drools.workbench.screens.scenariosimulation.kogito.client.converters.ApiJSInteropConverter.getJSIScenarioSimulationModelType;
import static org.drools.workbench.screens.scenariosimulation.kogito.client.converters.JSInteropApiConverter.getScenarioSimulationModel;

@Dependent
/**
 * Wrapper to be used inside Kogito
 */
public class ScenarioSimulationEditorKogitoWrapper extends MultiPageEditorContainerPresenter<ScenarioSimulationModel> implements ScenarioSimulationEditorWrapper {

    protected ScenarioSimulationEditorPresenter scenarioSimulationEditorPresenter;
    private FileMenuBuilder fileMenuBuilder;
    private AuthoringEditorDock authoringWorkbenchDocks;
    private SCESIM scesimContainer;

    public ScenarioSimulationEditorKogitoWrapper() {
        //Zero-parameter constructor for CDI proxies
    }

    @Inject
    public ScenarioSimulationEditorKogitoWrapper(
            final ScenarioSimulationEditorPresenter scenarioSimulationEditorPresenter,
            final FileMenuBuilder fileMenuBuilder,
            final PlaceManager placeManager,
            final MultiPageEditorContainerView multiPageEditorContainerView,
            final AuthoringEditorDock authoringWorkbenchDocks) {
        super(scenarioSimulationEditorPresenter.getView(), /*fileMenuBuilder, */placeManager, multiPageEditorContainerView);
        this.scenarioSimulationEditorPresenter = scenarioSimulationEditorPresenter;
        this.fileMenuBuilder = fileMenuBuilder;
        this.authoringWorkbenchDocks = authoringWorkbenchDocks;
    }

    @Override
    protected void buildMenuBar() {
        setMenus(fileMenuBuilder.build());
        getMenus().getItemsMap().values().forEach(menuItem -> menuItem.setEnabled(true));
    }

    @Override
    public Promise getContent() {
        marshallContent(scenarioSimulationEditorPresenter.getContentSupplier().get());
        return null;
    }

    @Override
    public void setContent(String value) {
        getWidget().init(this);
        Path path = new PathFactory.PathImpl("new.scesimr", "file:///new.scesimr");
        scenarioSimulationEditorPresenter.init(this, new ObservablePathImpl().wrap(path));
        scenarioSimulationEditorPresenter.showDocks(PlaceStatus.CLOSE);
        unmarshallContent(value);
    }

    @Override
    public void wrappedRegisterDock(String id, IsWidget widget) {
        //
    }

    @Override
    public void onImport(String fileContents, RemoteCallback<AbstractScesimModel> importCallBack, ErrorCallback<Object> importErrorCallback, AbstractScesimModel<? extends AbstractScesimData> scesimModel) {
        //
    }

    @Override
    public void onExportToCsv(RemoteCallback<Object> exportCallBack, ScenarioSimulationHasBusyIndicatorDefaultErrorCallback scenarioSimulationHasBusyIndicatorDefaultErrorCallback, AbstractScesimModel<? extends AbstractScesimData> scesimModel) {
        //
    }

    @Override
    public void onDownloadReportToCsv(RemoteCallback<Object> exportCallBack, ScenarioSimulationHasBusyIndicatorDefaultErrorCallback scenarioSimulationHasBusyIndicatorDefaultErrorCallback, AuditLog auditLog) {
        //
    }

    @Override
    public void validate(Simulation simulation, Settings settings, RemoteCallback<?> callback) {
        //
    }

    /**
     * This method adds specifically the Background grid and its related onFocus behavior
     * @param scenarioGridWidget
     */
    @Override
    public void addBackgroundPage(final ScenarioGridWidget scenarioGridWidget) {
        getWidget().getMultiPage().addPage(BACKGROUND_TAB_INDEX, new PageImpl(scenarioGridWidget, ScenarioSimulationEditorConstants.INSTANCE.backgroundTabTitle()) {
            @Override
            public void onFocus() {
                super.onFocus();
                onBackgroundTabSelected();
            }
        });
    }

    protected void addImportsTab(IsWidget importsWidget) {
        addPage(new PageImpl(importsWidget, CommonConstants.INSTANCE.DataObjectsTabTitle()) {
            @Override
            public void onFocus() {
                super.onFocus();
                onImportsTabSelected();
            }
        });
    }

    @Override
    public void selectSimulationTab() {
        final TabListItem item = (TabListItem) ((MultiPageEditorViewImpl) getWidget().getMultiPage().getView()).getTabBar().getWidget(SIMULATION_TAB_INDEX);
        if (item != null) {
            item.showTab(false);
        }
    }

    @Override
    public void selectBackgroundTab() {
        final TabListItem item = (TabListItem) ((MultiPageEditorViewImpl) getWidget().getMultiPage().getView()).getTabBar().getWidget(BACKGROUND_TAB_INDEX);
        if (item != null) {
            item.showTab(false);
        }
    }

    @Override
    public void resetContentHash() {
        //
    }

    public void onStartup(final PlaceRequest place) {
        super.init(place);
        authoringWorkbenchDocks.setup("AuthoringPerspective", place);
    }

    public boolean mayClose() {
        return !scenarioSimulationEditorPresenter.isDirty();
    }

    public IsWidget getTitle() {
        return super.getTitle();
    }

    public MultiPageEditorContainerView getWidget() {
        return super.getWidget();
    }

    public FileMenuBuilder getFileMenuBuilder() {
        return fileMenuBuilder;
    }

    public void setMenus(final Consumer<Menus> menusConsumer) {
        menusConsumer.accept(getMenus());
    }

    @Override
    public void onRunScenario(RemoteCallback<SimulationRunResult> refreshModelCallback, ScenarioSimulationHasBusyIndicatorDefaultErrorCallback scenarioSimulationHasBusyIndicatorDefaultErrorCallback, ScesimModelDescriptor simulationDescriptor, Settings settings, List<ScenarioWithIndex> toRun, Background background) {
        throw new UnsupportedOperationException("Not available in Submarine");
    }

    @Override
    public void wrappedSave(String commitMessage) {
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
        // TODO {gcardosi* verify/align with BusinessCentralWrapper for new "background" tab
        scenarioSimulationEditorPresenter.makeMenuBar(fileMenuBuilder);
    }

    @Override
    protected Supplier<ScenarioSimulationModel> getContentSupplier() {
        return scenarioSimulationEditorPresenter.getContentSupplier();
    }

    protected void marshallContent(ScenarioSimulationModel scenarioSimulationModel) {
        final JSIScenarioSimulationModelType jsiScenarioSimulationModelType = getJSIScenarioSimulationModelType(scenarioSimulationModel);
        scesimContainer.setScenarioSimulationModel(jsiScenarioSimulationModelType);
        MainJs.marshall(scesimContainer, "scesim", getJSInteropMarshallConvert());
    }

    protected void unmarshallContent(String toUnmarshal) {
        MainJs.unmarshall(toUnmarshal, "scesim", getJSInteropUnmarshallConvert());
    }

    protected SCESIMMarshallCallback getJSInteropMarshallConvert() {
        return xml -> {
            GWT.log("xml " + xml);
        };
    }

    protected SCESIMUnmarshallCallback getJSInteropUnmarshallConvert() {
        return scesim -> {
            this.scesimContainer = scesim;
            final JSIScenarioSimulationModelType scenarioSimulationModelType = scesim.getScenarioSimulationModel();
            final ScenarioSimulationModel scenarioSimulationModel = getScenarioSimulationModel(scenarioSimulationModelType);
            getModelSuccessCallbackMethod(scenarioSimulationModel);
        };
    }

    protected void getModelSuccessCallbackMethod(ScenarioSimulationModel model) {
        scenarioSimulationEditorPresenter.setPackageName("com");
        resetEditorPages();
        DataManagementStrategy dataManagementStrategy;
        if (ScenarioSimulationModel.Type.RULE.equals(model.getSettings().getType())) {
            dataManagementStrategy = new KogitoDMODataManagementStrategy();
        } else {
            dataManagementStrategy = new KogitoDMNDataManagementStrategy(scenarioSimulationEditorPresenter.getEventBus());
        }
        dataManagementStrategy.setModel(model);
        scenarioSimulationEditorPresenter.getModelSuccessCallbackMethod(dataManagementStrategy, model);
    }

    protected void onBackgroundTabSelected() {
        scenarioSimulationEditorPresenter.onBackgroundTabSelected();
    }

    protected void onImportsTabSelected() {
        scenarioSimulationEditorPresenter.onImportsTabSelected();
    }

}
