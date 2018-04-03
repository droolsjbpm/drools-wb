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

package org.drools.workbench.screens.drltext.client.editor;

import java.util.ArrayList;

import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.drools.workbench.screens.drltext.service.DRLTextEditorService;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.gwtbootstrap3.client.ui.Modal;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.test.MockProvider;
import org.kie.workbench.common.widgets.client.callbacks.AssetValidatedCallback;
import org.kie.workbench.common.widgets.client.popups.validation.ValidationPopup;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.client.history.VersionRecordManager;
import org.uberfire.java.nio.IOException;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mvp.Command;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
@WithClassesToStub({Modal.class})
public class DRLEditorPresenterValidationTest {

    @Mock
    private DRLEditorView view;

    @Mock
    protected DRLTextEditorService drlTextEditorService;

    private DRLEditorPresenter presenter;

    @Before
    public void setUp() throws Exception {
        presenter = new DRLEditorPresenter(view) {
            {
                drlTextEditorService = new CallerMock(DRLEditorPresenterValidationTest.this.drlTextEditorService);
                validationPopup = MockProvider.getMockValidationPopup();
                versionRecordManager = mock(VersionRecordManager.class);
            }
        };
    }

    @Test
    public void commandIsCalled() throws Exception {

        doReturn(new ArrayList<ValidationMessage>()).when(drlTextEditorService).validate(any(Path.class),
                                                                                         anyString());

        final Command afterValidation = mock(Command.class);
        presenter.onValidate(afterValidation);
        verify(afterValidation).execute();
    }

    @Test
    public void callFailsAndCommandIsCalled() throws Exception {

        doThrow(new IOException()).when(drlTextEditorService).validate(any(Path.class),
                                                                       anyString());

        final Command afterValidation = mock(Command.class);
        presenter.onValidate(afterValidation);
        verify(afterValidation).execute();
    }
}