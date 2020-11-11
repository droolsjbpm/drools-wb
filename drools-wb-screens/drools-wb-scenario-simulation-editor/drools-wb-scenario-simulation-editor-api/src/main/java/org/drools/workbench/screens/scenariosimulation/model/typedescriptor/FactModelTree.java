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
package org.drools.workbench.screens.scenariosimulation.model.typedescriptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.jboss.errai.common.client.api.annotations.Portable;

/**
 * Class used to recursively represent a given fact with its ModelFields eventually expanded
 */
@Portable
public class FactModelTree {

    public enum Type {
        INPUT,
        DECISION,
        UNDEFINED
    }

    private String factName;  // The name of the asset
    private String fullPackage;  // The package of the asset
    private boolean isSimple = false;
    /**
     * Map of the simple properties: key = property name, value = property' type name
     */
    private Map<String, PropertyTypeName> simpleProperties;
    /**
     * Map of the collection' properties generic-type: key = property name (ex "books"), value = list of property' generic types (ex "com.Book")
     */
    private Map<String, List<String>> genericTypesMap;
    /**
     * Map of the expandable properties: key = property name, value = property' expandable/complex type
     */
    private Map<String, String> expandableProperties = new HashMap<>();
    private Type type;

    public FactModelTree() {
        // CDI
    }

    /**
     * Call this constructor to have a <code>FactModelTree</code> with <b>UNDEFINED</b> <code>Type</code>
     * @param factName
     * @param fullPackage
     * @param simpleProperties
     * @param genericTypesMap the <b>generic type</b> info, in the format {collection_class_name}#{generic_type}: ex "java.util.List#com.Book"
     */
    public FactModelTree(String factName, String fullPackage, Map<String, PropertyTypeName> simpleProperties, Map<String, List<String>> genericTypesMap) {
        this(factName, fullPackage, simpleProperties, genericTypesMap, Type.UNDEFINED);
    }

    /**
     * Call this constructor to specify the <code>FactModelTree</code>' <code>Type</code>
     * @param factName
     * @param fullPackage
     * @param simpleProperties
     * @param genericTypesMap the <b>generic type</b> info, in the format {collection_class_name}#{generic_type}: ex "java.util.List#com.Book"
     * @param type
     */
    public FactModelTree(String factName, String fullPackage, Map<String, PropertyTypeName> simpleProperties, Map<String, List<String>> genericTypesMap, Type type) {
        this.factName = factName;
        this.fullPackage = fullPackage;
        this.simpleProperties = simpleProperties;
        this.genericTypesMap = genericTypesMap;
        this.type = type;
    }

    public String getFactName() {
        return factName;
    }

    public String getFullPackage() {
        return fullPackage;
    }

    public Map<String, PropertyTypeName> getSimpleProperties() {
        return simpleProperties;
    }

    public Map<String, String> getExpandableProperties() {
        return expandableProperties;
    }

    public Map<String, List<String>> getGenericTypesMap() {
        return genericTypesMap;
    }

    public void addSimpleProperty(String propertyName, PropertyTypeName propertyTypeName) {
        simpleProperties.put(propertyName, propertyTypeName);
    }

    public void addExpandableProperty(String propertyName, String propertyType) {
        expandableProperties.put(propertyName, propertyType);
    }

    /**
     * Returns the <b>generic type</b> info of the given <b>property</b>
     * @param propertyName
     * @return the <code>List</code> of <b>generic type</b>s, or an <b>empty</b> one
     */
    public List<String> getGenericTypeInfo(String propertyName) {
        return genericTypesMap.getOrDefault(propertyName, new ArrayList<>());
    }

    public void removeSimpleProperty(String propertyName) {
        simpleProperties.remove(propertyName);
    }

    public boolean isSimple() {
        return isSimple;
    }

    public void setSimple(boolean simple) {
        isSimple = simple;
    }

    public Type getType() {
        return type;
    }

    public FactModelTree cloneFactModelTree() {
        Map<String, PropertyTypeName> clonedSimpleProperties = new HashMap<>(simpleProperties);
        Map<String, List<String>> clonedGenericTypesMap =
                genericTypesMap.entrySet()
                        .stream()
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                e -> {
                                    List<String> toReturn =  new ArrayList<>();
                                    toReturn.addAll(e.getValue());
                                    return toReturn;
                                }

                        ));
        FactModelTree toReturn = new FactModelTree(factName, fullPackage, clonedSimpleProperties, clonedGenericTypesMap, type);
        toReturn.expandableProperties = new HashMap<>(expandableProperties);
        toReturn.isSimple = isSimple;
        return toReturn;
    }

    @Override
    public String toString() {
        return "FactModelTree{" +
                "factName='" + factName + '\'' +
                ", simpleProperties=" + simpleProperties +
                ", expandableProperties=" + expandableProperties +
                '}';
    }

    /**
     * Nested DTO which holds a Property name. This is required in case of DMN Type scenario, where, if a DMNType
     * contains a baseType, it takes the type name from its baseType.
     * This is to manage DMN Simple Types and Anonymous inner types (please refer to DMN documentation for further info)
     * For all other cases, typeName is the name to use and show.
     * - typeName: It is the real type name;
     * - baseTypeName: Optional, it represents the baseType of a DMNType. If is present, this should be shown in UI but
     *                 ignored on all other steps.
     */
    public static class PropertyTypeName {

        private String typeName;
        private Optional<String> baseTypeName;

        public PropertyTypeName() {
            typeName = null;
            baseTypeName = Optional.empty();
        }

        public PropertyTypeName(String typeName) {
            this.typeName = typeName;
            this.baseTypeName = Optional.empty();
        }

        public PropertyTypeName(String typeName, String baseTypeName) {
            this.typeName = typeName;
            this.baseTypeName = Optional.ofNullable(baseTypeName);
        }

        public String getTypeName() {
            return typeName;
        }

        public Optional<String> getBaseTypeName() {
            return baseTypeName;
        }

        public void setTypeName(String typeName) {
            this.typeName = typeName;
        }

        public void setBaseTypeName(String baseTypeName) {
            this.baseTypeName = Optional.ofNullable(baseTypeName);
        }

        /**
         * This method returns the correct <b>type</b> name of the property to be visualized in UI. In case of nested
         * objects, '$' character is substituted with `.`/
         * @return
         */
        public String getPropertyTypeNameToVisualize() {
            return baseTypeName.orElse(typeName.replace('$', '.'));
        }
    }
}
