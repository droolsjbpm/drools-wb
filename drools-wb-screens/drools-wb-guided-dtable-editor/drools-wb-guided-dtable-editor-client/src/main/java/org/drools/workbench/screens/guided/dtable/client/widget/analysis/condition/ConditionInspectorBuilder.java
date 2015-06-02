/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.screens.guided.dtable.client.widget.analysis.condition;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;

import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.screens.guided.dtable.client.utils.GuidedDecisionTableUtils;

public class ConditionInspectorBuilder {

    private       GuidedDecisionTableUtils utils;
    private final Pattern52                pattern;
    private       ConditionCol52           conditionColumn;
    private final DTCellValue52            realCellValue;

    public ConditionInspectorBuilder(GuidedDecisionTableUtils utils,
                                     Pattern52 pattern,
                                     ConditionCol52 conditionColumn,
                                     DTCellValue52 realCellValue) {
        this.utils = utils;
        this.pattern = pattern;
        this.conditionColumn = conditionColumn;
        this.realCellValue = realCellValue;
    }

    @SuppressWarnings("rawtypes")
    public ConditionInspector buildConditionInspector() {
        String type = utils.getType(conditionColumn);

        if (isTypeGuvnorEnum(conditionColumn)) {
            // Guvnor enum
            return new EnumConditionInspector(pattern,
                                             conditionColumn.getFactField(),
                                             Arrays.asList(utils.getValueList(conditionColumn)),
                                             realCellValue.getStringValue(),
                                             conditionColumn.getOperator());
        } else if (type == null) {
            // type null means the field is free-format
            return new UnrecognizedConditionInspector(pattern,
                                                     conditionColumn.getFactField(),
                                                     conditionColumn.getOperator());
        } else if (type.equals(DataType.TYPE_STRING)) {
            return new StringConditionInspector(pattern,
                                               conditionColumn.getFactField(),
                                               realCellValue.getStringValue(),
                                               conditionColumn.getOperator());
        } else if (type.equals(DataType.TYPE_NUMERIC) || type.equals(DataType.TYPE_NUMERIC_BIGDECIMAL)) {
            return new NumericBigDecimalConditionInspector(pattern,
                                                          conditionColumn.getFactField(),
                    getBigDecimalValue(),
                                                          conditionColumn.getOperator());
        } else if (type.equals(DataType.TYPE_NUMERIC_BIGINTEGER)) {
            return new NumericBigIntegerConditionInspector(pattern,
                                                          conditionColumn.getFactField(),
                    getBigIntegerValue(),
                                                          conditionColumn.getOperator());
        } else if (type.equals(DataType.TYPE_NUMERIC_BYTE)) {
            return new NumericByteConditionInspector(pattern,
                                                    conditionColumn.getFactField(),
                                                    (Byte) realCellValue.getNumericValue(),
                                                    conditionColumn.getOperator());
        } else if (type.equals(DataType.TYPE_NUMERIC_DOUBLE)) {
            return new NumericDoubleConditionInspector(pattern,
                                                      conditionColumn.getFactField(),
                    getDoubleValue(),
                                                      conditionColumn.getOperator());
        } else if (type.equals(DataType.TYPE_NUMERIC_FLOAT)) {
            return new NumericFloatConditionInspector(pattern,
                                                     conditionColumn.getFactField(),
                                                     (Float) realCellValue.getNumericValue(),
                                                     conditionColumn.getOperator());
        } else if (type.equals(DataType.TYPE_NUMERIC_INTEGER)) {
            return new NumericIntegerConditionInspector(pattern,
                                                       conditionColumn.getFactField(),
                    getIntegerValue(),
                                                       conditionColumn.getOperator());
        } else if (type.equals(DataType.TYPE_NUMERIC_LONG)) {
            return new NumericLongConditionInspector(pattern,
                                                    conditionColumn.getFactField(),
                    getLongValue(),
                                                    conditionColumn.getOperator());
        } else if (type.equals(DataType.TYPE_NUMERIC_SHORT)) {
            return new NumericShortConditionInspector(pattern,
                                                     conditionColumn.getFactField(),
                    getShortValue(),
                                                     conditionColumn.getOperator());
        } else if (type.equals(DataType.TYPE_BOOLEAN)) {
            return new BooleanConditionInspector(pattern,
                                                conditionColumn.getFactField(),
                                                realCellValue.getBooleanValue(),
                                                conditionColumn.getOperator());
        } else if (type.equals(DataType.TYPE_DATE)) {
            return new DateConditionInspector(pattern,
                                             conditionColumn.getFactField(),
                                             realCellValue.getDateValue(),
                                             conditionColumn.getOperator());
        } else {
            return new UnrecognizedConditionInspector(pattern,
                                                     conditionColumn.getFactField(),
                                                     conditionColumn.getOperator());
        }
    }

    private Short getShortValue() {
        return (Short) realCellValue.getNumericValue();
    }

    private Long getLongValue() {
        if (realCellValue.getNumericValue() != null) {
            return (Long) realCellValue.getNumericValue();
        } else {
            return new Long(realCellValue.getStringValue());
        }
    }

    private Double getDoubleValue() {
        if (realCellValue.getNumericValue() != null) {
            return (Double) realCellValue.getNumericValue();
        } else {
            return new Double(realCellValue.getStringValue());
        }
    }

    private BigInteger getBigIntegerValue() {
        if (realCellValue.getNumericValue() != null) {
            return (BigInteger) realCellValue.getNumericValue();
        } else {
            return new BigInteger(realCellValue.getStringValue());
        }
    }

    private BigDecimal getBigDecimalValue() {
        if (realCellValue.getNumericValue() != null) {
            return (BigDecimal) realCellValue.getNumericValue();
        } else {
            return new BigDecimal(realCellValue.getStringValue());
        }
    }

    private Integer getIntegerValue() {
        if (realCellValue.getNumericValue() != null) {
            return (Integer) realCellValue.getNumericValue();
        } else {
            return new Integer(realCellValue.getStringValue());
        }
    }

    private boolean isTypeGuvnorEnum(ConditionCol52 conditionColumn) {
        return utils.getValueList(conditionColumn).length != 0;
    }

}
