/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.dtablexls.backend.server.conversion;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.appformer.project.datamodel.oracle.DataType;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;

/**
 * Utilities relating to the use of DTCellValue's
 */
public class DTCellValueUtilities {

    private static final String DATE_FORMAT = ApplicationPreferences.getDroolsDateFormat();

    private static final SimpleDateFormat FORMATTER = new SimpleDateFormat(DATE_FORMAT);

    /**
     * Callback to record data-type conversion errors.
     */
    public interface ConversionErrorCallback {

        /**
         * Called when a conversion error occurred.
         * @param value The value being converted.
         * @param dataType The target data-type to which the value is being converted.
         */
        void onConversionError(final String value,
                               final DataType.DataTypes dataType);
    }

    /**
     * The column-data type is looked up from the SuggestionCompletionEngine and
     * represents the *true* data-type that the column represents. The data-type
     * associated with the Cell Value can be incorrect for legacy models. For
     * pre-5.2 they will always be String and for pre-5.4 numerical fields are
     * always Numeric
     * @param type
     * @param dcv
     */
    public static void assertDTCellValue(final String type,
                                         final DTCellValue52 dcv,
                                         final ConversionErrorCallback callback) {
        if (dcv == null) {
            return;
        }

        //If already converted exit
        final DataType.DataTypes dataType = convertToTypeSafeType(type);
        if (dataType.equals(dcv.getDataType())) {
            return;
        }

        switch (dcv.getDataType()) {
            case NUMERIC:
                convertDTCellValueFromNumeric(dataType,
                                              dcv);
                break;
            default:
                convertDTCellValueFromString(dataType,
                                             dcv,
                                             callback);
        }
    }

    private static DataType.DataTypes convertToTypeSafeType(final String type) {
        if (type.equals(DataType.TYPE_NUMERIC)) {
            return DataType.DataTypes.NUMERIC;
        } else if (type.equals(DataType.TYPE_NUMERIC_BIGDECIMAL)) {
            return DataType.DataTypes.NUMERIC_BIGDECIMAL;
        } else if (type.equals(DataType.TYPE_NUMERIC_BIGINTEGER)) {
            return DataType.DataTypes.NUMERIC_BIGINTEGER;
        } else if (type.equals(DataType.TYPE_NUMERIC_BYTE)) {
            return DataType.DataTypes.NUMERIC_BYTE;
        } else if (type.equals(DataType.TYPE_NUMERIC_DOUBLE)) {
            return DataType.DataTypes.NUMERIC_DOUBLE;
        } else if (type.equals(DataType.TYPE_NUMERIC_FLOAT)) {
            return DataType.DataTypes.NUMERIC_FLOAT;
        } else if (type.equals(DataType.TYPE_NUMERIC_INTEGER)) {
            return DataType.DataTypes.NUMERIC_INTEGER;
        } else if (type.equals(DataType.TYPE_NUMERIC_LONG)) {
            return DataType.DataTypes.NUMERIC_LONG;
        } else if (type.equals(DataType.TYPE_NUMERIC_SHORT)) {
            return DataType.DataTypes.NUMERIC_SHORT;
        } else if (type.equals(DataType.TYPE_BOOLEAN)) {
            return DataType.DataTypes.BOOLEAN;
        } else if (type.equals(DataType.TYPE_DATE)) {
            return DataType.DataTypes.DATE;
        }
        return DataType.DataTypes.STRING;
    }

