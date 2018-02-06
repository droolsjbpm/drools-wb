/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.dtree.backend.server.indexing;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.lucene.search.Query;
import org.drools.workbench.screens.guided.dtree.type.GuidedDTreeResourceTypeDefinition;
import org.junit.Test;
import org.kie.workbench.common.services.refactoring.backend.server.BaseIndexingTest;
import org.kie.workbench.common.services.refactoring.backend.server.TestIndexer;
import org.kie.workbench.common.services.refactoring.backend.server.query.builder.SingleTermQueryBuilder;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueReferenceIndexTerm;
import org.kie.workbench.common.services.refactoring.service.ResourceType;
import org.uberfire.ext.metadata.io.KObjectUtil;
import org.uberfire.java.nio.file.Path;

@NotThreadSafe
public class IndexRuleMultipleTypesTest extends BaseIndexingTest<GuidedDTreeResourceTypeDefinition> {

    @Test
    public void testIndexDrlRuleMultipleTypes() throws IOException, InterruptedException {
        //Add test files
        final Path path1 = basePath.resolve( "drl3.tdrl" );
        final String drl1 = loadText( "drl3.tdrl" );
        ioService().write( path1,
                           drl1 );
        final Path path2 = basePath.resolve( "drl4.tdrl" );
        final String drl2 = loadText( "drl4.tdrl" );
        ioService().write( path2,
                           drl2 );

        Thread.sleep( 5000 ); //wait for events to be consumed from jgit -> (notify changes -> watcher -> index) -> lucene index

        List<String> index = Arrays.asList(KObjectUtil.toKCluster(basePath.getFileSystem()).getClusterId());

        {
            final Query query = new SingleTermQueryBuilder( new ValueReferenceIndexTerm( "org.drools.workbench.screens.guided.dtree.backend.server.indexing.classes.Applicant", ResourceType.JAVA ) )
                    .build();
            searchFor(index,  query, 2, path1, path2);
        }

        {
            final Query query = new SingleTermQueryBuilder( new ValueReferenceIndexTerm( "org.drools.workbench.screens.guided.dtree.backend.server.indexing.classes.Mortgage", ResourceType.JAVA ) )
                    .build();
            searchFor(index,  query, 1, path2);
        }

    }

    @Override
    protected TestIndexer getIndexer() {
        return new TestGuidedDecisionTreeFileIndexer();
    }


    @Override
    protected GuidedDTreeResourceTypeDefinition getResourceTypeDefinition() {
        return new GuidedDTreeResourceTypeDefinition();
    }

    @Override
    protected String getRepositoryName() {
        return this.getClass().getSimpleName();
    }

}
