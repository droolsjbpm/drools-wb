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

package org.drools.workbench.services.verifier.plugin.client;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.verifier.api.reporting.CheckType;
import org.drools.verifier.api.reporting.Severity;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.drools.workbench.services.verifier.plugin.client.testutil.TestUtil.assertContains;

@RunWith(GwtMockitoTestRunner.class)
public class DecisionTableAnalyzerSingleHitTest
        extends AnalyzerUpdateTestBase {

    @Test
    public void testSingleHit() throws Exception {
        analyzer = analyzerProvider.makeAnalyser()
                .withPersonAgeColumn(">")
                .withPersonApprovedActionSetField()
                .withData(DataBuilderProvider
                                  .row(0,
                                       null)
                                  .row(50,
                                       false)
                                  .end())
                .buildAnalyzer();

        fireUpAnalyzer();

        assertContains(analyzerProvider.getAnalysisReport(),
                       CheckType.SINGLE_HIT_LOST,
                       Severity.NOTE);
    }
}