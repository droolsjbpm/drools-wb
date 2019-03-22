/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.scenariosimulation.client.collectioneditor.editingbox;

import java.util.Collections;
import java.util.Map;

import com.google.gwt.dom.client.UListElement;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.Matchers.anyMap;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ItemEditingBoxPresenterTest extends AbstractEditingBoxPresenterTest {

    private static final String TEST_KEY = "TEST-CLASSNAME#TEST-PROPERTYNAME";
    private static final String TEST_PROPERTYNAME = "TEST-PROPERTYNAME";

    private Map<String, String> testInstancePropertyMap = Collections.singletonMap("TEST-KEY", "TEST-VALUE");

    @Mock
    private ItemEditingBox listEditingBoxMock;

    @Mock
    private UListElement propertiesContainerMock;


    @Before
    public void setup() {
        editingBoxToCloseMock = listEditingBoxMock;
        when(listEditingBoxMock.getPropertiesContainer()).thenReturn(propertiesContainerMock);
        when(viewsProviderMock.getItemEditingBox()).thenReturn(listEditingBoxMock);
        editingBoxPresenter = spy(new ItemEditingBoxPresenter() {
            {
                this.viewsProvider = viewsProviderMock;
                this.propertyPresenter = propertyPresenterMock;
                this.collectionEditorPresenter = collectionPresenterMock;
            }
        });
        super.setup();
    }

    @Test
    public void getEditingBox() {
        ((ItemEditingBoxPresenter)editingBoxPresenter).getEditingBox(TEST_KEY, testInstancePropertyMap, anyMap());
        verify(viewsProviderMock, times(1)).getItemEditingBox();
        verify(listEditingBoxMock, times(1)).init(((ItemEditingBoxPresenter)editingBoxPresenter));
        verify(listEditingBoxMock, times(1)).setKey(TEST_KEY);
        verify(editingBoxTitleMock, times(1)).setInnerText("Edit " + TEST_PROPERTYNAME);
        verify(listEditingBoxMock, times(1)).getEditingBox();
    }

    @Test
    public void save() {
        editingBoxPresenter.save();
        verify(propertyPresenterMock, times(1)).updateProperties("value");
        verify(collectionPresenterMock, times(1)).addListItem(anyMap(), anyMap());
    }

}
