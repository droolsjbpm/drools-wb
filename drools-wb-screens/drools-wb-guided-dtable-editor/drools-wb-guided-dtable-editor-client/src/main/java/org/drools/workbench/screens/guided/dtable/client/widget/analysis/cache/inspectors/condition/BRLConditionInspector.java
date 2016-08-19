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

package org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.inspectors.condition;

import java.util.Iterator;

import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.BRLCondition;

public class BRLConditionInspector
        extends ConditionInspector {

    public BRLConditionInspector( final BRLCondition brlCondition ) {
        super( brlCondition );
    }

    @Override
    public String toHumanReadableString() {
        final StringBuilder builder = new StringBuilder();

        final Iterator iterator = getValues().iterator();

        while ( iterator.hasNext() ) {
            builder.append( iterator.next().toString() );
            if ( iterator.hasNext() ) {
                builder.append( ", " );
            }
        }

        return builder.toString();
    }

    @Override
    public boolean conflicts( final Object other ) {
        if ( other instanceof BRLConditionInspector ) {
            return !getValues().containsAll( (( BRLConditionInspector ) other).getValues() );
        } else {
            return false;
        }
    }

    @Override
    public boolean overlaps( final Object other ) {
        if ( other instanceof BRLConditionInspector ) {
            return (( BRLConditionInspector ) other).getValues().containsAny( getValues() );
        } else {
            return false;
        }
    }

    @Override
    public boolean subsumes( final Object other ) {
        if ( other instanceof BRLConditionInspector ) {
            return (( BRLConditionInspector ) other).getValues().containsAll( getValues() );
        } else {
            return false;
        }
    }
}
