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

import java.util.Collections;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.screens.scenariosimulation.client.events.CloseCompositeEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.SaveEditorEvent;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class CollectionViewImplTest extends AbstractCollectionEditorTest {

    private CollectionViewImpl collectionEditorViewImplSpy;

    @Mock
    private DivElement collectionEditorModalBodyMock;
    @Mock
    private LabelElement createLabelMock;
    @Mock
    private LabelElement collectionCreationModeLabelMock;
    @Mock
    private SpanElement collectionCreationCreateLabelMock;
    @Mock
    private SpanElement collectionCreationCreateSpanMock;
    @Mock
    private SpanElement collectionCreationDefineLabelMock;
    @Mock
    private SpanElement collectionCreationDefineSpanMock;

    @Before
    public void setup() {
        when(collectionEditorModalBodyMock.getStyle()).thenReturn(styleMock);
        this.collectionEditorViewImplSpy = spy(new CollectionViewImpl() {
            {
                this.presenter = collectionPresenterMock;
                this.collectionEditorModalBody = collectionEditorModalBodyMock;
                this.createLabel = createLabelMock;
                this.collectionCreationModeLabel = collectionCreationModeLabelMock;
                this.collectionCreationCreateLabel = collectionCreationCreateLabelMock;
                this.collectionCreationCreateSpan = collectionCreationCreateSpanMock;
                this.collectionCreationDefineLabel = collectionCreationDefineLabelMock;
                this.collectionCreationDefineSpan = collectionCreationDefineSpanMock;
            }
        });
    }

    @Test
    public void initListStructure() {
        collectionEditorViewImplSpy.initListStructure("key", Collections.EMPTY_MAP, Collections.EMPTY_MAP, false, false);
        verify(collectionEditorViewImplSpy, times(1)).commonInit(eq(false), eq(false));
        verify(createLabelMock, atLeast(1)).setInnerText(ScenarioSimulationEditorConstants.INSTANCE.createLabelList());
        verify(collectionCreationModeLabelMock, times(1)).setInnerText(ScenarioSimulationEditorConstants.INSTANCE.collectionListCreation());
        verify(collectionCreationCreateLabelMock, times(1)).setInnerText(ScenarioSimulationEditorConstants.INSTANCE.createLabelList());
        verify(collectionCreationCreateSpanMock, times(1)).setInnerText(ScenarioSimulationEditorConstants.INSTANCE.createLabelListDescription());
        verify(collectionCreationDefineLabelMock, times(1)).setInnerText(ScenarioSimulationEditorConstants.INSTANCE.defineLabelList());
        verify(collectionCreationDefineSpanMock, times(1)).setInnerText(ScenarioSimulationEditorConstants.INSTANCE.defineLabelListDescription());
        verify(collectionPresenterMock, times(1)).initListStructure(eq("key"), eq(Collections.EMPTY_MAP), eq(Collections.EMPTY_MAP), eq(collectionEditorViewImplSpy));
        assertTrue(collectionEditorViewImplSpy.listWidget);
    }

    @Test
    public void initMapStructure() {
        collectionEditorViewImplSpy.initMapStructure("key", Collections.EMPTY_MAP, Collections.EMPTY_MAP, false, false);
        verify(collectionEditorViewImplSpy, times(1)).commonInit(eq(false), eq(false));
        verify(createLabelMock, atLeast(1)).setInnerText(ScenarioSimulationEditorConstants.INSTANCE.createLabelMap());
        verify(collectionCreationModeLabelMock, times(1)).setInnerText(ScenarioSimulationEditorConstants.INSTANCE.collectionMapCreation());
        verify(collectionCreationCreateLabelMock, times(1)).setInnerText(ScenarioSimulationEditorConstants.INSTANCE.createLabelMap());
        verify(collectionCreationCreateSpanMock, times(1)).setInnerText(ScenarioSimulationEditorConstants.INSTANCE.createLabelMapDescription());
        verify(collectionCreationDefineLabelMock, times(1)).setInnerText(ScenarioSimulationEditorConstants.INSTANCE.defineLabelMap());
        verify(collectionCreationDefineSpanMock, times(1)).setInnerText(ScenarioSimulationEditorConstants.INSTANCE.defineLabelMapDescription());
        verify(collectionPresenterMock, times(1)).initMapStructure(eq("key"), eq(Collections.EMPTY_MAP), eq(Collections.EMPTY_MAP), eq(collectionEditorViewImplSpy));
        assertFalse(collectionEditorViewImplSpy.listWidget);
    }

    @Test
    public void setValue() {
        String testValue = "TEST-JSON";
        collectionEditorViewImplSpy.setValue(testValue);
        verify(collectionPresenterMock, times(1)).setValue(eq(testValue));
    }

    @Test
    public void onCloseCollectionEditorButtonClick() {
        collectionEditorViewImplSpy.onCloseCollectionEditorButtonClick(clickEventMock);
        verify(collectionEditorViewImplSpy, times(1)).fireEvent(isA(CloseCompositeEvent.class));
        verify(clickEventMock, times(1)).stopPropagation();
    }

    @Test
    public void onCancelButtonClick() {
        collectionEditorViewImplSpy.onCancelButtonClick(clickEventMock);
        verify(collectionEditorViewImplSpy, times(1)).close();
        verify(clickEventMock, times(1)).stopPropagation();
    }

    @Test
    public void onRemoveButtonClick() {
        collectionEditorViewImplSpy.onRemoveButtonClick(clickEventMock);
        verify(collectionPresenterMock, times(1)).remove();
        verify(clickEventMock, times(1)).stopPropagation();
    }

    @Test
    public void onSaveButtonClick() {
        collectionEditorViewImplSpy.onSaveButtonClick(clickEventMock);
        verify(collectionPresenterMock, times(1)).save();
        verify(clickEventMock, times(1)).stopPropagation();
    }

    @Test
    public void onAddItemButton() {
        collectionEditorViewImplSpy.onAddItemButton(clickEventMock);
        verify(collectionPresenterMock, times(1)).showEditingBox();
        verify(clickEventMock, times(1)).stopPropagation();
    }

    @Test
    public void onFaAngleRightClick() {
        doReturn(true).when(collectionEditorViewImplSpy).isShown();
        collectionEditorViewImplSpy.onFaAngleRightClick(clickEventMock);
        verify(collectionPresenterMock, times(1)).onToggleRowExpansion(eq(true));
        verify(clickEventMock, times(1)).stopPropagation();
        reset(collectionPresenterMock);
        reset(clickEventMock);
        doReturn(false).when(collectionEditorViewImplSpy).isShown();
        collectionEditorViewImplSpy.onFaAngleRightClick(clickEventMock);
        verify(collectionPresenterMock, times(1)).onToggleRowExpansion(eq(false));
        verify(clickEventMock, times(1)).stopPropagation();
    }

    @Test
    public void toggleRowExpansion() {
        doReturn(true).when(collectionEditorViewImplSpy).isShown();
        collectionEditorViewImplSpy.toggleRowExpansion();
        verify(collectionEditorViewImplSpy, times(1)).toggleRowExpansion(false);
        reset(collectionEditorViewImplSpy);
        doReturn(false).when(collectionEditorViewImplSpy).isShown();
        collectionEditorViewImplSpy.toggleRowExpansion();
        verify(collectionEditorViewImplSpy, times(1)).toggleRowExpansion(true);
    }

    @Test
    public void updateValue() {
        collectionEditorViewImplSpy.updateValue("TEST_VALUE");
        verify(collectionEditorViewImplSpy, times(1)).fireEvent(isA(SaveEditorEvent.class));
    }

    @Test
    public void close() {
        collectionEditorViewImplSpy.close();
        verify(collectionEditorViewImplSpy, times(1)).fireEvent(isA(CloseCompositeEvent.class));
    }

    @Test
    public void setFixedHeight() {
        double value = 23.0;
        Style.Unit unit = Style.Unit.PX;
        collectionEditorViewImplSpy.setFixedHeight(value, unit);
        verify(styleMock, times(1)).setHeight(eq(value), eq(unit));
    }
}
