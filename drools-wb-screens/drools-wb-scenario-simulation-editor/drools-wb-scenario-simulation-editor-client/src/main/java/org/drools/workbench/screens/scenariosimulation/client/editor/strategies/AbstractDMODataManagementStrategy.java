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
package org.drools.workbench.screens.scenariosimulation.client.editor.strategies;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.drools.scenariosimulation.api.utils.ScenarioSimulationSharedUtils;
import org.drools.workbench.screens.scenariosimulation.client.commands.ScenarioSimulationContext;
import org.drools.workbench.screens.scenariosimulation.client.enums.GridWidget;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.TestToolsView;
import org.drools.workbench.screens.scenariosimulation.model.typedescriptor.FactModelTree;
import org.drools.workbench.screens.scenariosimulation.model.typedescriptor.FactModelTuple;
import org.kie.soup.project.datamodel.oracle.ModelField;
import org.uberfire.client.callbacks.Callback;

public abstract class AbstractDMODataManagementStrategy extends AbstractDataManagementStrategy {

    //Package for which this Scenario Simulation relates
    protected String packageName = "";

    protected abstract String getFQCNByFactName(String factName);

    protected abstract String getParametricFieldType(String factName, String propertyName);

    protected abstract List<String> getFactTypes();

    protected abstract boolean hasEnumValues(String factType, String factField);

    protected abstract String[] getEnumValues(String factType, String factField);

    protected abstract void getSuperType(String factType, Callback<String> callback);

    protected abstract boolean skipPopulateTestTools();

    protected abstract void manageDataObjects(final List<String> dataObjectsTypes,
                                              final Map<String, String> superTypeMap,
                                              final TestToolsView.Presenter testToolsPresenter,
                                              final int expectedElements,
                                              final SortedMap<String, FactModelTree> dataObjectsFieldsMap,
                                              final ScenarioSimulationContext context,
                                              final List<String> simpleJavaTypes,
                                              final GridWidget gridWidget);

    @Override
    public void populateTestTools(final TestToolsView.Presenter testToolsPresenter,
                                  final ScenarioSimulationContext context,
                                  final GridWidget gridWidget) {
        if (factModelTreeHolder.getFactModelTuple() != null) {
            storeData(factModelTreeHolder.getFactModelTuple(), testToolsPresenter, context, gridWidget);
        } else {
            if (skipPopulateTestTools()) {
                testToolsPresenter.hideInstances();
                return;
            }
            // Retrieve the relevant facttypes
            List<String> factTypes = getFactTypes();

            // Split the DMO from the Simple Java types
            final Map<Boolean, List<String>> partitionedFactTypes = factTypes.stream()
                    .collect(Collectors.partitioningBy(factType -> SIMPLE_CLASSES_MAP.keySet().contains(factType)));

            final List<String> dataObjectsTypes = partitionedFactTypes.get(false);
            final List<String> simpleJavaTypes = partitionedFactTypes.get(true);
            int expectedElements = dataObjectsTypes.size();
            // Instantiate a dataObjects container map
            final SortedMap<String, FactModelTree> dataObjectsFieldsMap = new TreeMap<>();
            final Map<String, String> superTypesMap = new HashMap<>();
            if (dataObjectsTypes.isEmpty()) { // Add to manage the situation when no complex objects are present
                aggregatorCallbackMethod(testToolsPresenter, expectedElements, dataObjectsFieldsMap, context, null, simpleJavaTypes, gridWidget);
            } else {
                loadSuperTypes(dataObjectsTypes, testToolsPresenter, expectedElements, dataObjectsFieldsMap, superTypesMap, context, simpleJavaTypes, gridWidget);
            }
        }
    }

    protected void loadSuperTypes(final List<String> dataObjectsTypes,
                             final TestToolsView.Presenter testToolsPresenter,
                             final int expectedElements,
                             final SortedMap<String, FactModelTree> dataObjectsFieldsMap,
                             final Map<String, String> superTypesMap,
                             final ScenarioSimulationContext context,
                             final List<String> simpleJavaTypes,
                             final GridWidget gridWidget) {
        dataObjectsTypes.forEach(factType -> getSuperType(factType, result -> superTypeAggregatorCallBack(dataObjectsTypes,
                                                                                                      superTypesMap,
                                                                                                      testToolsPresenter,
                                                                                                      expectedElements,
                                                                                                      dataObjectsFieldsMap,
                                                                                                      context,
                                                                                                      simpleJavaTypes,
                                                                                                      gridWidget,
                                                                                                      new AbstractMap.SimpleEntry<>(factType, result))));
    }

    private void superTypeAggregatorCallBack(List<String> dataObjectsTypes, Map<String, String> superTypeMap, TestToolsView.Presenter testToolsPresenter, int expectedElements, SortedMap<String, FactModelTree> dataObjectsFieldsMap, ScenarioSimulationContext context, List<String> simpleJavaTypes, GridWidget gridWidget, AbstractMap.SimpleEntry<String, String> result) {
        if (result != null) {
            superTypeMap.put(result.getKey(), result.getValue());
        } 
        if (superTypeMap.size() == expectedElements) {
            
            
            manageDataObjects(dataObjectsTypes, superTypeMap, testToolsPresenter, expectedElements, dataObjectsFieldsMap, context, simpleJavaTypes, gridWidget);
        }
    }

