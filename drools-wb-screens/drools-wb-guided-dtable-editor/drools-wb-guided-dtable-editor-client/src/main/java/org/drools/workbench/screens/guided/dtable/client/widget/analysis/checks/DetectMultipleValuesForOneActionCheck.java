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

package org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks;

import java.util.ArrayList;
import java.util.List;

import org.drools.workbench.screens.guided.dtable.client.resources.i18n.AnalysisConstants;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.PatternInspector;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.RuleInspector;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.action.ActionInspector;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.base.SingleCheck;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.reporting.Issue;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.reporting.Severity;

public class DetectMultipleValuesForOneActionCheck
        extends SingleCheck {

    private List<ActionInspector> conflictingObjects = new ArrayList<>();

    public DetectMultipleValuesForOneActionCheck( final RuleInspector ruleInspector ) {
        super( ruleInspector );
    }

    @Override
    public void check() {

        conflictingObjects.clear();

        for ( final PatternInspector patternInspector : ruleInspector.getPatternsInspector() ) {
            final ArrayList<ActionInspector> result = patternInspector.getActionsInspector().hasConflicts();
            if ( !result.isEmpty() ) {
                hasIssues = true;
                conflictingObjects.addAll( result );
                return;

            }
        }

        hasIssues = false;
    }

    @Override
    public Issue getIssue() {
        final Issue issue = new Issue( Severity.WARNING,
                                       AnalysisConstants.INSTANCE.MultipleValuesForOneAction(),
                                       ruleInspector.getRowIndex() + 1 );

        issue.getExplanation()
                .startNote()
                .addParagraph( AnalysisConstants.INSTANCE.MultipleValuesNote1P1( conflictingObjects.get( 0 ).toHumanReadableString(), conflictingObjects.get( 1 ).toHumanReadableString() ) )
                .end()
                .addParagraph( AnalysisConstants.INSTANCE.MultipleValuesP1() );

        return issue;
    }
}
