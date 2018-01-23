/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.testscenario.client.page.audit;

import java.util.Arrays;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.models.testscenarios.shared.ExecutionTrace;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class FiredRulesTableTest {

    @Mock
    private ExecutionTrace trace;

    private FiredRulesTable table;

    @Before
    public void setUp() throws Exception {
        table = spy(new FiredRulesTable());
    }

    @Test
    public void testRedrawFiredRules() throws Exception {
        final String[] firedRules = new String[]{"rule1", "rule2"};

        doReturn(firedRules).when(trace).getRulesFired();
        doReturn(2l).when(trace).getNumberOfRulesFired();
        doReturn(10l).when(trace).getExecutionTimeResult();

        table.redrawFiredRules(trace);
        verify(table).setTitle("property0RulesFiredIn1Ms(2, 10)");
        verify(table).setRowData(Arrays.asList(firedRules));
    }
}
