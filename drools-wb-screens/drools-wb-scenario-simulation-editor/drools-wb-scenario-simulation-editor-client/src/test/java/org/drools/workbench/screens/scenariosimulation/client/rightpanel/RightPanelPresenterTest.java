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

package org.drools.workbench.screens.scenariosimulation.client.rightpanel;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.event.shared.EventBus;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.screens.scenariosimulation.client.events.SetInstanceHeaderEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.SetPropertyHeaderEvent;
import org.drools.workbench.screens.scenariosimulation.client.models.FactModelTree;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class RightPanelPresenterTest extends AbstractRightPanelTest {

    @Mock
    private RightPanelView mockRightPanelView;

    @Mock
    private DivElement mockListContainer;

    @Mock
    private ListGroupItemPresenter mockListGroupItemPresenter;
    @Mock
    private EventBus mockEventBus;

    private RightPanelPresenter rightPanelPresenter;

    @Before
    public void setup() {
        super.setup();
        when(mockRightPanelView.getListContainer()).thenReturn(mockListContainer);
        when(mockListGroupItemPresenter.getDivElement(FACT_NAME, FACT_MODEL_TREE)).thenReturn(mockListContainer);
        this.rightPanelPresenter = spy(new RightPanelPresenter(mockRightPanelView, mockListGroupItemPresenter) {
            {
                this.factTypeFieldsMap = mockTopLevelMap;
                this.eventBus = mockEventBus;
            }
        });
    }

    @Test
    public void onSetup() {
        rightPanelPresenter.setup();
        verify(mockRightPanelView, times(1)).init(rightPanelPresenter);
    }

    @Test
    public void getTitle() {
        assertEquals(ScenarioSimulationEditorConstants.INSTANCE.testTools(), rightPanelPresenter.getTitle());
    }

    @Test
    public void onClearSearch() {
        rightPanelPresenter.onClearSearch();
        verify(mockRightPanelView, times(1)).clearInputSearch();
        verify(mockRightPanelView, times(1)).hideClearButton();
    }

    @Test
    public void onClearNameField() {
        rightPanelPresenter.onClearNameField();
        verify(mockRightPanelView, times(1)).clearNameField();
    }

    @Test
    public void onClearStatus() {
        rightPanelPresenter.onClearStatus();
        verify(rightPanelPresenter, times(1)).onClearSearch();
        verify(rightPanelPresenter, times(1)).onClearNameField();
        verify(rightPanelPresenter, times(1)).clearList();
    }

    @Test
    public void getFactModelTree() {
        rightPanelPresenter.setFactTypeFieldsMap(mockTopLevelMap);
        String factName = getRandomFactModelTree(mockTopLevelMap, 0);
        FactModelTree retrieved = rightPanelPresenter.getFactModelTree(factName);
        assertNotNull(retrieved);
        assertEquals(mockTopLevelMap.get(factName), retrieved);
    }

    @Test
    public void setFactTypeFieldsMap() {
        rightPanelPresenter.setFactTypeFieldsMap(mockTopLevelMap);
        verify(rightPanelPresenter, times(mockTopLevelMap.size())).addListGroupItemView(anyString(), anyObject());
    }

    @Test
    public void onShowClearButton() {
        rightPanelPresenter.onShowClearButton();
        verify(mockRightPanelView, times(1)).showClearButton();
    }

    @Test
    public void setEventBus() {
        rightPanelPresenter.setEventBus(mockEventBus);
        assertEquals(mockEventBus, rightPanelPresenter.eventBus);
    }

    @Test
    public void addListGroupItemView() {
        rightPanelPresenter.addListGroupItemView(FACT_NAME, FACT_MODEL_TREE);
        verify(mockRightPanelView, times(1)).getListContainer();
        verify(mockListGroupItemPresenter, times(1)).getDivElement(eq(FACT_NAME), eq(FACT_MODEL_TREE));
        verify(mockListContainer, times(1)).appendChild(anyObject());
    }

    @Test
    public void onEnableEditorTabWithoutFactName() {
        rightPanelPresenter.onEnableEditorTab();
        verify(rightPanelPresenter, times(1)).onSearchedEvent(eq(""));
        verify(mockListGroupItemPresenter, times(1)).enable();
        verify(mockListGroupItemPresenter, never()).enable(anyString());
        verify(mockListGroupItemPresenter, never()).disable();
        verify(mockRightPanelView, times(1)).enableEditorTab();
    }

    @Test
    public void onEnableEditorTabWithFactName() {
        rightPanelPresenter.onEnableEditorTab(FACT_NAME);
        verify(rightPanelPresenter, times(1)).onSearchedEvent(eq(FACT_NAME));
        verify(mockListGroupItemPresenter, times(1)).enable(eq(FACT_NAME));
        verify(mockListGroupItemPresenter, never()).enable();
        verify(mockListGroupItemPresenter, never()).disable();
        verify(mockRightPanelView, times(1)).enableEditorTab();
    }

    @Test
    public void onDisableEditorTab() {
        rightPanelPresenter.onDisableEditorTab();
        verify(mockListGroupItemPresenter, times(1)).disable();
        verify(mockListGroupItemPresenter, never()).enable();
        verify(mockRightPanelView, times(1)).disableEditorTab();
    }

    @Test
    public void onModifyColumn() {
        rightPanelPresenter.editingColumnEnabled = true;
        rightPanelPresenter.onModifyColumn(FACT_NAME, VALUE, VALUE_CLASS_NAME);
        verify(mockEventBus, times(1)).fireEvent(isA(SetPropertyHeaderEvent.class));
        verify(mockEventBus, never()).fireEvent(isA(SetInstanceHeaderEvent.class));
        reset(mockEventBus);
        rightPanelPresenter.onModifyColumn(FACT_NAME);
        verify(mockEventBus, times(1)).fireEvent(isA(SetInstanceHeaderEvent.class));
        verify(mockEventBus, never()).fireEvent(isA(SetPropertyHeaderEvent.class));
    }
}