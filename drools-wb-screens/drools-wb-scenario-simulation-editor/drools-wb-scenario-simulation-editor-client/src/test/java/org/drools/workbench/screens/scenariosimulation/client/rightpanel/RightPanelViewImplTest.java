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

import com.google.gwt.dom.client.ButtonElement;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class RightPanelViewImplTest {

    private RightPanelViewImpl rightPanelView;

    @Mock
    private RightPanelPresenter mockRightPanelPresenter;

    @Mock
    private InputElement mockInputSearch;

    @Mock
    private InputElement mockNameField;

    @Mock
    private ButtonElement mockClearSearchButton;

    @Before
    public void setup() {
        this.rightPanelView = spy(new RightPanelViewImpl() {
            {
                this.inputSearch = mockInputSearch;
                this.clearSearchButton = mockClearSearchButton;
                this.nameField = mockNameField;
            }
        });
        rightPanelView.init(mockRightPanelPresenter);
        verify(mockRightPanelPresenter, times(1)).clearSearch();
    }

    @Test
    public void onClearSearchButtonClick() {
        reset(mockRightPanelPresenter);
        rightPanelView.onClearSearchButtonClick(mock(ClickEvent.class));
        verify(mockRightPanelPresenter, times(1)).clearSearch();
    }

    @Test
    public void onInputSearchKeyUp() {
        rightPanelView.onInputSearchKeyUp(mock(KeyUpEvent.class));
        verify(mockRightPanelPresenter, times(1)).showClearButton();
    }

    @Test
    public void clearInputSearch() {
        rightPanelView.clearInputSearch();
        verify(mockInputSearch, times(1)).setValue(eq(""));
    }

    @Test
    public void clearNameField() {
        rightPanelView.clearNameField();
        verify(mockNameField, times(1)).setValue(eq(""));
    }

    @Test
    public void hideClearButton() {
        rightPanelView.hideClearButton();
        verify(mockClearSearchButton, times(1)).setDisabled(eq(true));
        verify(mockClearSearchButton, times(1)).setAttribute(eq("style"), eq("display: none;"));
    }

    @Test
    public void showClearButton() {
        rightPanelView.showClearButton();
        verify(mockClearSearchButton, times(1)).setDisabled(eq(false));
        verify(mockClearSearchButton, times(1)).removeAttribute(eq("style"));
    }
}