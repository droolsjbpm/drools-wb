/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

import org.drools.workbench.screens.guided.dtable.client.widget.analysis.RowInspector;

public abstract class SingleCheck
        extends CheckBase
        implements Comparable<SingleCheck> {

    protected final RowInspector rowInspector;

    public SingleCheck( final RowInspector rowInspector ) {
        this.rowInspector = rowInspector;
    }

    public RowInspector getRowInspector() {
        return rowInspector;
    }

    @Override
    public int compareTo( final SingleCheck singleCheck ) {
        return rowInspector.getRowIndex() - singleCheck.getRowInspector().getRowIndex();
    }
}
