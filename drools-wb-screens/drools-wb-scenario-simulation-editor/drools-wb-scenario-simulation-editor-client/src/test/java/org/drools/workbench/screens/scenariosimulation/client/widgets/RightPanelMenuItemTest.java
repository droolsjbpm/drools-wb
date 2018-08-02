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

package org.drools.workbench.screens.scenariosimulation.client.widgets;

import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.screens.scenariosimulation.client.commands.RightPanelMenuCommand;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.RightPanelPresenter;
import org.gwtbootstrap3.client.ui.Button;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.PlaceStatus;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class RightPanelMenuItemTest {

    private RightPanelMenuItem rightPanelMenuItem;

    @Mock
    private RightPanelMenuCommand rightPanelMenuCommand;

    @Mock
    private PlaceManager placeManager;

    @Before
    public void setup() {
        this.rightPanelMenuItem = new RightPanelMenuItem(placeManager, rightPanelMenuCommand);
        when(placeManager.getStatus(RightPanelPresenter.IDENTIFIER)).thenReturn(PlaceStatus.OPEN);
    }

    @Test
    public void init() {
        rightPanelMenuItem.init();
        verify(rightPanelMenuItem.getButton(), times(1)).setText(ScenarioSimulationEditorConstants.INSTANCE.HideRightPanel());
        verify(placeManager, times(1)).registerOnOpenCallback(any(DefaultPlaceRequest.class), any());
        verify(placeManager, times(1)).registerOnCloseCallback(any(DefaultPlaceRequest.class), any());
    }

    @Test
    public void build() {
        Widget retrieved = rightPanelMenuItem.build();
        assertNotNull(retrieved);
        assertTrue(retrieved instanceof Button);
    }
}