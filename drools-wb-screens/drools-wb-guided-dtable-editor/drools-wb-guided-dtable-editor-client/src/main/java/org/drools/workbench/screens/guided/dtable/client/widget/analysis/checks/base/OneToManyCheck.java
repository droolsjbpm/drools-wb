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

package org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.base;

import java.util.Collection;

import org.drools.workbench.screens.guided.dtable.client.widget.analysis.RowInspector;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.RowInspectorCache;

public abstract class OneToManyCheck
        extends SingleCheck {

    private RowInspectorCache.Filter filter;

    public OneToManyCheck( RowInspector rowInspector,
                           RowInspectorCache.Filter filter ) {
        super( rowInspector );
        this.filter = filter;
    }

    public OneToManyCheck( RowInspector rowInspector ) {
        super( rowInspector );
    }

    protected boolean thereIsAtLestOneRow() {
        return getOtherRows().size() >= 1;
    }

    public RowInspector getRowInspector() {
        return rowInspector;
    }

    public Collection<RowInspector> getOtherRows() {
        return rowInspector.getCache().all( filter );
    }
}
