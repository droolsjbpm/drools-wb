/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.dtable.client.widget.analysis;

import java.util.Collections;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.AnalysisConstants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.drools.workbench.screens.guided.dtable.client.widget.analysis.TestUtil.*;

@RunWith( GwtMockitoTestRunner.class )
public class DecisionTableAnalyzerSingleHitTest {

    @GwtMock
    AnalysisConstants analysisConstants;
    @GwtMock
    DateTimeFormat    dateTimeFormat;

    private AnalyzerProvider analyzerProvider;

    @Before
    public void setUp() throws Exception {
        analyzerProvider = new AnalyzerProvider();
    }

    @Test
    public void testSingleHit() throws Exception {
        final DecisionTableAnalyzer analyzer = analyzerProvider.makeAnalyser()
                                                               .withPersonAgeColumn( ">" )
                                                               .withPersonApprovedActionSetField()
                                                               .withData( DataBuilderProvider
                                                                                  .row( 0, null )
                                                                                  .row( 50, false )
                                                                                  .end() )
                                                               .buildAnalyzer();


        analyzer.analyze( Collections.emptyList() );
        assertContains( "SingleHitLost", analyzerProvider.getAnalysisReport() );
    }

}