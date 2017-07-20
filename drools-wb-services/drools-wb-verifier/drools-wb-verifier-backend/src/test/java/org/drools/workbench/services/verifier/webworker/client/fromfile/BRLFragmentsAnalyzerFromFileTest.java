/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.services.verifier.webworker.client.fromfile;

import java.util.Set;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.models.guided.dtable.backend.GuidedDTXMLPersistence;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.services.verifier.api.client.reporting.Issue;
import org.drools.workbench.services.verifier.core.main.Analyzer;
import org.drools.workbench.services.verifier.webworker.client.AnalyzerUpdateTestBase;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.drools.workbench.services.verifier.webworker.client.testutil.TestUtil.assertDoesNotContain;
import static org.drools.workbench.services.verifier.webworker.client.testutil.TestUtil.loadResource;

@RunWith(GwtMockitoTestRunner.class)
public class BRLFragmentsAnalyzerFromFileTest
        extends AnalyzerUpdateTestBase {

    @Test
    public void testRuleTableGDST() throws
            Exception {
        final String xml = loadResource("RuleTable.gdst");

        final GuidedDecisionTable52 table52 = GuidedDTXMLPersistence.getInstance()
                .unmarshal(xml);

        final Analyzer analyzer = analyzerProvider.makeAnalyser(table52);

        analyzer.resetChecks();
        analyzer.analyze();

        final Set<Issue> analysisReport = analyzerProvider.getAnalysisReport();

        for (final Issue issue : analysisReport) {
            System.out.println(issue.getCheckType());
            System.out.println(issue.getDebugMessage());
        }

        assertDoesNotContain(CONFLICTING_ROWS,
                             analysisReport);
        assertDoesNotContain(SINGLE_HIT_LOST,
                             analysisReport);
    }
}