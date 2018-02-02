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

package org.drools.workbench.screens.guided.dtable.backend.server;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.drools.workbench.screens.guided.dtable.service.GuidedDecisionTableEditorService;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.guvnor.test.CDITestSetup;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;

public class GuidedDecisionTableEditorServiceImplCDITest extends CDITestSetup {

    private GuidedDecisionTableEditorService testedService;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();

        testedService = getReference(GuidedDecisionTableEditorService.class);
    }

    @After
    public void tearDown() throws Exception {
        super.cleanup();
    }

    @Test
    public void testFunctionFromDrl() throws Exception {
        final Path path = getPath("rhba370/src/main/resources/com/sample/dtissuesampleproject/UseFunctionFromDrl.gdst");
        final List<ValidationMessage> validationMessages = testedService.validate(path, testedService.load(path));
        Assertions.assertThat(validationMessages).isEmpty();
    }

    @Test
    @Ignore("RHDM-329")
    public void testUndeclaredFunction() throws Exception {
        final Path path = getPath("rhba370/src/main/resources/com/sample/dtissuesampleproject/UseUndeclaredFunction.gdst");
        final List<ValidationMessage> validationMessages = testedService.validate(path, testedService.load(path));
        Assertions.assertThat(validationMessages).hasSize(1);
        Assertions.assertThat(validationMessages)
                .extracting("text", String.class)
                .allMatch(text -> text.contains("[KBase: defaultKieBase]: Unable to Analyse Expression  isNotEmptyUndeclaredFunction(userCode)"));
    }

    private Path getPath(String resource) throws URISyntaxException {
        final URL resourceURL = getClass().getResource(resource);
        final org.uberfire.java.nio.file.Path resourceNioPath = fileSystemProvider.getPath(resourceURL.toURI());
        return Paths.convert(resourceNioPath);
    }
}
