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

package org.drools.workbench.screens.guided.dtable.client.widget.analysis;

import java.util.Collection;

import org.drools.workbench.models.datamodel.imports.Import;
import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.datamodel.rule.BaseSingleFieldConstraint;
import org.drools.workbench.models.guided.dtable.shared.model.ActionInsertFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionSetFieldCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;

public class ExtendedGuidedDecisionTableBuilder
        extends AbstractDecisionTableBuilder {

    public ExtendedGuidedDecisionTableBuilder( final String packageName,
                                               final Collection<Import> imports,
                                               final String tableName ) {
        table.setPackageName( packageName );
        table.setTableFormat( GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY );
        table.getImports().getImports().addAll( imports );
        table.setTableName( tableName );
    }

    public ExtendedGuidedDecisionTableBuilder withNumericColumn( String boundName,
                                                                 String factType,
                                                                 String field,
                                                                 String operator ) {
        Pattern52 pattern = findPattern( boundName, factType );

        ConditionCol52 con1 = new ConditionCol52();
        con1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        con1.setFieldType( DataType.TYPE_NUMERIC );
        con1.setFactField( field );
        con1.setHeader( "Applicant age" );
        con1.setOperator( operator );
        pattern.getChildColumns().add( con1 );

        addPattern( pattern );

        return this;
    }

    public ExtendedGuidedDecisionTableBuilder withConditionIntegerColumn( String boundName,
                                                                          String factType,
                                                                          String field,
                                                                          String operator ) {
        Pattern52 pattern = findPattern( boundName, factType );

        ConditionCol52 con1 = new ConditionCol52();
        con1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        con1.setFieldType( DataType.TYPE_NUMERIC_INTEGER );
        con1.setFactField( field );
        con1.setHeader( "Applicant age" );
        con1.setOperator( operator );
        pattern.getChildColumns().add( con1 );

        addPattern( pattern );

        return this;
    }

    public ExtendedGuidedDecisionTableBuilder withStringColumn( String boundName,
                                                                String factType,
                                                                String field,
                                                                String operator ) {
        Pattern52 pattern = findPattern( boundName, factType );

        ConditionCol52 con1 = new ConditionCol52();
        con1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        con1.setFieldType( DataType.TYPE_STRING );
        con1.setFactField( field );
        con1.setHeader( "Applicant age" );
        con1.setOperator( operator );
        pattern.getChildColumns().add( con1 );

        addPattern( pattern );

        return this;
    }

    public ExtendedGuidedDecisionTableBuilder withEnumColumn( String boundName,
                                                              String factType,
                                                              String field,
                                                              String operator,
                                                              String valueList ) {
        Pattern52 pattern = findPattern( boundName, factType );

        ConditionCol52 con1 = new ConditionCol52();
        con1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_ENUM );
        con1.setValueList( valueList );
        con1.setFieldType( DataType.TYPE_STRING );
        con1.setFactField( field );
        con1.setHeader( "Applicant age" );
        con1.setOperator( operator );
        pattern.getChildColumns().add( con1 );

        addPattern( pattern );

        return this;
    }

    public ExtendedGuidedDecisionTableBuilder withActionSetField( String boundName,
                                                                  String factField,
                                                                  String typeNumericInteger ) {

        table.getActionCols().add( createActionSetField( boundName,
                                                         factField,
                                                         typeNumericInteger ) );

        return this;
    }

    public ExtendedGuidedDecisionTableBuilder withActionInsertFact( String factType,
                                                                    String boundName,
                                                                    String factField,
                                                                    String typeNumericInteger ) {

        table.getActionCols().add( createActionInsertFact( factType,
                                                           boundName,
                                                           factField,
                                                           typeNumericInteger ) );

        return this;
    }

    public static ActionSetFieldCol52 createActionSetField( String boundName,
                                                     String factField,
                                                     String typeNumericInteger ) {
        ActionSetFieldCol52 column = new ActionSetFieldCol52();
        column.setBoundName( boundName );
        column.setFactField( factField );
        column.setType( typeNumericInteger );
        return column;
    }

    public static ActionInsertFactCol52 createActionInsertFact( String factType,
                                                                String boundName,
                                                                String factField,
                                                                String type ) {
        ActionInsertFactCol52 column = new ActionInsertFactCol52();
        column.setFactType( factType );
        column.setBoundName( boundName );
        column.setFactField( factField );
        column.setType( type );
        return column;
    }

}
