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

package org.drools.workbench.services.verifier.webworker.client.testutil.cache.util;

import java.util.ArrayList;
import java.util.Set;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.services.verifier.core.main.Analyzer;
import org.drools.workbench.services.verifier.plugin.client.Coordinate;
import org.drools.workbench.services.verifier.webworker.client.DTableUpdateManager;
import org.drools.workbench.services.verifier.webworker.client.DataBuilderProvider;
import org.drools.workbench.services.verifier.webworker.client.UpdateException;
import org.drools.workbench.services.verifier.webworker.client.testutil.AnalyzerProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.anySet;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class DTableUpdateManagerTest {


    private DTableUpdateManager updateManager;
    private GuidedDecisionTable52 table52;
    private AnalyzerProvider analyzerProvider;

    @Mock
    private Analyzer analyzer;

    @Before
    public void setUp() throws
                        Exception {
        analyzerProvider = new AnalyzerProvider();

        table52 = analyzerProvider.makeAnalyser()
                .withPersonAgeColumn( "==" )
                .withPersonApprovedActionSetField()
                .withData( DataBuilderProvider
                                   .row( 1,
                                         true )
                                   .row( null,
                                         null )
                                   .end() )
                .buildTable();

        updateManager = analyzerProvider.getUpdateManager( table52,
                                                           analyzer );
    }

    @Test
    public void testDoNotUpdateWhenDescriptionChanges() throws
                                                        Exception,
                                                        UpdateException {
        ArrayList<Coordinate> coordinates = new ArrayList<Coordinate>();
        coordinates.add( new Coordinate( 1,
                                         1 ) );

        updateManager.update( table52,
                              coordinates );

        verify( analyzer,
                never() ).update( any( Set.class ) );
    }

    @Test
    public void testDoNotUpdateConditionWhenValueDidNotChange() throws
                                                                Exception,
                                                                UpdateException {
        ArrayList<Coordinate> coordinates = new ArrayList<Coordinate>();
        coordinates.add( new Coordinate( 1,
                                         2 ) );

        updateManager.update( table52,
                              coordinates );

        verify( analyzer,
                never() ).update( anySet() );
    }

    @Test
    public void testDoNotUpdateActionWhenValueDidNotChange() throws
                                                             Exception,
                                                             UpdateException {
        ArrayList<Coordinate> coordinates = new ArrayList<Coordinate>();
        coordinates.add( new Coordinate( 0,
                                         3 ) );

        updateManager.update( table52,
                              coordinates );

        verify( analyzer,
                never() ).update( anySet() );
    }

    @Test
    public void testSetIntegerConditionToNewInteger() throws
                                                      Exception,
                                                      UpdateException {
        ArrayList<Coordinate> coordinates = new ArrayList<Coordinate>();
        Coordinate coordinate = new Coordinate( 0,
                                                2 );
        coordinates.add( coordinate );
        table52.getData()
                .get( 0 )
                .get( 2 )
                .setNumericValue( 123 );

        updateManager.update( table52,
                              coordinates );

        verify( analyzer ).update( anySet() );
    }

    @Test
    public void testSetBooleanActionToNewBoolean() throws
                                                   Exception,
                                                   UpdateException {
        final ArrayList<Coordinate> coordinates = new ArrayList<Coordinate>();
        final Coordinate coordinate = new Coordinate( 0,
                                                      3 );
        coordinates.add( coordinate );
        table52.getData()
                .get( 0 )
                .get( 3 )
                .setBooleanValue( false );

        updateManager.update( table52,
                              coordinates );

        verify( analyzer ).update( anySet() );
    }

    @Test
    public void testSetActionToNull() throws
                                      Exception,
                                      UpdateException {
        ArrayList<Coordinate> coordinates = new ArrayList<Coordinate>();
        Coordinate coordinate = new Coordinate( 0,
                                                3 );
        coordinates.add( coordinate );
        table52.getData()
                .get( 0 )
                .get( 3 )
                .setBooleanValue( null );

        updateManager.update( table52,
                              coordinates );

        verify( analyzer ).update( anySet() );
    }

    @Test
    public void testSetConditionToNull() throws
                                         Exception,
                                         UpdateException {
        ArrayList<Coordinate> coordinates = new ArrayList<Coordinate>();
        Coordinate coordinate = new Coordinate( 0,
                                                2 );
        coordinates.add( coordinate );
        table52.getData()
                .get( 0 )
                .get( 2 )
                .setNumericValue( (Integer) null );

        updateManager.update( table52,
                              coordinates );

        verify( analyzer ).update( anySet() );
    }

    @Test
    public void testFillNullCondition() throws
                                        Exception,
                                        UpdateException {
        ArrayList<Coordinate> coordinates = new ArrayList<Coordinate>();
        Coordinate coordinate = new Coordinate( 1,
                                                2 );
        coordinates.add( coordinate );
        table52.getData()
                .get( 1 )
                .get( 2 )
                .setNumericValue( 123 );

        updateManager.update( table52,
                              coordinates );

        verify( analyzer ).update( anySet() );
    }

    @Test
    public void testFillNullAction() throws
                                     Exception,
                                     UpdateException {
        ArrayList<Coordinate> coordinates = new ArrayList<Coordinate>();
        Coordinate coordinate = new Coordinate( 1,
                                                3 );
        coordinates.add( coordinate );
        table52.getData()
                .get( 1 )
                .get( 3 )
                .setBooleanValue( false );

        updateManager.update( table52,
                              coordinates );

        verify( analyzer ).update( anySet() );
    }

    @Test
    public void testFillNullActionWithNull() throws
                                             Exception,
                                             UpdateException {
        ArrayList<Coordinate> coordinates = new ArrayList<Coordinate>();
        Coordinate coordinate = new Coordinate( 1,
                                                3 );
        coordinates.add( coordinate );
        table52.getData()
                .get( 1 )
                .get( 3 )
                .setBooleanValue( null );

        updateManager.update( table52,
                              coordinates );

        verify( analyzer,
                never() ).update( anySet() );
    }

    @Test
    public void testFillNullConditionWithNull() throws
                                                Exception,
                                                UpdateException {
        ArrayList<Coordinate> coordinates = new ArrayList<Coordinate>();
        Coordinate coordinate = new Coordinate( 1,
                                                2 );
        coordinates.add( coordinate );
        table52.getData()
                .get( 1 )
                .get( 2 )
                .setNumericValue( (Integer) null );

        updateManager.update( table52,
                              coordinates );

        verify( analyzer,
                never() ).update( anySet() );
    }
}