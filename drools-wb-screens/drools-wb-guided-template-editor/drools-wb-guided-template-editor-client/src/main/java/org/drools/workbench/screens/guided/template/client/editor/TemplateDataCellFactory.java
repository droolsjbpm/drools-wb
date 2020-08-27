/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.guided.template.client.editor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import com.google.gwt.event.shared.EventBus;
import org.drools.workbench.models.guided.template.shared.TemplateModel;
import org.kie.soup.project.datamodel.oracle.DataType;
import org.kie.soup.project.datamodel.oracle.OperatorsOracle;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.AbstractCellFactory;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.DecoratedGridCellValueAdaptor;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.cells.AbstractProxyPopupDropDownEditCell;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.cells.ProxyPopupDateDropDownEditCell;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.cells.ProxyPopupNumericBigDecimalDropDownEditCell;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.cells.ProxyPopupNumericBigIntegerDropDownEditCell;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.cells.ProxyPopupNumericByteDropDownEditCell;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.cells.ProxyPopupNumericDoubleDropDownEditCell;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.cells.ProxyPopupNumericFloatDropDownEditCell;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.cells.ProxyPopupNumericIntegerDropDownEditCell;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.cells.ProxyPopupNumericLongDropDownEditCell;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.cells.ProxyPopupNumericShortDropDownEditCell;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.cells.ProxyPopupTextDropDownEditCell;

