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
package org.drools.workbench.screens.scenariosimulation.businesscentral.client.editor.strategies;

import java.util.Arrays;
import java.util.List;
import java.util.SortedMap;

import org.drools.workbench.screens.scenariosimulation.client.commands.ScenarioSimulationContext;
import org.drools.workbench.screens.scenariosimulation.client.editor.strategies.AbstractDMODataManagementStrategy;
import org.drools.workbench.screens.scenariosimulation.client.models.ScenarioGridModel;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.TestToolsView;
import org.drools.workbench.screens.scenariosimulation.model.ScenarioSimulationModelContent;
import org.drools.workbench.screens.scenariosimulation.model.typedescriptor.FactModelTree;
import org.kie.soup.project.datamodel.oracle.ModelField;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracleFactory;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.client.callbacks.Callback;

public class BusinessCentralDMODataManagementStrategy extends AbstractDMODataManagementStrategy {

    private AsyncPackageDataModelOracleFactory oracleFactory;
    protected AsyncPackageDataModelOracle oracle;

    //Package for which this Scenario Simulation relates
    protected String packageName = "";

    public BusinessCentralDMODataManagementStrategy(final AsyncPackageDataModelOracleFactory oracleFactory, final ScenarioSimulationContext scenarioSimulationContext) {
        this.oracleFactory = oracleFactory;
        this.scenarioSimulationContext = scenarioSimulationContext;
    }

    @Override
    public void manageScenarioSimulationModelContent(ObservablePath currentPath, ScenarioSimulationModelContent toManage) {
        model = toManage.getModel();
        oracle = oracleFactory.makeAsyncPackageDataModelOracle(currentPath,
                                                               model,
                                                               toManage.getDataModel());
    }

    @Override
    public boolean isADataType(String value) {
        return oracle != null && Arrays.asList(oracle.getFactTypes()).contains(value);
    }

    public AsyncPackageDataModelOracle getOracle() {
        return oracle;
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
        return result -> fieldCompletionsCallbackMethod(factName, result, aggregatorCallback);
    }

    /**
     * Actual code of the <b>fieldCompletionsCallback</b>; isolated for testing
     * @param factName
     * @param result
     * @param aggregatorCallback
     */
    protected void fieldCompletionsCallbackMethod(String factName, ModelField[] result, Callback<FactModelTree> aggregatorCallback) {
        FactModelTree toSend = getFactModelTree(factName, result);
        aggregatorCallback.callback(toSend);
    }


    /**
     * This <code>Callback</code> will receive data from other callbacks and when the retrieved results get to the
     * expected ones it will recursively elaborate the map
     * @param testToolsPresenter
     * @param expectedElements
     * @param factTypeFieldsMap
     * @param scenarioGridModel
     * @return
     */
    protected Callback<FactModelTree> aggregatorCallback(final TestToolsView.Presenter testToolsPresenter, final int expectedElements, final SortedMap<String, FactModelTree> factTypeFieldsMap, final ScenarioGridModel scenarioGridModel, final List<String> simpleJavaTypes) {
        return result -> aggregatorCallbackMethod(testToolsPresenter, expectedElements, factTypeFieldsMap, scenarioGridModel, result, simpleJavaTypes);
    }

    @Override
    protected void manageDataObjects(List<String> dataObjectsTypes, final TestToolsView.Presenter testToolsPresenter,  int expectedElements,
                                     final SortedMap<String, FactModelTree> dataObjectsFieldsMap,
                                     final ScenarioGridModel scenarioGridModel,
                                     final List<String> simpleJavaTypes) {
        // Instantiate the aggregator callback
        Callback<FactModelTree> aggregatorCallback = aggregatorCallback(testToolsPresenter, expectedElements, dataObjectsFieldsMap, scenarioGridModel, simpleJavaTypes);
        // Iterate over all dataObjects to retrieve their modelfields
        dataObjectsTypes.forEach(factType ->
                                         oracle.getFieldCompletions(factType, fieldCompletionsCallback(factType, aggregatorCallback)));

    }

    @Override
    protected List<String> getFactTypes() {
        return Arrays.asList(oracle.getFactTypes());
    }

    @Override
    protected boolean skipPopulateTestTools() {
        return oracle == null || oracle.getFactTypes().length == 0;
    }

    @Override
    protected String getFQCNByFactName(String factName) {
        return oracle.getFQCNByFactName(factName);
    }

    @Override
    protected String getParametricFieldType(String factName, String propertyName) {
        return oracle.getParametricFieldType(factName, propertyName);
    }
}