    /**
     * Create a <code>FactModelTree</code> for a given <b>factName</b> populating it with the given
     * <code>ModelField[]</code>
     * @param factName
     * @param superTypeMap
     * @param modelFields
     * @return
     * @implNote For the moment being, due to current implementation of <b>DMO</b>, it it not possible to retrieve <b>all</b>
     * the generic types of a class with more then one, but only the last one. So, for <code>Map</code>, the <b>key</b>
     * will always be a <code>java.lang.String</code>
     */
    public FactModelTree getFactModelTree(String factName, Map<String, String> superTypeMap, ModelField[] modelFields) {
        Map<String, String> simpleProperties = new HashMap<>();
        Map<String, List<String>> genericTypesMap = new HashMap<>();
        String factPackageName = packageName;
        String fullFactClassName = getFQCNByFactName(factName);
        if (fullFactClassName != null && fullFactClassName.contains(".")) {
            factPackageName = fullFactClassName.substring(0, fullFactClassName.lastIndexOf('.'));
        }
        if (Enum.class.getCanonicalName().equals(superTypeMap.get(factName))) {
            simpleProperties.put("value", Enum.class.getCanonicalName());
            return new FactModelTree(factName,factPackageName, simpleProperties, genericTypesMap);
        }

        for (ModelField modelField : modelFields) {
            if (!modelField.getName().equals("this")) {
                boolean isEnum = hasEnumValues(factName, modelField.getName());
                String fieldClassName = isEnum ? Enum.class.getCanonicalName() : modelField.getClassName();
                String className = SIMPLE_CLASSES_MAP.containsKey(fieldClassName) ?
                        SIMPLE_CLASSES_MAP.get(fieldClassName).getCanonicalName() :
                        modelField.getClassName();
                simpleProperties.put(modelField.getName(), className);
                if (ScenarioSimulationSharedUtils.isCollection(className) ) {
                    populateGenericTypeMap(genericTypesMap, factName, modelField.getName(), ScenarioSimulationSharedUtils.isList(className));
                }
            }
        }
        return new FactModelTree(factName, factPackageName, simpleProperties, genericTypesMap);
    }

    /**
     * Populate the given <code>Map</code> with the generic type(s) of given property.
     * If <code>isList</code> is false, the first generic will be <b>java.lang.String</b>
     * @param toPopulate
     * @param factName
     * @param propertyName
     * @param isList
     * @implNote due to current DMO implementation, it is not possible to retrieve <b>all</b> generic types of a given class, but only the last one; for the moment being, the generic type
     * for <code>Map</code> will be <b>java.lang.String</b>
     */
    public void populateGenericTypeMap(Map<String, List<String>> toPopulate, String factName, String propertyName, boolean isList) {
        List<String> genericTypes = new ArrayList<>();
        if (!isList) {
            genericTypes.add(String.class.getName());
        }
        String genericInfo = getParametricFieldType(factName, propertyName);
        String fullGenericInfoClassName = getFQCNByFactName(genericInfo);
        genericTypes.add(fullGenericInfoClassName);
        toPopulate.put(propertyName, genericTypes);
    }

    /**
     * Actual code of the <b>aggregatorCallback</b>; isolated for testing
     * @param testToolsPresenter
     * @param expectedElements
     * @param factTypeFieldsMap
     * @param context
     * @param result pass <code>null</code> if there is not any <i>complex</i> data object but only simple ones
     * @param simpleJavaTypes
     */
    public void aggregatorCallbackMethod(final TestToolsView.Presenter testToolsPresenter,
                                         final int expectedElements,
                                         final SortedMap<String, FactModelTree> factTypeFieldsMap,
                                         final ScenarioSimulationContext context,
                                         final FactModelTree result,
                                         final List<String> simpleJavaTypes,
                                         final GridWidget gridWidget) {
        if (result != null) {
            factTypeFieldsMap.put(result.getFactName(), result);
        }
        if (factTypeFieldsMap.size() == expectedElements) { // This is used to invoke this callback only once, when all the expected "complex" objects has been managed
            factTypeFieldsMap.values().forEach(factModelTree -> populateFactModelTree(factModelTree, factTypeFieldsMap));
            SortedMap<String, FactModelTree> simpleJavaTypeFieldsMap =
                    new TreeMap<>(simpleJavaTypes.stream()
                                          .collect(Collectors.toMap(
                                                  factType -> factType,
                                                  factType -> {
                                                      SimpleClassEntry classEntry = SIMPLE_CLASSES_MAP.get(factType);
                                                      return getSimpleClassFactModelTree(classEntry.getSimpleName(), classEntry.getCanonicalName());
                                                  })));

            SortedMap<String, FactModelTree> visibleFacts = new TreeMap<>(factTypeFieldsMap);
            visibleFacts.putAll(simpleJavaTypeFieldsMap);
            FactModelTuple factModelTuple = new FactModelTuple(visibleFacts, new TreeMap<>());
            factModelTreeHolder.setFactModelTuple(factModelTuple);
            storeData(factModelTuple, testToolsPresenter, context, gridWidget);
        }
    }

    /**
     * This method replace a <b>simple property</b> from the given <code>FactModelTree</code> and replace it with
     * an <b>expandable property</b> <code>FactModelTree</code> from the given  <b>factTypeFieldsMap</b>, if a matching
     * element exists.
     * @param toPopulate
     * @param factTypeFieldsMap
     */
    public void populateFactModelTree(FactModelTree toPopulate, final SortedMap<String, FactModelTree> factTypeFieldsMap) {
        List<String> toRemove = new ArrayList<>();
        toPopulate.getSimpleProperties().forEach((key, value) -> {
            if (factTypeFieldsMap.containsKey(value)) {
                toRemove.add(key);
                toPopulate.addExpandableProperty(key, factTypeFieldsMap.get(value).getFactName());
            }
        });
        toRemove.forEach(toPopulate::removeSimpleProperty);
    }
}
