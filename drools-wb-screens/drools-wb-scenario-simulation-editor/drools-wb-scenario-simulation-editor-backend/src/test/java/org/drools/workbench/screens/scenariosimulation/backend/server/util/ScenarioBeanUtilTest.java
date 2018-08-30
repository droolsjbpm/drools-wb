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

package org.drools.workbench.screens.scenariosimulation.backend.server.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.workbench.screens.scenariosimulation.backend.server.model.Dispute;
import org.drools.workbench.screens.scenariosimulation.backend.server.model.NotEmptyConstructor;
import org.drools.workbench.screens.scenariosimulation.backend.server.model.Person;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ScenarioBeanUtilTest {

    private static String FIRST_NAME = "firstNameToSet";
    private static int AGE = 10;

    @Test
    public void fillBeanTest() {
        Map<List<String>, Object> paramsToSet = new HashMap<>();
        paramsToSet.put(Arrays.asList("creator", "firstName"), FIRST_NAME);
        paramsToSet.put(Arrays.asList("creator", "age"), AGE);

        Object result = ScenarioBeanUtil.fillBean(Dispute.class.getCanonicalName(), paramsToSet);

        assertTrue(result instanceof Dispute);

        Dispute dispute = (Dispute) result;
        assertEquals(dispute.getCreator().getFirstName(), FIRST_NAME);
        assertEquals(dispute.getCreator().getAge(), AGE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void fillBeanLoadClassTest() {
        ScenarioBeanUtil.fillBean("FakeCanonicalName", new HashMap<>());
    }

    @Test(expected = IllegalArgumentException.class)
    public void fillBeanFailNotEmptyConstructorTest() {
        Map<List<String>, Object> paramsToSet = new HashMap<>();
        paramsToSet.put(Arrays.asList("name"), null);

        ScenarioBeanUtil.fillBean(NotEmptyConstructor.class.getCanonicalName(), paramsToSet);
    }

    @Test(expected = IllegalArgumentException.class)
    public void fillBeanFailTest() {
        Map<List<String>, Object> paramsToSet = new HashMap<>();
        paramsToSet.put(Arrays.asList("fakeField"), null);

        ScenarioBeanUtil.fillBean(Dispute.class.getCanonicalName(), paramsToSet);
    }

    @Test
    public void navigateToObjectTest() {
        Dispute dispute = new Dispute();
        Person creator = new Person();
        creator.setFirstName(FIRST_NAME);
        dispute.setCreator(creator);
        List<String> pathToProperty = Arrays.asList("creator", "firstName");

        Object targetObject = ScenarioBeanUtil.navigateToObject(dispute, pathToProperty);

        assertEquals(targetObject, FIRST_NAME);
    }

    @Test(expected = IllegalArgumentException.class)
    public void navigateToObjectNoStepTest() {
        ScenarioBeanUtil.navigateToObject(new Dispute(), new ArrayList<>());
    }

    @Test(expected = IllegalArgumentException.class)
    public void navigateToObjectFakeFieldTest() {
        Dispute dispute = new Dispute();
        List<String> pathToProperty = Arrays.asList("fakeField");

        ScenarioBeanUtil.navigateToObject(dispute, pathToProperty);
    }
}