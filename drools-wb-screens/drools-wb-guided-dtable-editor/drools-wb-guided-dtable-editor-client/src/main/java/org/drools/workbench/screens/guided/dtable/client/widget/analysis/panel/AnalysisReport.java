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

package org.drools.workbench.screens.guided.dtable.client.widget.analysis.panel;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.drools.workbench.screens.guided.dtable.client.widget.analysis.reporting.Issue;
import org.uberfire.mvp.PlaceRequest;

public class AnalysisReport {

    private final SortedSet<Issue> issues = new TreeSet<Issue>();
    private PlaceRequest place;

    public AnalysisReport( PlaceRequest place ) {
        this.place = place;
    }

    public void addIssue( final Issue issue ) {
        issues.add( issue );
    }

    public PlaceRequest getPlace() {
        return place;
    }

    public List<Issue> getAnalysisData() {
        return new ArrayList<Issue>( issues );
    }
}
