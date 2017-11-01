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

package org.drools.workbench.screens.guided.rule.backend.server.indexing;

import java.io.IOException;

import org.apache.lucene.search.Query;
import org.drools.workbench.screens.guided.rule.type.GuidedRuleDRLResourceTypeDefinition;
import org.junit.Test;
import org.kie.workbench.common.services.refactoring.backend.server.BaseIndexingTest;
import org.kie.workbench.common.services.refactoring.backend.server.TestIndexer;
import org.kie.workbench.common.services.refactoring.backend.server.query.builder.SingleTermQueryBuilder;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueIndexTerm.TermSearchType;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueReferenceIndexTerm;
import org.kie.workbench.common.services.refactoring.service.ResourceType;
import org.uberfire.ext.metadata.engine.Index;
import org.uberfire.ext.metadata.io.KObjectUtil;
import org.uberfire.java.nio.file.Path;

public class IndexRuleTypeTest extends BaseIndexingTest<GuidedRuleDRLResourceTypeDefinition> {

    @Test
    public void testIndexRuleTypes() throws IOException, InterruptedException {
        //Add test files
        final Path path1 = basePath.resolve( "drl1.rdrl" );
        final String drl1 = loadText( "drl1.rdrl" );
        ioService().write( path1,
                           drl1 );
        final Path path2 = basePath.resolve( "drl2.rdrl" );
        final String drl2 = loadText( "drl2.rdrl" );
        ioService().write( path2,
                           drl2 );

        Thread.sleep( 5000 ); //wait for events to be consumed from jgit -> (notify changes -> watcher -> index) -> lucene index

        final Index index = getConfig().getIndexManager().get( KObjectUtil.toKCluster( basePath.getFileSystem() ) );

        {
            final Query query = new SingleTermQueryBuilder( new ValueReferenceIndexTerm( "org.drools.workbench.screens.guided.rule.backend.server.indexing.classes.Applicant", ResourceType.JAVA ) )
                    .build();
            searchFor(index, query, 2 );
        }

        {
            final Query query = new SingleTermQueryBuilder( new ValueReferenceIndexTerm( "*.Applicant", ResourceType.JAVA, TermSearchType.WILDCARD ) )
                    .build();
            searchFor(index, query, 2 );
        }

    }

    @Override
    protected TestIndexer getIndexer() {
        return new TestGuidedRuleDrlFileIndexer();
    }

    @Override
    protected GuidedRuleDRLResourceTypeDefinition getResourceTypeDefinition() {
        return new GuidedRuleDRLResourceTypeDefinition();
    }

    @Override
    protected String getRepositoryName() {
        return this.getClass().getSimpleName();
    }

}
