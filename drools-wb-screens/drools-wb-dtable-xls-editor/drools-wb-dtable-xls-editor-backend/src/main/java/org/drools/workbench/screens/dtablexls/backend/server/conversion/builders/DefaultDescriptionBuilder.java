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
package org.drools.workbench.screens.dtablexls.backend.server.conversion.builders;

import java.util.ArrayList;
import java.util.List;

import org.drools.decisiontable.parser.ActionType;
import org.drools.decisiontable.parser.ActionType.Code;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;

/**
 * Default description builder for when an explicit column has not been defined
 * in the XLS file. Descriptions are empty Strings.
 */
public class DefaultDescriptionBuilder
        implements
        GuidedDecisionTableSourceBuilderDirect {

    private List<DTCellValue52> values = new ArrayList<DTCellValue52>();

    @Override
    public Code getActionTypeCode() {
        return ActionType.Code.DESCRIPTION;
    }

    @Override
    public void populateDecisionTable( final GuidedDecisionTable52 dtable,
                                       final int maxRowCount ) {
        if ( this.values.size() < maxRowCount ) {
            for ( int iRow = this.values.size(); iRow < maxRowCount; iRow++ ) {
                this.values.add( new DTCellValue52( "" ) );
            }
        }

        for ( int iRow = 0; iRow < this.values.size(); iRow++ ) {
            dtable.getData().get( iRow ).add( 1,
                                              this.values.get( iRow ) );
        }
    }

    @Override
    public void addCellValue( final int row,
                              final int column,
                              final String value ) {
        String description = (value != null && !value.isEmpty()) ? value : "Created from row " + ( row + 1 ) ;
        this.values.add( new DTCellValue52( description ) );
    }

    @Override
    public void clearValues() {
        this.values.clear();
    }

    @Override
    public boolean hasValues() {
        return this.values.size() > 0;
    }

    @Override
    public String getResult() {
        throw new UnsupportedOperationException( "DefaultDescriptionBuilder does not return DRL." );
    }

    @Override
    public void addTemplate( final int row,
                             final int col,
                             final String content ) {
        throw new UnsupportedOperationException( "DefaultDescriptionBuilder does implement code snippets." );
    }

    @Override
    public int getRowCount() {
        return values.size();
    }

    @Override
    public int getColumn() {
        return 1;
    }
}
