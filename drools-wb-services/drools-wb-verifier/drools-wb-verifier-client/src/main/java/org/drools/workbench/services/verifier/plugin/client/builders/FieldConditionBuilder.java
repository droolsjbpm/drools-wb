/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.services.verifier.plugin.client.builders;

import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.services.verifier.api.client.checks.util.NullEqualityOperator;
import org.drools.workbench.services.verifier.api.client.configuration.AnalyzerConfiguration;
import org.drools.workbench.services.verifier.api.client.index.Column;
import org.drools.workbench.services.verifier.api.client.index.Condition;
import org.drools.workbench.services.verifier.api.client.index.Field;
import org.drools.workbench.services.verifier.api.client.index.FieldCondition;
import org.drools.workbench.services.verifier.api.client.index.Index;
import org.drools.workbench.services.verifier.api.client.index.Pattern;
import org.drools.workbench.services.verifier.api.client.index.keys.Values;
import org.kie.soup.commons.validation.PortablePreconditions;

public class FieldConditionBuilder {

    private final BuilderFactory builderFactory;
    private final Index index;
    private final VerifierColumnUtilities utils;
    private final AnalyzerConfiguration configuration;
    private Pattern pattern;
    private ConditionCol52 conditionCol52;
    private DTCellValue52 realCellValue;
    private int columnIndex;

    public FieldConditionBuilder(final BuilderFactory builderFactory,
                                 final Index index,
                                 final VerifierColumnUtilities utils,
                                 final AnalyzerConfiguration configuration) {
        this.builderFactory = PortablePreconditions.checkNotNull("builderFactory",
                                                                 builderFactory);
        this.index = PortablePreconditions.checkNotNull("index",
                                                        index);
        this.utils = PortablePreconditions.checkNotNull("utils",
                                                        utils);
        this.configuration = PortablePreconditions.checkNotNull("configuration",
                                                                configuration);
    }

    public Condition build() throws
            BuildException {

        PortablePreconditions.checkNotNull("conditionCol52",
                                           conditionCol52);
        PortablePreconditions.checkNotNull("columnIndex",
                                           columnIndex);

        try {
            final Field field = resolveField();

            final Condition condition = buildCondition(field);
            field.getConditions()
                    .add(condition);
            return condition;
        } catch (final BuildException buildException) {
            throw buildException;
        } catch (final Exception e) {
            throw new BuildException("Failed to build " + pattern.getName() + " # " + ToString.toString(conditionCol52));
        }
    }

    private Field resolveField() throws
            BuildException {
        try {
            return builderFactory.getFieldResolver()
                    .with(pattern)
                    .with(columnIndex)
                    .with(conditionCol52)
                    .resolve();
        } catch (final Exception e) {
            throw new BuildException("Failed to resolve field " + pattern.getName() + " # " + ToString.toString(conditionCol52));
        }
    }

    private Condition buildCondition(final Field field) throws
            BuildException {
        try {

            final Column column = getColumn();

            return new FieldCondition(field,
                                      column,
                                      resolveOperator(conditionCol52.getOperator()),
                                      resolveValues(conditionCol52.getOperator()),
                                      configuration);
        } catch (final BuildException e) {
            throw e;
        } catch (final Exception e) {
            throw new BuildException("Failed to build FieldCondition ");
        }
    }

    private Values resolveValues(final String operator) throws
            BuildException {

        if (NullEqualityOperator.contains(operator)) {
            if (realCellValue.getBooleanValue() != null && realCellValue.getBooleanValue()) {
                return Values.nullValue();
            } else {
                return new Values();
            }
        } else {
            try {
                Values values = new ValuesResolver(utils,
                                                   columnIndex,
                                                   conditionCol52,
                                                   realCellValue).getValues();
                return values;
            } catch (final Exception e) {
                throw new BuildException("Failed to resolve values:" + ToString.toString(conditionCol52) + " " + ToString.toString(realCellValue) + e.getMessage());
            }
        }
    }

    private String resolveOperator(final String operator) {
        if (NullEqualityOperator.contains(operator)) {
            return NullEqualityOperator.resolveOperator(operator);
        } else {
            return operator;
        }
    }

    private Column getColumn() throws
            BuildException {
        try {

            return index.getColumns()
                    .where(Column.index()
                                   .is(columnIndex))
                    .select()
                    .first();
        } catch (final Exception e) {
            throw new BuildException("Failed to find column ");
        }
    }

    public FieldConditionBuilder with(final Pattern pattern) {
        this.pattern = pattern;
        return this;
    }

    public FieldConditionBuilder with(final ConditionCol52 conditionCol52) {
        this.conditionCol52 = PortablePreconditions.checkNotNull("conditionCol52",
                                                                 conditionCol52);
        return this;
    }

    public FieldConditionBuilder with(final DTCellValue52 realCellValue) {
        this.realCellValue = realCellValue;
        return this;
    }

    public FieldConditionBuilder with(final int columnIndex) {
        this.columnIndex = columnIndex;
        return this;
    }
}
