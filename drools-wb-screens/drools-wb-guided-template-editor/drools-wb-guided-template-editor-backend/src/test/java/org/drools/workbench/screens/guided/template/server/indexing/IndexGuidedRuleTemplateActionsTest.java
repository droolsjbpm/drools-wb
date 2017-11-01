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

package org.drools.workbench.screens.guided.template.server.indexing;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.lucene.search.Query;
import org.drools.workbench.models.guided.template.backend.RuleTemplateModelXMLPersistenceImpl;
import org.drools.workbench.models.guided.template.shared.TemplateModel;
import org.drools.workbench.screens.guided.template.type.GuidedRuleTemplateResourceTypeDefinition;
import org.junit.Test;
import org.kie.soup.project.datamodel.imports.Import;
import org.kie.workbench.common.services.refactoring.backend.server.BaseIndexingTest;
import org.kie.workbench.common.services.refactoring.backend.server.TestIndexer;
import org.kie.workbench.common.services.refactoring.backend.server.query.builder.SingleTermQueryBuilder;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueReferenceIndexTerm;
import org.kie.workbench.common.services.refactoring.service.ResourceType;
import org.uberfire.ext.metadata.engine.Index;
import org.uberfire.java.nio.file.Path;

public class IndexGuidedRuleTemplateActionsTest extends BaseIndexingTest<GuidedRuleTemplateResourceTypeDefinition> {

    @Test
    public void testIndexGuidedRuleTemplateActions() throws IOException, InterruptedException {
        //Add test files
        final Path path = basePath.resolve("template1.template");
        final TemplateModel model = GuidedRuleTemplateFactory.makeModelWithActions("org.drools.workbench.screens.guided.template.server.indexing",
                                                                                   new ArrayList<Import>() {{
                                                                                       add(new Import("org.drools.workbench.screens.guided.template.server.indexing.classes.Applicant"));
                                                                                       add(new Import("org.drools.workbench.screens.guided.template.server.indexing.classes.Mortgage"));
                                                                                   }},
                                                                                   "template1");
        final String xml = RuleTemplateModelXMLPersistenceImpl.getInstance().marshal(model);
        ioService().write(path,
                          xml);

        Thread.sleep(5000); //wait for events to be consumed from jgit -> (notify changes -> watcher -> index) -> lucene index

        final Index index = getConfig().getIndexManager().get(org.uberfire.ext.metadata.io.KObjectUtil.toKCluster(basePath.getFileSystem()));

        {
            final Query query = new SingleTermQueryBuilder(new ValueReferenceIndexTerm("org.drools.workbench.screens.guided.template.server.indexing.classes.Applicant", ResourceType.JAVA))
                    .build();
            searchFor(index, query, 1, path);
        }

        {
            final Query query = new SingleTermQueryBuilder(new ValueReferenceIndexTerm("org.drools.workbench.screens.guided.template.server.indexing.classes.Mortgage", ResourceType.JAVA))
                    .build();
            searchFor(index, query, 1, path);
        }
    }

    @Override
    protected TestIndexer getIndexer() {
        return new TestGuidedRuleTemplateFileIndexer();
    }

    @Override
    protected GuidedRuleTemplateResourceTypeDefinition getResourceTypeDefinition() {
        return new GuidedRuleTemplateResourceTypeDefinition();
    }

    @Override
    protected String getRepositoryName() {
        return this.getClass().getSimpleName();
    }
}
