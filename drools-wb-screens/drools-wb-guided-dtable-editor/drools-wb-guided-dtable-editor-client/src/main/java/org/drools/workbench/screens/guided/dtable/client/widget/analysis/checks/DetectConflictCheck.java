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

package org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks;

import org.drools.workbench.screens.guided.dtable.client.resources.i18n.AnalysisConstants;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.RowInspector;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.base.PairCheck;

public class DetectConflictCheck
        extends PairCheck {

    public DetectConflictCheck(RowInspector rowInspector,
                               RowInspector other) {
        super(rowInspector, other);
    }

    @Override
    public void check() {
        if (other.getRowIndex() != rowInspector.getRowIndex()) {
            if (rowInspector.getConditions().isRedundant(other.getConditions())) {
                if (rowInspector.getActions().conflicts(other.getActions())) {
                    hasIssues = true;
                }
            }
        }
    }

    @Override
    public String getIssue() {
        return AnalysisConstants.INSTANCE.ConflictingMatchWithRow(other.getRowIndex() + 1);
    }
}
