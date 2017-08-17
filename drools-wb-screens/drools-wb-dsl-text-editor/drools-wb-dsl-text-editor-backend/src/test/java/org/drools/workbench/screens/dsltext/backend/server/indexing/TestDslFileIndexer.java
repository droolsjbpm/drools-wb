/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.dsltext.backend.server.indexing;

import java.util.HashMap;

import javax.enterprise.context.ApplicationScoped;

import org.appformer.project.datamodel.commons.oracle.ProjectDataModelOracleImpl;
import org.appformer.project.datamodel.oracle.DataType;
import org.appformer.project.datamodel.oracle.FieldAccessorsAndMutators;
import org.appformer.project.datamodel.oracle.ModelField;
import org.appformer.project.datamodel.oracle.ProjectDataModelOracle;
import org.drools.workbench.screens.dsltext.type.DSLResourceTypeDefinition;
import org.kie.workbench.common.services.refactoring.backend.server.TestIndexer;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.Path;

/**
 * Test indexer
 */
public class TestDslFileIndexer extends DslFileIndexer implements TestIndexer<DSLResourceTypeDefinition> {

    private final String packageName;

    public TestDslFileIndexer(final String packageName) {
        this.packageName=packageName;
    }

    @Override
    public void setIOService( final IOService ioService ) {
        this.ioService = ioService;
    }

    @Override
    public void setProjectService( final KieProjectService projectService ) {
        this.projectService = projectService;
    }

    @Override
    public void setResourceTypeDefinition( final DSLResourceTypeDefinition type ) {
        this.dslType = type;
    }

    @Override
    protected String getPackageName( final Path path ) {
        return packageName;
    }

    @Override
    protected ProjectDataModelOracle getProjectDataModelOracle( final Path path ) {
        final ProjectDataModelOracle dmo = new ProjectDataModelOracleImpl();
        dmo.addProjectModelFields( new HashMap<String, ModelField[]>() {{
            put( "org.drools.workbench.screens.dsltext.backend.server.indexing.classes.Applicant",
                 new ModelField[]{
                         new ModelField( "age",
                                         "java.lang.Integer",
                                         ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                         ModelField.FIELD_ORIGIN.DECLARED,
                                         FieldAccessorsAndMutators.ACCESSOR,
                                         DataType.TYPE_NUMERIC_INTEGER ) } );
            put( "org.drools.workbench.screens.dsltext.backend.server.indexing.classes.Mortgage",
                 new ModelField[]{
                         new ModelField( "amount",
                                         "java.lang.Integer",
                                         ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                         ModelField.FIELD_ORIGIN.DECLARED,
                                         FieldAccessorsAndMutators.ACCESSOR,
                                         DataType.TYPE_NUMERIC_INTEGER ),
                         new ModelField( "applicant",
                                         "org.drools.workbench.screens.dsltext.backend.server.indexing.classes.Applicant",
                                         ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                         ModelField.FIELD_ORIGIN.DECLARED,
                                         FieldAccessorsAndMutators.ACCESSOR,
                                         "org.drools.workbench.screens.dsltext.backend.server.indexing.classes.Applicant" ) } );
        }} );
        return dmo;
    }

}