public class TemplateDataCellFactory
        extends AbstractCellFactory<TemplateDataColumn> {

    /**
     * Construct a Cell Factory for a specific Template Data Widget
     * @param oracle DataModelOracle to assist with drop-downs
     * @param dropDownManager DropDownManager for dependent cells
     * @param isReadOnly Should cells be created for a read-only mode of operation
     * @param eventBus EventBus to which cells can send update events
     */
    public TemplateDataCellFactory(AsyncPackageDataModelOracle oracle,
                                   TemplateDropDownManager dropDownManager,
                                   boolean isReadOnly,
                                   EventBus eventBus) {
        super(oracle,
              dropDownManager,
              isReadOnly,
              eventBus);
    }

    /**
     * Create a Cell for the given TemplateDataColumn
     * @param column The Template Data Table model column
     * @return A Cell
     */
    public DecoratedGridCellValueAdaptor<? extends Comparable<?>> getCell(TemplateDataColumn column) {

        if (column.getDataType().equals(TemplateModel.DEFAULT_TYPE)) {
            return makeTextCellWrapper();
        }

        //Check if the column has an enumeration
        final String dataType = column.getDataType();
        if (oracle.hasEnums(column.getFactType(),
                            column.getFactField())) {

            return makeSelectionEnumCell(column.getFactType(),
                                         column.getFactField(),
                                         column.getOperator(),
                                         dataType);
        } else if (OperatorsOracle.operatorRequiresList(column.getOperator())) {
            // " " and "," needed to list multiple values
            return makeTextCellWrapper();
        } else {
            if (column.getDataType().equals(DataType.TYPE_BOOLEAN)) {
                return makeBooleanCell();
            } else if (DataType.isDate(column.getDataType())) {
                return makeUniversalDateCell();
            } else if (dataType.equals(DataType.TYPE_NUMERIC)) {
                return makeNumericCell();
            } else if (dataType.equals(DataType.TYPE_NUMERIC_BIGDECIMAL)) {
                return makeNumericBigDecimalCell();
            } else if (dataType.equals(DataType.TYPE_NUMERIC_BIGINTEGER)) {
                return makeNumericBigIntegerCell();
            } else if (dataType.equals(DataType.TYPE_NUMERIC_BYTE)) {
                return makeNumericByteCell();
            } else if (dataType.equals(DataType.TYPE_NUMERIC_DOUBLE)) {
                return makeNumericDoubleCell();
            } else if (dataType.equals(DataType.TYPE_NUMERIC_FLOAT)) {
                return makeNumericFloatCell();
            } else if (dataType.equals(DataType.TYPE_NUMERIC_INTEGER)) {
                return makeNumericIntegerCell();
            } else if (dataType.equals(DataType.TYPE_NUMERIC_LONG)) {
                return makeNumericLongCell();
            } else if (dataType.equals(DataType.TYPE_NUMERIC_SHORT)) {
                return makeNumericShortCell();
            } else {
                return makeTextCellWrapper();
            }
        }
    }

    // Get a cell for a Value List
    // Package private to be testable
    DecoratedGridCellValueAdaptor<? extends Comparable<?>> makeSelectionEnumCell(String factType,
                                                                                 String fieldName,
                                                                                 String operator,
                                                                                 String dataType) {
        final boolean isSingleValueOperator = !OperatorsOracle.operatorRequiresList(operator);

        if (DataType.isNumeric(dataType) && isSingleValueOperator) {
            return makeNumericSelectionEnumCell(factType,
                                         fieldName,
                                         operator,
                                         dataType);
        } else if (dataType.equals(DataType.TYPE_BOOLEAN) && isSingleValueOperator) {
            return makeBooleanCell();
        } else if (dataType.equals(DataType.TYPE_DATE) && isSingleValueOperator) {
            final AbstractProxyPopupDropDownEditCell<Date, Date> pudd = new ProxyPopupDateDropDownEditCell(factType,
                                                                                                           fieldName,
                                                                                                           operator,
                                                                                                           oracle,
                                                                                                           dropDownManager,
                                                                                                           isReadOnly,
                                                                                                           DATE_FORMAT);
            return decoratedGridCellValueAdaptor(pudd);
        } else {
            final AbstractProxyPopupDropDownEditCell<String, String> pudd = new ProxyPopupTextDropDownEditCell(factType,
                                                                                                               fieldName,
                                                                                                               operator,
                                                                                                               oracle,
                                                                                                               dropDownManager,
                                                                                                               isReadOnly);
            return decoratedGridCellValueAdaptor(pudd);
        }
    }

    DecoratedGridCellValueAdaptor<? extends Comparable<?>> makeNumericSelectionEnumCell(String factType,
                                                                                        String fieldName,
                                                                                        String operator,
                                                                                        String dataType) {
        if (dataType.equals(DataType.TYPE_NUMERIC)) {
            final AbstractProxyPopupDropDownEditCell<BigDecimal, BigDecimal> pudd = new ProxyPopupNumericBigDecimalDropDownEditCell(factType,
                                                                                                                                    fieldName,
                                                                                                                                    operator,
                                                                                                                                    oracle,
                                                                                                                                    dropDownManager,
                                                                                                                                    isReadOnly);
            return decoratedGridCellValueAdaptor(pudd);
        } else if (dataType.equals(DataType.TYPE_NUMERIC_BIGDECIMAL)) {
            final AbstractProxyPopupDropDownEditCell<BigDecimal, BigDecimal> pudd = new ProxyPopupNumericBigDecimalDropDownEditCell(factType,
                                                                                                                                    fieldName,
                                                                                                                                    operator,
                                                                                                                                    oracle,
                                                                                                                                    dropDownManager,
                                                                                                                                    isReadOnly);
            return decoratedGridCellValueAdaptor(pudd);
        } else if (dataType.equals(DataType.TYPE_NUMERIC_BIGINTEGER)) {
            final AbstractProxyPopupDropDownEditCell<BigInteger, BigInteger> pudd = new ProxyPopupNumericBigIntegerDropDownEditCell(factType,
                                                                                                                                    fieldName,
                                                                                                                                    operator,
                                                                                                                                    oracle,
                                                                                                                                    dropDownManager,
                                                                                                                                    isReadOnly);
            return decoratedGridCellValueAdaptor(pudd);
        } else if (dataType.equals(DataType.TYPE_NUMERIC_BYTE)) {
            final AbstractProxyPopupDropDownEditCell<Byte, Byte> pudd = new ProxyPopupNumericByteDropDownEditCell(factType,
                                                                                                                  fieldName,
                                                                                                                  operator,
                                                                                                                  oracle,
                                                                                                                  dropDownManager,
                                                                                                                  isReadOnly);
            return decoratedGridCellValueAdaptor(pudd);
        } else if (dataType.equals(DataType.TYPE_NUMERIC_DOUBLE)) {
            final AbstractProxyPopupDropDownEditCell<Double, Double> pudd = new ProxyPopupNumericDoubleDropDownEditCell(factType,
                                                                                                                        fieldName,
                                                                                                                        operator,
                                                                                                                        oracle,
                                                                                                                        dropDownManager,
                                                                                                                        isReadOnly);
            return decoratedGridCellValueAdaptor(pudd);
        } else if (dataType.equals(DataType.TYPE_NUMERIC_FLOAT)) {
            final AbstractProxyPopupDropDownEditCell<Float, Float> pudd = new ProxyPopupNumericFloatDropDownEditCell(factType,
                                                                                                                     fieldName,
                                                                                                                     operator,
                                                                                                                     oracle,
                                                                                                                     dropDownManager,
                                                                                                                     isReadOnly);
            return decoratedGridCellValueAdaptor(pudd);
        } else if (dataType.equals(DataType.TYPE_NUMERIC_INTEGER)) {
            final AbstractProxyPopupDropDownEditCell<Integer, Integer> pudd = new ProxyPopupNumericIntegerDropDownEditCell(factType,
                                                                                                                           fieldName,
                                                                                                                           operator,
                                                                                                                           oracle,
                                                                                                                           dropDownManager,
                                                                                                                           isReadOnly);
            return decoratedGridCellValueAdaptor(pudd);
        } else if (dataType.equals(DataType.TYPE_NUMERIC_LONG)) {
            final AbstractProxyPopupDropDownEditCell<Long, Long> pudd = new ProxyPopupNumericLongDropDownEditCell(factType,
                                                                                                                  fieldName,
                                                                                                                  operator,
                                                                                                                  oracle,
                                                                                                                  dropDownManager,
                                                                                                                  isReadOnly);
            return decoratedGridCellValueAdaptor(pudd);
        } else if (dataType.equals(DataType.TYPE_NUMERIC_SHORT)) {
            final AbstractProxyPopupDropDownEditCell<Short, Short> pudd = new ProxyPopupNumericShortDropDownEditCell(factType,
                                                                                                                     fieldName,
                                                                                                                     operator,
                                                                                                                     oracle,
                                                                                                                     dropDownManager,
                                                                                                                     isReadOnly);
            return decoratedGridCellValueAdaptor(pudd);
        } else {
            throw new IllegalStateException("Unknown numeric data type: " + dataType);
        }
    }

    /**
     * Created for testing purpose. Allows to check if data type was taken into account by cell factory.
     */
    DecoratedGridCellValueAdaptor decoratedGridCellValueAdaptor(final AbstractProxyPopupDropDownEditCell pudd) {
        return new DecoratedGridCellValueAdaptor<>(pudd,
                                                   eventBus);
    }

    /**
     * Wrapper method due to a test purpose
     */
    protected DecoratedGridCellValueAdaptor<String> makeTextCellWrapper() {
        return makeTextCell();
    }
}
