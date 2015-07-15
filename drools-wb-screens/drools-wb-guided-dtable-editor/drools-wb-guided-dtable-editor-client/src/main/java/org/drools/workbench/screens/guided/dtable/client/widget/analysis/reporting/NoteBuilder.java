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

package org.drools.workbench.screens.guided.dtable.client.widget.analysis.reporting;

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import org.drools.workbench.screens.guided.dtable.client.resources.GuidedDecisionTableResources;

public class NoteBuilder<T> {

    private final SafeHtmlBuilder htmlBuilder;
    private final T parent;

    public NoteBuilder( final SafeHtmlBuilder htmlBuilder,
                        final T parent ) {
        this.htmlBuilder = htmlBuilder;
        this.parent = parent;
        htmlBuilder.appendHtmlConstant( "<div class='" + GuidedDecisionTableResources.INSTANCE.analysisCss().note() + "'>" );
    }

    public TableBuilder<NoteBuilder<T>> startExampleTable() {
        return new TableBuilder<NoteBuilder<T>>( htmlBuilder,
                                                 this );
    }

    public NoteBuilder<T> addParagraph( final String text ) {
        Util.addParagraph( htmlBuilder,
                           text );
        return this;
    }

    public T end() {
        htmlBuilder.appendHtmlConstant( "</div>" );
        return parent;
    }

}