    //If the Decision Table model has been converted from the legacy text based
    //class then all values are held in the DTCellValue's StringValue. This
    //function attempts to set the correct DTCellValue property based on
    //the DTCellValue's data type.
    private static void convertDTCellValueFromString(final DataType.DataTypes dataType,
                                                     final DTCellValue52 dcv,
                                                     final ConversionErrorCallback callback) {
        String text = dcv.getStringValue();
        switch (dataType) {
            case BOOLEAN:
                dcv.setBooleanValue((text == null ? Boolean.FALSE : Boolean.valueOf(text)));
                break;
            case DATE:
                Date d = null;
                try {
                    if (!(text == null || text.isEmpty())) {
                        d = FORMATTER.parse(stripQuotes(text));
                    }
                } catch (ParseException e) {
                    callback.onConversionError(text,
                                               dataType);
                }
                dcv.setDateValue(d);
                break;
            case NUMERIC:
                BigDecimal numericValue = null;
                try {
                    if (!(text == null || text.isEmpty())) {
                        numericValue = new BigDecimal(stripTrailing(stripQuotes(text),
                                                                    "B"));
                    }
                } catch (Exception e) {
                    callback.onConversionError(text,
                                               dataType);
                }
                dcv.setNumericValue(numericValue);
                break;
            case NUMERIC_BIGDECIMAL:
                BigDecimal bigDecimalValue = null;
                try {
                    if (!(text == null || text.isEmpty())) {
                        bigDecimalValue = new BigDecimal(stripTrailing(stripQuotes(text),
                                                                       "B"));
                    }
                } catch (Exception e) {
                    callback.onConversionError(text,
                                               dataType);
                }
                dcv.setNumericValue(bigDecimalValue);
                break;
            case NUMERIC_BIGINTEGER:
                BigInteger bigIntegerValue = null;
                try {
                    if (!(text == null || text.isEmpty())) {
                        bigIntegerValue = new BigInteger(stripTrailing(stripQuotes(text),
                                                                       "I"));
                    }
                } catch (Exception e) {
                    callback.onConversionError(text,
                                               dataType);
                }
                dcv.setNumericValue(bigIntegerValue);
                break;
            case NUMERIC_BYTE:
                Byte byteValue = null;
                try {
                    if (!(text == null || text.isEmpty())) {
                        byteValue = Byte.valueOf(stripQuotes(text));
                    }
                } catch (Exception e) {
                    callback.onConversionError(text,
                                               dataType);
                }
                dcv.setNumericValue(byteValue);
                break;
            case NUMERIC_DOUBLE:
                Double doubleValue = null;
                try {
                    if (!(text == null || text.isEmpty())) {
                        doubleValue = Double.valueOf(stripQuotes(text));
                    }
                } catch (Exception e) {
                    callback.onConversionError(text,
                                               dataType);
                }
                dcv.setNumericValue(doubleValue);
                break;
            case NUMERIC_FLOAT:
                Float floatValue = null;
                try {
                    if (!(text == null || text.isEmpty())) {
                        floatValue = Float.valueOf(stripQuotes(text));
                    }
                } catch (Exception e) {
                    callback.onConversionError(text,
                                               dataType);
                }
                dcv.setNumericValue(floatValue);
                break;
            case NUMERIC_INTEGER:
                Integer integerValue = null;
                try {
                    if (!(text == null || text.isEmpty())) {
                        integerValue = Integer.valueOf(stripQuotes(text));
                    }
                } catch (Exception e) {
                    callback.onConversionError(text,
                                               dataType);
                }
                dcv.setNumericValue(integerValue);
                break;
            case NUMERIC_LONG:
                Long longValue = null;
                try {
                    if (!(text == null || text.isEmpty())) {
                        longValue = Long.valueOf(stripQuotes(text));
                    }
                } catch (Exception e) {
                    callback.onConversionError(text,
                                               dataType);
                }
                dcv.setNumericValue(longValue);
                break;
            case NUMERIC_SHORT:
                Short shortValue = null;
                try {
                    if (!(text == null || text.isEmpty())) {
                        shortValue = Short.valueOf(stripQuotes(text));
                    }
                } catch (Exception e) {
                    callback.onConversionError(text,
                                               dataType);
                }
                dcv.setNumericValue(shortValue);
                break;
        }
    }

    static String stripQuotes(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        if (text.startsWith("\"")) {
            text = text.substring(1);
        }
        if (text.endsWith("\"")) {
            text = text.substring(0,
                                  text.length() - 1);
        }
        return text;
    }

    static String stripTrailing(String text,
                                String suffix) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        if (text.endsWith(suffix)) {
            return text.substring(0,
                                  text.length() - 1);
        }
        return text;
    }

    //If the Decision Table model was pre-5.4 Numeric data-types were always stored as 
    //BigDecimals. This function attempts to set the correct DTCellValue property based 
    //on the *true* data type.
    private static void convertDTCellValueFromNumeric(final DataType.DataTypes dataType,
                                                      final DTCellValue52 dcv) {
        //Generic type NUMERIC was always stored as a BigDecimal
        final BigDecimal value = (BigDecimal) dcv.getNumericValue();
        switch (dataType) {
            case NUMERIC_BIGDECIMAL:
                dcv.setNumericValue(value == null ? null : value);
                break;
            case NUMERIC_BIGINTEGER:
                dcv.setNumericValue(value == null ? null : value.toBigInteger());
                break;
            case NUMERIC_BYTE:
                dcv.setNumericValue(value == null ? null : value.byteValue());
                break;
            case NUMERIC_DOUBLE:
                dcv.setNumericValue(value == null ? null : value.doubleValue());
                break;
            case NUMERIC_FLOAT:
                dcv.setNumericValue(value == null ? null : value.floatValue());
                break;
            case NUMERIC_INTEGER:
                dcv.setNumericValue(value == null ? null : value.intValue());
                break;
            case NUMERIC_LONG:
                dcv.setNumericValue(value == null ? null : value.longValue());
                break;
            case NUMERIC_SHORT:
                dcv.setNumericValue(value == null ? null : value.shortValue());
                break;
        }
    }
}
