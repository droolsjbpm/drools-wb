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
package org.drools.workbench.screens.scenariosimulation.client.collectioneditor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.drools.workbench.screens.scenariosimulation.client.utils.ConstantHolder.NODE_HIDDEN;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class PropertyPresenterTest extends AbstractCollectionEditorTest {

    private static final String INNER_TEXT = "INNER_TEXT";
    private static final String ITEM_ID = "ITEM_ID";
    private static final String TEST_PROPERTYNAME = "TEST-PROPERTYNAME";
    private static final String TEST_PROPERTYVALUE = "TEST-PROPERTYVALUE";
    private static final String TEST_NEWVALUE = "TEST_NEWVALUE";

    @Mock
    private PropertyView propertyViewMock;

    @Mock
    private PropertyViewImpl propertyViewImplMock;

    @Mock
    private LIElement liElementMock;

    @Mock
    private SpanElement spanElementMock;

    @Mock
    private InputElement propertyValueInputMock;

    @Mock
    private LIElement propertyFieldsMock;

    private Map<String, SpanElement> propertySpanElementMapLocal = new HashMap<>();

    private Map<String, List<PropertyView>> propertyViewMapLocal = new HashMap<>();

    @Mock
    private Style styleMock;

    private PropertyPresenter propertyEditorPresenter;

    @Before
    public void setup() {
        List<PropertyView> propertyViewListLocal = new ArrayList<>();
        propertyViewListLocal.add(propertyViewMock);
        propertyViewMapLocal.put(ITEM_ID, propertyViewListLocal);
        propertySpanElementMapLocal.put(TEST_PROPERTYNAME, spanElementMock);
        when(liElementMock.getStyle()).thenReturn(styleMock);
        this.propertyEditorPresenter = spy(new PropertyPresenter() {
            {
                this.viewsProvider = viewsProviderMock;
                this.propertySpanElementMap = propertySpanElementMapLocal;
                this.propertyViewMap = propertyViewMapLocal;
            }
        });
        when(spanElementMock.getInnerText()).thenReturn(INNER_TEXT);
        when(spanElementMock.getStyle()).thenReturn(styleMock);
        when(spanElementMock.getAttribute("data-i18n-key")).thenReturn(TEST_PROPERTYNAME);
        when(viewsProviderMock.getPropertyEditorView()).thenReturn(propertyViewMock);
        when(propertyValueInputMock.getStyle()).thenReturn(styleMock);
        when(propertyValueInputMock.getValue()).thenReturn(TEST_NEWVALUE);
        when(propertyViewMock.getPropertyName()).thenReturn(spanElementMock);
        when(propertyViewMock.getPropertyValueSpan()).thenReturn(spanElementMock);
        when(propertyViewMock.getPropertyFields()).thenReturn(propertyFieldsMock);
        when(propertyViewMock.getPropertyValueInput()).thenReturn(propertyValueInputMock);
        when(propertyViewImplMock.getPropertyFields()).thenReturn(liElementMock);
        when(propertyFieldsMock.getStyle()).thenReturn(styleMock);
    }

    @Test
    public void getPropertyValueContainsKey() throws Exception {
        assertEquals(INNER_TEXT, propertyEditorPresenter.getPropertyValue(TEST_PROPERTYNAME));
    }

    @Test(expected = Exception.class)
    public void getPropertyValueNotContainsKey() throws Exception {
        propertySpanElementMapLocal.clear();
        propertyEditorPresenter.getPropertyValue(TEST_PROPERTYNAME);
    }

    @Test
    public void editProperties() {
        propertyEditorPresenter.editProperties(ITEM_ID);
        verify(spanElementMock, times(1)).getStyle();
        verify(styleMock, times(1)).setDisplay(Style.Display.NONE);
        verify(propertyValueInputMock, times(1)).setValue(anyString());
        verify(propertyValueInputMock, times(1)).getStyle();
        verify(styleMock, times(1)).setDisplay(Style.Display.INLINE);
        verify(propertyValueInputMock, times(1)).setDisabled(eq(false));
    }

    @Test
    public void stopEditProperties() {
        propertyEditorPresenter.stopEditProperties(ITEM_ID);
        verify(propertyEditorPresenter, times(1)).stopEdit(eq(ITEM_ID), eq(false));
    }

    @Test
    public void updateProperties() {
        assertNotNull(propertyEditorPresenter.updateProperties(ITEM_ID));
        verify(propertyEditorPresenter, times(1)).stopEdit(eq(ITEM_ID), eq(true));
    }

    @Test
    public void getProperties() {
        assertNotNull(propertyEditorPresenter.getProperties(ITEM_ID));
    }

    @Test
    public void getPropertyFields() {
        String hashedPropertyName = "#" + TEST_PROPERTYNAME;
        propertyViewMapLocal.clear();
        final LIElement retireved = propertyEditorPresenter.getPropertyFields(ITEM_ID, TEST_PROPERTYNAME, TEST_PROPERTYVALUE);
        assertNotNull(retireved);
        verify(propertyValueInputMock, times(1)).setAttribute(eq("placeholder"), eq(hashedPropertyName));
        verify(propertyValueInputMock, times(1)).setAttribute(eq("data-field"), eq("propertyValue" + hashedPropertyName));
        verify(propertyValueInputMock, times(1)).setDisabled(eq(true));
        verify(styleMock, times(1)).setDisplay(eq(Style.Display.NONE));
        verify(propertyFieldsMock, times(1)).setAttribute(eq("data-field"), eq("propertyFields" + hashedPropertyName));
        assertTrue(propertyViewMapLocal.containsKey(ITEM_ID));
        assertTrue(propertyViewMapLocal.get(ITEM_ID).contains(propertyViewMock));
    }

    @Test
    public void getEditingPropertyFields() {
        assertNotNull(propertyEditorPresenter.getEditingPropertyFields(ITEM_ID, TEST_PROPERTYNAME, TEST_PROPERTYVALUE));
        verify(propertyEditorPresenter, times(1)).getPropertyFields(eq(ITEM_ID), eq(TEST_PROPERTYNAME), eq(TEST_PROPERTYVALUE));
        verify(propertyEditorPresenter, times(1)).editProperties(eq(ITEM_ID));
    }

    @Test
    public void onToggleRowExpansion() {
        propertyEditorPresenter.onToggleRowExpansion(ITEM_ID, true);
        verify(propertyFieldsMock, times(1)).addClassName(eq(NODE_HIDDEN));
        verify(styleMock, times(1)).setDisplay(eq(Style.Display.NONE));
        propertyEditorPresenter.onToggleRowExpansion(ITEM_ID, false);
        verify(propertyFieldsMock, times(1)).removeClassName(eq(NODE_HIDDEN));
        verify(styleMock, times(1)).setDisplay(eq(Style.Display.BLOCK));
    }

    @Test
    public void deleteProperties() {
        propertyEditorPresenter.deleteProperties(ITEM_ID);
        verify(propertyFieldsMock, times(1)).removeFromParent();
        assertFalse(propertySpanElementMapLocal.containsKey(TEST_PROPERTYNAME));
        assertFalse(propertyViewMapLocal.containsKey(ITEM_ID));
    }

    @Test
    public void stopEditToUpdateTrue() {
        commonStopEdit(true);
    }

    @Test
    public void stopEditToUpdateFalse() {
        commonStopEdit(false);
    }

    private void commonStopEdit(boolean toUdate) {
        final Map<String, String> retrieved = propertyEditorPresenter.stopEdit(ITEM_ID, toUdate);
        assertNotNull(retrieved);
        assertTrue(retrieved.containsKey(INNER_TEXT));
        if (toUdate) {
            verify(spanElementMock, times(1)).setInnerText(eq(TEST_NEWVALUE));
        }
        assertEquals(INNER_TEXT, retrieved.get(INNER_TEXT));
        verify(styleMock, times(1)).setDisplay(Style.Display.INLINE);
        verify(styleMock, times(1)).setDisplay(Style.Display.NONE);
        verify(propertyValueInputMock, times(1)).setDisabled(eq(true));
    }
}
