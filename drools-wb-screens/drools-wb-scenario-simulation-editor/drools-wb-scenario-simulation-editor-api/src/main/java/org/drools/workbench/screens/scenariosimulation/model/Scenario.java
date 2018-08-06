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
package org.drools.workbench.screens.scenariosimulation.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.jboss.errai.common.client.api.annotations.Portable;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@Portable
public class Scenario {

    private String description;
    /**
     * List of values to be used to test this scenario
     */
    private List<FactMappingValue> factMappingValues = new ArrayList<>();

    private SimulationDescriptor simulationDescriptor = new SimulationDescriptor();

    public Scenario() {
    }

    public Scenario(String description, SimulationDescriptor simulationDescriptor) {
        this.description = description;
        this.simulationDescriptor = simulationDescriptor;
    }

    public List<FactMappingValue> getFactMappingValues() {
        return Collections.unmodifiableList(factMappingValues);
    }

    public FactMappingValue addMappingValue(FactIdentifier factIdentifier, ExpressionIdentifier expressionIdentifier, String value) {
        String factName = factIdentifier.getName();
        if (getFactMappingValue(factIdentifier, expressionIdentifier).isPresent()) {
            throw new IllegalArgumentException(
                    new StringBuilder().append("A fact value for expression '").append(expressionIdentifier.getName())
                            .append("' and fact '").append(factName).append("' already exist").toString());
        }
        FactMappingValue factMappingValue = new FactMappingValue(factIdentifier, expressionIdentifier, value);
        factMappingValues.add(factMappingValue);
        return factMappingValue;
    }

    public Optional<FactMappingValue> getFactMappingValue(FactIdentifier factIdentifier, ExpressionIdentifier expressionIdentifier) {
        return factMappingValues.stream().filter(e -> e.getFactIdentifier().getName().equalsIgnoreCase(factIdentifier.getName()) &&
                e.getExpressionIdentifier().equals(expressionIdentifier)).findFirst();
    }

    public Optional<FactMappingValue> getFactMappingValueByIndex(int index) {
        FactMapping factMappingByIndex = simulationDescriptor.getFactMappingByIndex(index);
        if (factMappingByIndex == null) {
            throw new IllegalArgumentException("Impossible to retrieve FactMapping at index " + index);
        }
        return getFactMappingValue(factMappingByIndex.getFactIdentifier(), factMappingByIndex.getExpressionIdentifier());
    }

    public List<FactMappingValue> getFactMappingValuesByFactIdentifier(FactIdentifier factIdentifier) {
        return factMappingValues.stream().filter(e -> e.getFactIdentifier().equals(factIdentifier)).collect(toList());
    }

    public String getDescription() {
        return description;
    }

    public Collection<String> getFactNames() {
        return factMappingValues.stream().map(e -> e.getFactIdentifier().getName()).collect(toSet());
    }
}