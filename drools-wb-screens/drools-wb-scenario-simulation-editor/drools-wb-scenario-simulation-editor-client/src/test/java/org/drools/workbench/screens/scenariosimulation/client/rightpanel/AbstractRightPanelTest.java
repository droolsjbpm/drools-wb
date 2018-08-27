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

package org.drools.workbench.screens.scenariosimulation.client.rightpanel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.stream.IntStream;

import com.google.gwt.dom.client.LIElement;
import org.drools.workbench.screens.scenariosimulation.client.models.FactModelTree;
import org.junit.Before;
import org.mockito.Mock;

abstract class AbstractRightPanelTest {

    @Mock
    protected LIElement mockLIElement;

    Map<String, FactModelTree> mockTopLevelMap;
    String FACT_NAME;
    FactModelTree FACT_MODEL_TREE;

    @Before
    public void setup() {
        mockTopLevelMap = getMockTopLevelMap();
        FACT_NAME = new ArrayList<>(mockTopLevelMap.keySet()).get(0);
        FACT_MODEL_TREE = mockTopLevelMap.get(FACT_NAME);
    }

    protected String getRandomFactModelTree(Map<String, FactModelTree> source, int position) {
        return new ArrayList<>(source.keySet()).get(position);
    }

    private Map<String, FactModelTree> getMockTopLevelMap() {
        Map<String, FactModelTree> toReturn = new HashMap<>();
        IntStream
                .range(0, 3)
                .forEach(id -> {
                    String key = getRandomString();
                    FactModelTree value = new FactModelTree(key, getMockSimpleProperties());
                    toReturn.put(key, value);
                    if (id == 1) {
                        value.addSimpleProperty(getRandomString(), getRandomFactModelTree(toReturn, 0));
                    }
                    if (id == 2) {
                        value.addSimpleProperty(getRandomString(), getRandomFactModelTree(toReturn, 1));
                        // Recursion
                        value.addSimpleProperty(getRandomString(), value.getFactName());
                    }
                });
        return toReturn;
    }

    private Map<String, String> getMockSimpleProperties() {
        Map<String, String> toReturn = new HashMap<>();
        IntStream
                .range(0, +3)
                .forEach(id -> toReturn.put(getRandomString(), getRandomType()));
        return toReturn;
    }

    private String getRandomString() {
        String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder builder = new StringBuilder();
        int numberOfLetters =  letters.length();
        Random random = new Random();
        int sizeOfRandomString = random.nextInt(6) + 3;
        IntStream
                .range(0, sizeOfRandomString)
                .forEach(position -> builder.append(letters.charAt(random.nextInt(numberOfLetters))));
        return builder.toString();
    }

    private String getRandomType() {
        int type = new Random().nextInt(4);
        switch (type) {
            case 0:
                return "lava.lang.String";
            case 1:
                return "byte";
            case 2:
                return "java.lang.Integer";
            case 3:
                return "java.lang.Boolean";
            default:
                return "int";
        }
    }
}