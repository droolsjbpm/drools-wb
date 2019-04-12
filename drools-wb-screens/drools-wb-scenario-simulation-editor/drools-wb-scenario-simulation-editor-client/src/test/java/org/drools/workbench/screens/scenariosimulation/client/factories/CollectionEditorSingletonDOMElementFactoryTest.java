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

package org.drools.workbench.screens.scenariosimulation.client.factories;

import java.util.HashMap;
import java.util.Map;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.dom.client.DivElement;
import org.drools.workbench.screens.scenariosimulation.client.collectioneditor.CollectionPresenter;
import org.drools.workbench.screens.scenariosimulation.client.collectioneditor.CollectionViewImpl;
import org.drools.workbench.screens.scenariosimulation.model.typedescriptor.FactModelTree;
import org.drools.workbench.screens.scenariosimulation.utils.ScenarioSimulationSharedUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.drools.workbench.screens.scenariosimulation.client.utils.ScenarioSimulationUtils.isSimpleJavaType;
import static org.drools.workbench.screens.scenariosimulation.model.ScenarioSimulationModel.Type.DMN;
import static org.drools.workbench.screens.scenariosimulation.model.ScenarioSimulationModel.Type.RULE;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class CollectionEditorSingletonDOMElementFactoryTest extends AbstractFactoriesTest {

    protected final String STRING_CLASS_NAME = String.class.getCanonicalName();
    protected final String NUMBER_CLASS_NAME = Number.class.getCanonicalName();
    protected final Map<String, String> expectedMapForNotSimpleType = new HashMap<>();
    protected final Map<String, String> expectedMapForNotSimpleType1 = new HashMap<>();
    protected final Map<String, String> expectedMapForNotSimpleType2= new HashMap<>();

    protected CollectionEditorSingletonDOMElementFactory collectionEditorSingletonDOMElementFactoryMock;
    protected CollectionViewImpl collectionEditorViewImpl;

    @Mock
    protected CollectionPresenter collectionPresenterMock;

    @Mock
    protected DivElement collectionEditorModalBodyMock;

    @Mock
    protected FactModelTree factModelTreeMock;

    @Mock
    protected FactModelTree factModelTreeMock1;

    @Mock
    protected FactModelTree factModelTreeMock2;


    @Before
    public void setup() {
        super.setup();
        this.collectionEditorViewImpl = spy(new CollectionViewImpl() {
            {
                this.presenter = collectionPresenterMock;
                this.collectionEditorModalBody = collectionEditorModalBodyMock;
            }
        });

        this.collectionEditorSingletonDOMElementFactoryMock = spy(new CollectionEditorSingletonDOMElementFactory(scenarioGridPanelMock,
                                                                                                                 scenarioGridLayerMock,
                                                                                                                 scenarioGridMock,
                                                                                                                 scenarioSimulationContextLocal,
                                                                                                                 viewsProviderMock));
        factMappingMock.getGenericTypes().add(STRING_CLASS_NAME);
        factMappingMock.getGenericTypes().add(NUMBER_CLASS_NAME);
        when(factMappingMock.getFactAlias()).thenReturn(FULL_CLASS_NAME);
        when(scenarioSimulationContextLocal.getDataObjectFieldsMap().get(any())).thenReturn(factModelTreeMock);
        /* This FactModelTree is used to test setCollectionEditorStructureData() method (return null in any case) */
        when(factModelTreeMock.getSimpleProperties()).thenReturn(new HashMap<>());
        when(factModelTreeMock.getExpandableProperties()).thenReturn(new HashMap<>());
        /* This FactModelTree is used to test manageList and manageMap methods*/
        when(factModelTreeMock1.getSimpleProperties()).thenReturn(expectedMapForNotSimpleType);
        when(factModelTreeMock1.getExpandableProperties()).thenReturn(expectedMapForNotSimpleType1);
        when(scenarioSimulationContextLocal.getDataObjectFieldsMap().get(FULL_CLASS_NAME)).thenReturn(factModelTreeMock1);
        expectedMapForNotSimpleType.put("x", "y");
        expectedMapForNotSimpleType1.put("a", "b");
        when(scenarioSimulationContextLocal.getDataObjectFieldsMap().get("testclass")).thenReturn(factModelTreeMock2);
        when(factModelTreeMock2.getSimpleProperties()).thenReturn(expectedMapForNotSimpleType2);
        when(factModelTreeMock2.getExpandableProperties()).thenReturn(expectedMapForNotSimpleType1);
        expectedMapForNotSimpleType2.put("z", "w");
    }

    @Test
    public void manageList() {
        String key =  FULL_CLASS_NAME + "#" + LIST_CLASS_NAME;
        collectionEditorSingletonDOMElementFactoryMock.manageList(collectionEditorViewImpl, key, STRING_CLASS_NAME);
        verify(collectionEditorViewImpl, times(1)).setListWidget(true);
        Map<String, String> expectedMap1 = new HashMap<>();
        expectedMap1.put("value", STRING_CLASS_NAME);
        verify(collectionEditorViewImpl, times(1)).initListStructure(eq(key), eq(expectedMap1), isA(Map.class));
        verify(collectionEditorSingletonDOMElementFactoryMock, times(1)).getExpandablePropertiesMap(eq(STRING_CLASS_NAME));
    }

    @Test
    public void manageMap_RuleSimpleType() {
        Map<String, String> expectedMap1 = new HashMap<>();
        expectedMap1.put("value", NUMBER_CLASS_NAME);
        manageMap(NUMBER_CLASS_NAME, true, expectedMap1);

    }

    @Test
    public void manageMap_NotRuleSimpleType() {
        Map<String, String> expectedMap1 = new HashMap<>();
        expectedMap1.put("value", NUMBER_CLASS_NAME);
        manageMap(NUMBER_CLASS_NAME, false, expectedMap1);
    }

    @Test
    public void manageMap_NotRuleNotSimpleType() {
        manageMap(FULL_CLASS_NAME, false, expectedMapForNotSimpleType);
    }

    private void manageMap(String genericType1, boolean isRule, Map<String, String> expectedMap1) {
        String key =  FULL_CLASS_NAME + "#" + LIST_CLASS_NAME;
        Map<String, String> expectedMap0 = new HashMap<>();

        doReturn(expectedMap0).when(collectionEditorSingletonDOMElementFactoryMock).getSimplePropertiesMap(eq(STRING_CLASS_NAME));
        doReturn(expectedMap1).when(collectionEditorSingletonDOMElementFactoryMock).getSimplePropertiesMap(eq(genericType1));
        collectionEditorSingletonDOMElementFactoryMock.manageMap(collectionEditorViewImpl, key, STRING_CLASS_NAME, genericType1, isRule);
        verify(collectionEditorSingletonDOMElementFactoryMock, times(1)).getSimplePropertiesMap(STRING_CLASS_NAME);
        verify(collectionEditorSingletonDOMElementFactoryMock, times(1)).getSimplePropertiesMap(genericType1);

        verify(collectionEditorViewImpl, times(1)).setListWidget(false);
        verify(collectionEditorViewImpl, times(1)).initMapStructure(eq(key), eq(expectedMap0), eq(expectedMap1));
    }

    @Test(expected = IllegalStateException.class)
    public void setCollectionEditorStructureData_EmptyPropertyClass() {
        when(factMappingMock.getExpressionAlias()).thenReturn(MAP_CLASS_NAME);

        setCollectionEditorStructureData();
    }

    @Test
    public void setCollectionEditorStructureData_ManageMapRuleSimpleType() {
        when(factMappingMock.getExpressionAlias()).thenReturn(MAP_CLASS_NAME);
        when(factMappingMock.getClassName()).thenReturn(MAP_CLASS_NAME);
        when(simulationDescriptorMock.getType()).thenReturn(RULE);

        setCollectionEditorStructureData();
    }

    @Test
    public void setCollectionEditorStructureData_ManageMapRule_NotSimpleType() {
        when(factMappingMock.getExpressionAlias()).thenReturn(MAP_CLASS_NAME);
        when(factMappingMock.getClassName()).thenReturn(MAP_CLASS_NAME);
        when(simulationDescriptorMock.getType()).thenReturn(RULE);
        factMappingMock.getGenericTypes().clear();
        factMappingMock.getGenericTypes().add(FULL_CLASS_NAME);
        factMappingMock.getGenericTypes().add(FULL_CLASS_NAME + "1");

        setCollectionEditorStructureData();
    }

    @Test
    public void setCollectionEditorStructureData_ManageMapRule() {
        when(factMappingMock.getExpressionAlias()).thenReturn(MAP_CLASS_NAME);
        when(factMappingMock.getClassName()).thenReturn(MAP_CLASS_NAME);
        when(simulationDescriptorMock.getType()).thenReturn(RULE);

        setCollectionEditorStructureData();
    }

    @Test
    public void setCollectionEditorStructureData_ManageListRule() {
        when(factMappingMock.getExpressionAlias()).thenReturn(LIST_CLASS_NAME);
        when(factMappingMock.getClassName()).thenReturn(LIST_CLASS_NAME);
        when(simulationDescriptorMock.getType()).thenReturn(RULE);

        setCollectionEditorStructureData();
    }

    @Test
    public void setCollectionEditorStructureData_ManageListDMN() {
        when(factMappingMock.getExpressionAlias()).thenReturn(LIST_CLASS_NAME);
        when(factMappingMock.getClassName()).thenReturn(LIST_CLASS_NAME);
        when(simulationDescriptorMock.getType()).thenReturn(DMN);

        setCollectionEditorStructureData();
    }

    private void setCollectionEditorStructureData() {
        collectionEditorSingletonDOMElementFactoryMock.setCollectionEditorStructureData(collectionEditorViewImpl, factMappingMock);
        boolean isRule = RULE.equals(simulationDescriptorMock.getType());
        String genericTypeName0 = factMappingMock.getGenericTypes().get(0);
        String genericTypeName1 = factMappingMock.getGenericTypes().get(1);
        if (isRule && !isSimpleJavaType(genericTypeName0)) {
            verify(collectionEditorSingletonDOMElementFactoryMock, times(1)).getRuleComplexType(eq(genericTypeName0));
            genericTypeName0 = genericTypeName0.substring(genericTypeName0.lastIndexOf(".") + 1);
        }
        String key = factMappingMock.getFactAlias() + "#" + factMappingMock.getExpressionAlias();
        if (ScenarioSimulationSharedUtils.isList(factMappingMock.getExpressionAlias())) {
            verify(collectionEditorSingletonDOMElementFactoryMock, times(1)).manageList(
                    eq(collectionEditorViewImpl), eq(key), eq(genericTypeName0));
        } else {
            verify(collectionEditorSingletonDOMElementFactoryMock, times(1)).manageMap(
                    eq(collectionEditorViewImpl), eq(key), eq(genericTypeName0), eq(genericTypeName1), eq(isRule));
        }
    }
}