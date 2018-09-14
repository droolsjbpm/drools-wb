/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.services.verifier.webworker.client;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.verifier.api.reporting.CheckType;
import org.drools.verifier.api.reporting.Severity;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.services.verifier.webworker.client.testutil.AnalyzerConfigurationMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.drools.workbench.services.verifier.webworker.client.testutil.TestUtil.assertContains;

@RunWith(GwtMockitoTestRunner.class)
public class DecisionTableAnalyzerUniqueHitPolicyTest extends AnalyzerUpdateTestBase {

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        analyzerProvider.setConfiguration(new AnalyzerConfigurationMock(GuidedDecisionTable52.HitPolicy.UNIQUE_HIT));
    }

    @Test
    public void testUniqueHitPolicyRedundantRows() throws Exception, UpdateException {
        analyze("uniqueHitPolicyRedundantRows.gdst");

        assertContains(analyzerProvider.getAnalysisReport(),
                       CheckType.REDUNDANT_ROWS,
                       Severity.ERROR,
                       1,
                       2);
    }

    @Test
    public void testUniqueHitPolicySubsumptantRows() throws Exception, UpdateException {
        analyze("uniqueHitPolicySubsumptantRows.gdst");

        assertContains(analyzerProvider.getAnalysisReport(),
                       CheckType.SUBSUMPTANT_ROWS,
                       Severity.ERROR,
                       1,
                       2);
    }

    @Test
    public void testUniqueHitPolicyRedundantAndSubsumptantRows() throws Exception, UpdateException {
        analyze("uniqueHitPolicyRedundantAndSubsumptantRows.gdst");

        assertContains(analyzerProvider.getAnalysisReport(),
                       CheckType.SUBSUMPTANT_ROWS,
                       Severity.ERROR,
                       1,
                       2);

        assertContains(analyzerProvider.getAnalysisReport(),
                       CheckType.SUBSUMPTANT_ROWS,
                       Severity.ERROR,
                       2,
                       3);

        assertContains(analyzerProvider.getAnalysisReport(),
                       CheckType.REDUNDANT_ROWS,
                       Severity.ERROR,
                       1,
                       3);
    }
}