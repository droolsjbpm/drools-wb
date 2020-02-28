/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.scenariosimulation.kogito.client.dmn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.namespace.QName;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.screens.scenariosimulation.kogito.client.dmn.feel.BuiltInType;
import org.drools.workbench.screens.scenariosimulation.model.typedescriptor.FactModelTree;
import org.drools.workbench.screens.scenariosimulation.model.typedescriptor.FactModelTuple;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDRGElement;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDecision;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDefinitions;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITInputData;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITItemDefinition;
import org.mockito.Mock;
import org.uberfire.backend.vfs.Path;

import static junit.framework.TestCase.assertNotNull;
import static org.drools.scenariosimulation.api.utils.ConstantsHolder.VALUE;
import static org.drools.workbench.screens.scenariosimulation.kogito.client.dmn.AbstractKogitoDMNService.TYPEREF_QNAME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class AbstractKogitoDMNServiceTest {

    public static final String NAMESPACE = "namespace";
    public static final String NAME = "name";
    public static final String ID = "id";

    @Mock
    private JSITItemDefinition jsitItemDefinitionMock;
    @Mock
    private JSITItemDefinition jsitItemDefinitionNestedMock;
    @Mock
    private JSITDefinitions jsiITDefinitionsMock;
    @Mock
    private JSITDecision jsiITDecisionMock;
    @Mock
    private JSITInputData jsiITInputDataMock;

    private AbstractKogitoDMNService abstractKogitoDMNServiceSpy;
    private List<JSITItemDefinition> jstiItemDefinitions;
    private List<JSITDRGElement> jsitdrgElements;
    private List<JSITDRGElement> drgElements;


    @Before
    public void setup() {
        abstractKogitoDMNServiceSpy = spy(new AbstractKogitoDMNService() {
            @Override
            public void getDMNContent(Path path, RemoteCallback<String> remoteCallback, ErrorCallback<Object> errorCallback) {
                //Do nothing
            }
        });
        when(abstractKogitoDMNServiceSpy.isJSITInputData(eq(jsiITInputDataMock))).thenReturn(true);
        when(abstractKogitoDMNServiceSpy.isJSITDecision(eq(jsiITDecisionMock))).thenReturn(true);
        jstiItemDefinitions = new ArrayList<>();
        jsitdrgElements = new ArrayList<>();
        drgElements = new ArrayList<>();
        when(jsiITDefinitionsMock.getNamespace()).thenReturn(NAMESPACE);
        when(jsiITDefinitionsMock.getItemDefinition()).thenReturn(jstiItemDefinitions);
        when(jsiITDefinitionsMock.getDrgElement()).thenReturn(jsitdrgElements);
        when(jsitItemDefinitionMock.getName()).thenReturn(NAME);
        when(jsitItemDefinitionMock.getId()).thenReturn(ID);
        when(jsitItemDefinitionMock.getIsCollection()).thenReturn(false);
    }

    @Test
    public void retrieveFactModelTupleDmnListEmptyElements() {
        FactModelTuple factModelTuple = abstractKogitoDMNServiceSpy.getFactModelTuple(jsiITDefinitionsMock);
        assertTrue(factModelTuple.getVisibleFacts().isEmpty());
        assertTrue(factModelTuple.getHiddenFacts().isEmpty());
    }

    //TODO getFactModelTuple()

    /*public void retrieveFactModelTupleDmnList() {
        drgElements.add(jsiITInputDataMock);
        when(jsiITDefinitionsMock.getDrgElement()).thenReturn(drgElements);
        FactModelTuple factModelTuple = abstractKogitoDMNServiceSpy.getFactModelTuple(jsiITDefinitionsMock);
        // VisibleFacts should match inputs and decisions on given model
        //int expectedVisibleFacts = dmnModelLocal.getInputs().size() + dmnModelLocal.getDecisions().size();
        //assertEquals(expectedVisibleFacts, factModelTuple.getVisibleFacts().size());
        // Verify each inputDataNode has been correctly mapped
        //dmnModelLocal.getInputs().forEach(inputDataNode -> verifyFactModelTree(factModelTuple, inputDataNode, factModelTuple.getHiddenFacts()));
        // Verify each decisionNode has been correctly mapped
        //dmnModelLocal.getDecisions().forEach(decisionNode -> verifyFactModelTree(factModelTuple, decisionNode, factModelTuple.getHiddenFacts()));
    }

    /*@Test
    public void retrieveFactModelTupleDmnListComposite()  {
        setDmnModelLocal("dmn-list-composite.dmn", "https://github.com/kiegroup/drools/kie-dmn/_25BF2679-3109-488F-9AD1-DDBCCEBBE5F1", "dmn-list-composite");
        FactModelTuple factModelTuple = dmnTypeServiceImpl.retrieveFactModelTuple(mock(Path.class), null);
        // VisibleFacts should match inputs and decisions on given model
        int expectedVisibleFacts = dmnModelLocal.getInputs().size() + dmnModelLocal.getDecisions().size();
        assertEquals(expectedVisibleFacts, factModelTuple.getVisibleFacts().size());
        // Verify each inputDataNode has been correctly mapped
        dmnModelLocal.getInputs().forEach(inputDataNode -> verifyFactModelTree(factModelTuple, inputDataNode, factModelTuple.getHiddenFacts()));
        // Verify each decisionNode has been correctly mapped
        dmnModelLocal.getDecisions().forEach(decisionNode -> verifyFactModelTree(factModelTuple, decisionNode, factModelTuple.getHiddenFacts()));
    }*/

    @Test
    public void getDMNTypeFromMaps() {
        Map<String, ClientDMNType> dmnTypesMap = abstractKogitoDMNServiceSpy.getDMNTypesMap(jstiItemDefinitions, NAMESPACE);
        Map<QName, String> source = new HashMap<>();
        source.put(TYPEREF_QNAME, "number");
        ClientDMNType clientDMNType = abstractKogitoDMNServiceSpy.getDMNTypeFromMaps(dmnTypesMap, source);
        assertNotNull(clientDMNType);
        assertTrue(BuiltInType.NUMBER.equals(clientDMNType.getFeelType()));
    }

    @Test
    public void getDMNTypesMapEmptyItemDefinitions() {
        Map<String, ClientDMNType> dmnTypesMap = abstractKogitoDMNServiceSpy.getDMNTypesMap(jstiItemDefinitions, NAMESPACE);
        // It must contains all elements defined in BuiltInType ENUM without ANY
        assertTrue(dmnTypesMap.size() == 14);
        for (Map.Entry<String, ClientDMNType> entry : dmnTypesMap.entrySet()) {
            assertNotNull(entry.getValue().getFeelType());
            assertTrue(Arrays.stream(entry.getValue().getFeelType().getNames()).anyMatch(entry.getKey()::equals));
        }
        for (BuiltInType builtInType : BuiltInType.values()) {
            Arrays.stream(builtInType.getNames()).forEach(name -> assertNotNull(dmnTypesMap.get(name)));
        }
    }

    @Test
    public void getDMNTypesMapWithItemDefinitions() {
        jstiItemDefinitions.add(jsitItemDefinitionMock);
        Map<String, ClientDMNType> dmnTypesMap = abstractKogitoDMNServiceSpy.getDMNTypesMap(jstiItemDefinitions, NAMESPACE);
        // It must contains all elements defined in BuiltInType ENUM + one defined jstiItemDefinitionMock item
        assertTrue(dmnTypesMap.size() == 15);
        for (BuiltInType builtInType : BuiltInType.values()) {
            Arrays.stream(builtInType.getNames()).forEach(name -> assertNotNull(dmnTypesMap.get(name)));
        }
        assertNotNull(dmnTypesMap.get(NAME));
        assertNull(dmnTypesMap.get(NAME).getFeelType());
        assertEquals(NAME, dmnTypesMap.get(NAME).getName());
        assertEquals(NAMESPACE, dmnTypesMap.get(NAME).getNamespace());
        assertFalse(dmnTypesMap.get(NAME).isCollection());
    }

    @Test
    public void getDMNTypeNullItems() {
        ClientDMNType clientDmnType = abstractKogitoDMNServiceSpy.getDMNType(jsitItemDefinitionMock, NAMESPACE, new HashMap<>());
        assertEquals(NAMESPACE, clientDmnType.getNamespace());
        assertEquals(NAME, clientDmnType.getName());
        assertFalse(clientDmnType.isCollection());
        assertNull(clientDmnType.getFeelType());
    }

    @Test
    public void getDMNTypeEmptyItems() {
        when(jsitItemDefinitionMock.getItemComponent()).thenReturn(new ArrayList<>());
        ClientDMNType clientDmnType = abstractKogitoDMNServiceSpy.getDMNType(jsitItemDefinitionMock, NAMESPACE, new HashMap<>());
        assertEquals(NAMESPACE, clientDmnType.getNamespace());
        assertEquals(NAME, clientDmnType.getName());
        assertFalse(clientDmnType.isCollection());
        assertFalse(clientDmnType.isComposite());
        assertTrue(clientDmnType.getFields().isEmpty());
        assertNull(clientDmnType.getFeelType());
    }

    @Test
    public void getDMNTypeItems() {
        when(jsitItemDefinitionMock.getItemComponent()).thenReturn(Arrays.asList(jsitItemDefinitionNestedMock));
        ClientDMNType clientDmnType = abstractKogitoDMNServiceSpy.getDMNType(jsitItemDefinitionMock, NAMESPACE, new HashMap<>());
        assertEquals(NAMESPACE, clientDmnType.getNamespace());
        assertEquals(NAME, clientDmnType.getName());
        assertFalse(clientDmnType.isCollection());
        assertTrue(clientDmnType.isComposite());
        assertNotNull(clientDmnType.getFields());
        assertTrue(clientDmnType.getFields().size() == 1);
        assertNull(clientDmnType.getFeelType());
    }

    //TODO other cases for getDMNType

    //TODO getItemDefinitionComparator tests

    @Test
    public void createTopLevelFactModelTreeSimpleNoCollection() {
        // Single property retrieve
        ClientDMNType simpleString = new ClientDMNType(NAMESPACE, NAME, null, false, false, null, null);
        FactModelTree retrieved = abstractKogitoDMNServiceSpy.createTopLevelFactModelTree("testPath", simpleString, new TreeMap<>(), FactModelTree.Type.INPUT);
        Assert.assertNotNull(retrieved);
        assertEquals("testPath", retrieved.getFactName());
        assertEquals(1, retrieved.getSimpleProperties().size());
        assertTrue(retrieved.getSimpleProperties().containsKey(VALUE));
        assertEquals(simpleString.getName(), retrieved.getSimpleProperties().get(VALUE));
        assertTrue(retrieved.getExpandableProperties().isEmpty());
        assertTrue(retrieved.getGenericTypesMap().isEmpty());
    }

    @Test
    public void createTopLevelFactModelTreeSimpleCollection() {
        // Single property collection retrieve
        ClientDMNType simpleCollectionString = new ClientDMNType(NAMESPACE, NAME, null, true, false, null, null);
        TreeMap<String, FactModelTree> hiddenFactSimpleCollection = new TreeMap<>();
        FactModelTree retrieved = abstractKogitoDMNServiceSpy.createTopLevelFactModelTree("testPath", simpleCollectionString, hiddenFactSimpleCollection, FactModelTree.Type.INPUT);
        Assert.assertNotNull(retrieved);
        assertEquals("testPath", retrieved.getFactName());
        assertEquals(1, retrieved.getSimpleProperties().size());
        assertTrue(retrieved.getSimpleProperties().containsKey(VALUE));
        assertEquals("java.util.List", retrieved.getSimpleProperties().get(VALUE));
        assertTrue(retrieved.getExpandableProperties().isEmpty());
        assertEquals(1, retrieved.getGenericTypesMap().size());
        assertTrue(retrieved.getGenericTypesMap().containsKey(VALUE));
        Assert.assertNotNull(retrieved.getGenericTypesMap().get(VALUE));
        assertEquals(1, retrieved.getGenericTypesMap().get(VALUE).size());
        assertEquals(simpleCollectionString.getName(), retrieved.getGenericTypesMap().get(VALUE).get(0));
    }

    /*
    @Test
    public void createTopLevelFactModelTreeCompositeNoCollection() {
        // Single property retrieve
        ClientDMNType compositePerson = new ClientDMNType(NAMESPACE, NAME, null, true, true, new LinkedHashMap<>(), null);
        FactModelTree retrieved = abstractKogitoDMNServiceSpy.createTopLevelFactModelTree("testPath", compositePerson, new TreeMap<>(), FactModelTree.Type.INPUT);
        Assert.assertNotNull(retrieved);
        assertEquals("testPath", retrieved.getFactName());
        assertEquals(2, retrieved.getSimpleProperties().size());
        assertTrue(retrieved.getSimpleProperties().containsKey("friends"));
        assertEquals("java.util.List", retrieved.getSimpleProperties().get("friends"));
        assertTrue(retrieved.getSimpleProperties().containsKey("name"));
        assertEquals(NAME, retrieved.getSimpleProperties().get("name"));
        //
        assertEquals(1, retrieved.getGenericTypesMap().size());
        assertTrue(retrieved.getGenericTypesMap().containsKey("friends"));
        assertEquals(compositePerson.getFields().get("friends").getName(), retrieved.getGenericTypesMap().get("friends").get(0));
        //
        assertEquals(2, retrieved.getExpandableProperties().size());
        /*assertTrue(retrieved.getExpandableProperties().containsKey(EXPANDABLE_PROPERTY_PHONENUMBERS));
        assertEquals("tPhoneNumber", retrieved.getExpandableProperties().get(EXPANDABLE_PROPERTY_PHONENUMBERS));
        assertTrue(retrieved.getExpandableProperties().containsKey(EXPANDABLE_PROPERTY_DETAILS));
        assertEquals("tDetails", retrieved.getExpandableProperties().get(EXPANDABLE_PROPERTY_DETAILS));
    }

    @Test
    public void createTopLevelFactModelTreeCompositeCollection() {
        // Single property collection retrieve
        ClientDMNType compositePerson = new ClientDMNType(NAMESPACE, NAME, null, true, true, new LinkedHashMap<>(), null);
        TreeMap<String, FactModelTree> hiddenFactSimpleCollection = new TreeMap<>();
        FactModelTree retrieved = abstractKogitoDMNServiceSpy.createTopLevelFactModelTree("testPath", compositePerson, hiddenFactSimpleCollection, FactModelTree.Type.INPUT);
        Assert.assertNotNull(retrieved);
        assertEquals("testPath", retrieved.getFactName());
        assertEquals(1, retrieved.getSimpleProperties().size());
        assertTrue(retrieved.getSimpleProperties().containsKey(VALUE));
        assertEquals("java.util.List", retrieved.getSimpleProperties().get(VALUE));
        assertTrue(retrieved.getExpandableProperties().isEmpty());
        assertEquals(1, retrieved.getGenericTypesMap().size());
        assertTrue(retrieved.getGenericTypesMap().containsKey(VALUE));
        Assert.assertNotNull(retrieved.getGenericTypesMap().get(VALUE));
        assertEquals(1, retrieved.getGenericTypesMap().get(VALUE).size());
        assertEquals(compositePerson.getName(), retrieved.getGenericTypesMap().get(VALUE).get(0));
    }

    @Test
    public void createTopLevelFactModelTreeRecursiveTypes() {
        SortedMap<String, FactModelTree> hiddenFacts = new TreeMap<>();
        FactModelTree person = dmnTypeServiceImpl.createTopLevelFactModelTree("person", getRecursivePersonComposite(false), hiddenFacts, FactModelTree.Type.DECISION);
        Assert.assertNotNull(person);
        assertTrue(person.getExpandableProperties().containsKey("parent"));
        assertEquals("tPerson", person.getExpandableProperties().get("parent"));
        assertTrue(hiddenFacts.containsKey("tPerson"));

        hiddenFacts = new TreeMap<>();
        FactModelTree personCollection = dmnTypeServiceImpl.createTopLevelFactModelTree("person", getRecursivePersonComposite(true), hiddenFacts, FactModelTree.Type.DECISION);

        Assert.assertNotNull(personCollection);
        assertTrue(personCollection.getGenericTypesMap().containsKey(VALUE));
        assertEquals("tPerson", personCollection.getGenericTypeInfo(VALUE).get(0));
        assertTrue(hiddenFacts.containsKey("tPerson"));
        assertTrue(hiddenFacts.containsKey("tPersonList"));
    }*/
}


