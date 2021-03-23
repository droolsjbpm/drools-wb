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
package org.drools.workbench.screens.scenariosimulation.model.typedescriptor;

import java.util.Collections;

import org.drools.scenariosimulation.api.utils.ConstantsHolder;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FactModelTreeTest {

    private final static String FACT_NAME = "FactName";
    private final static String PACKAGE = "com";
    private final static String FACT_TYPE = "FactType";
    private final static String SIMPLE_PROPERTY_TYPE = "numeric";

    @Test
    public void ofDMOConstructorNoTypeName() {
        FactModelTree factModelTree = FactModelTree.ofDMO(FACT_NAME, PACKAGE, Collections.emptyMap(), Collections.emptyMap(), null);
        assertEquals(FACT_NAME, factModelTree.getFactName());
        assertEquals(FACT_NAME, factModelTree.getTypeName());
        assertEquals(PACKAGE, factModelTree.getFullPackage());
        assertEquals(PACKAGE + "." + FACT_NAME, factModelTree.getFullTypeName());
        assertEquals(FACT_NAME, factModelTree.getFactName());
        assertEquals(0, factModelTree.getSimpleProperties().size());
        assertEquals(0, factModelTree.getGenericTypesMap().size());
        assertEquals(FactModelTree.Type.UNDEFINED, factModelTree.getType());
        assertFalse(factModelTree.isSimple());
    }

    @Test
    public void ofDMOConstructorFactNameEqualsToTypeName() {
        FactModelTree factModelTree = FactModelTree.ofDMO(FACT_NAME, PACKAGE, Collections.emptyMap(), Collections.emptyMap(), FACT_NAME);
        assertEquals(FACT_NAME, factModelTree.getFactName());
        assertEquals(FACT_NAME, factModelTree.getTypeName());
        assertEquals(PACKAGE, factModelTree.getFullPackage());
        assertEquals(PACKAGE + "." + FACT_NAME, factModelTree.getFullTypeName());
        assertEquals(FACT_NAME, factModelTree.getFactName());
        assertEquals(0, factModelTree.getSimpleProperties().size());
        assertEquals(0, factModelTree.getGenericTypesMap().size());
        assertEquals(FactModelTree.Type.UNDEFINED, factModelTree.getType());
        assertFalse(factModelTree.isSimple());
    }

    @Test
    public void ofDMOConstructorFactNameNotEqualsToTypeName() {
        FactModelTree factModelTree = FactModelTree.ofDMO(FACT_NAME, PACKAGE, Collections.emptyMap(), Collections.emptyMap(), FACT_TYPE);
        assertEquals(FACT_NAME, factModelTree.getFactName());
        assertEquals(FACT_TYPE, factModelTree.getTypeName());
        assertEquals(PACKAGE, factModelTree.getFullPackage());
        assertEquals(PACKAGE + "." + FACT_TYPE, factModelTree.getFullTypeName());
        assertEquals(FACT_NAME, factModelTree.getFactName());
        assertEquals(0, factModelTree.getSimpleProperties().size());
        assertEquals(0, factModelTree.getGenericTypesMap().size());
        assertEquals(FactModelTree.Type.UNDEFINED, factModelTree.getType());
        assertFalse(factModelTree.isSimple());
    }

    @Test
    public void ofSimpleDMO() {
        FactModelTree factModelTree = FactModelTree.ofSimpleDMO(FACT_NAME, PACKAGE, SIMPLE_PROPERTY_TYPE, FACT_TYPE);
        assertEquals(FACT_NAME, factModelTree.getFactName());
        assertEquals(FACT_TYPE, factModelTree.getTypeName());
        assertEquals(PACKAGE, factModelTree.getFullPackage());
        assertEquals(PACKAGE + "." + FACT_TYPE, factModelTree.getFullTypeName());
        assertEquals(FACT_NAME, factModelTree.getFactName());
        assertEquals(1, factModelTree.getSimpleProperties().size());
        assertEquals(SIMPLE_PROPERTY_TYPE, factModelTree.getSimpleProperties().get(ConstantsHolder.VALUE).getTypeName());
        assertEquals(0, factModelTree.getGenericTypesMap().size());
        assertEquals(FactModelTree.Type.UNDEFINED, factModelTree.getType());
        assertTrue(factModelTree.isSimple());
    }

    @Test
    public void ofDMN() {
        FactModelTree factModelTree = FactModelTree.ofDMN(FACT_NAME, Collections.emptyMap(), Collections.emptyMap(), FACT_TYPE, FactModelTree.Type.INPUT);
        assertEquals(FACT_NAME, factModelTree.getFactName());
        assertEquals(FACT_TYPE, factModelTree.getTypeName());
        assertEquals("", factModelTree.getFullPackage());
        assertEquals(FACT_TYPE, factModelTree.getFullTypeName());
        assertEquals(FACT_NAME, factModelTree.getFactName());
        assertEquals(0, factModelTree.getSimpleProperties().size());
        assertEquals(0, factModelTree.getGenericTypesMap().size());
        assertEquals(FactModelTree.Type.INPUT, factModelTree.getType());
        assertFalse(factModelTree.isSimple());
    }

    @Test
    public void ofSimpleDMN() {
        FactModelTree factModelTree = FactModelTree.ofSimpleDMN(FACT_NAME, SIMPLE_PROPERTY_TYPE, Collections.emptyMap(), FACT_TYPE, FactModelTree.Type.INPUT);
        assertEquals(FACT_NAME, factModelTree.getFactName());
        assertEquals(FACT_TYPE, factModelTree.getTypeName());
        assertEquals("", factModelTree.getFullPackage());
        assertEquals(FACT_TYPE, factModelTree.getFullTypeName());
        assertEquals(FACT_NAME, factModelTree.getFactName());
        assertEquals(1, factModelTree.getSimpleProperties().size());
        assertEquals(SIMPLE_PROPERTY_TYPE, factModelTree.getSimpleProperties().get(ConstantsHolder.VALUE).getTypeName());
        assertEquals(0, factModelTree.getGenericTypesMap().size());
        assertEquals(FactModelTree.Type.INPUT, factModelTree.getType());
        assertTrue(factModelTree.isSimple());
    }

    @Test
    public void getTypeNameSetToNull() {
        FactModelTree factModelTree = new FactModelTree(FACT_NAME, PACKAGE, Collections.emptyMap(), Collections.emptyMap());
        assertEquals(FACT_NAME, factModelTree.getFactName());
        assertEquals(FACT_NAME, factModelTree.getTypeName());
        assertEquals(PACKAGE, factModelTree.getFullPackage());
        assertEquals(PACKAGE + "." + FACT_NAME, factModelTree.getFullTypeName());
        assertEquals(FACT_NAME, factModelTree.getFactName());
        assertEquals(FactModelTree.Type.UNDEFINED, factModelTree.getType());
    }

    @Test
    public void getTypeNameSet() {
        FactModelTree factModelTree = new FactModelTree(FACT_NAME, PACKAGE, Collections.emptyMap(), Collections.emptyMap(), FACT_TYPE);
        assertEquals(FACT_NAME, factModelTree.getFactName());
        assertEquals(FACT_TYPE, factModelTree.getTypeName());
        assertEquals(PACKAGE, factModelTree.getFullPackage());
        assertEquals(PACKAGE + "." + FACT_TYPE, factModelTree.getFullTypeName());
        assertEquals(FACT_NAME, factModelTree.getFactName());
        assertEquals(FactModelTree.Type.UNDEFINED, factModelTree.getType());
    }

    @Test
    public void getFullTypeName() {
        FactModelTree factModelTree = FactModelTree.ofDMO(FACT_NAME, PACKAGE, Collections.emptyMap(), Collections.emptyMap(), FACT_TYPE);
        assertEquals(PACKAGE + "." + FACT_TYPE, factModelTree.getFullTypeName());
        factModelTree = FactModelTree.ofDMO(FACT_NAME, "", Collections.emptyMap(), Collections.emptyMap(), FACT_TYPE);
        assertEquals(FACT_TYPE, factModelTree.getFullTypeName());
        factModelTree = FactModelTree.ofDMO(FACT_NAME, null, Collections.emptyMap(), Collections.emptyMap(), FACT_TYPE);
        assertEquals(FACT_TYPE, factModelTree.getFullTypeName());
        factModelTree = FactModelTree.ofDMO(FACT_NAME, "", Collections.emptyMap(), Collections.emptyMap(), FACT_NAME);
        assertEquals(FACT_NAME, factModelTree.getFullTypeName());
    }

}
