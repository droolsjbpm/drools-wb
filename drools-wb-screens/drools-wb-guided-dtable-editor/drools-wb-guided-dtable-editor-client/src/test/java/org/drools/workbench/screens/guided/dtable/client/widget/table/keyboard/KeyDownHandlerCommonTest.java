/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.guided.dtable.client.widget.table.keyboard;

import java.util.Optional;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class KeyDownHandlerCommonTest extends BaseKeyDownHandlerTest {

    @Override
    protected KeyDownHandler getHandler() {
        return new KeyDownHandlerCommon(gridPanel,
                                        gridLayer,
                                        gridWidget,
                                        gridCell,
                                        context);
    }

    @Test
    public void tabKeyCanvasActions() {
        final KeyDownEvent e = mockKeyDownEvent(Optional.of(KeyCodes.KEY_TAB),
                                                Optional.of(false),
                                                Optional.of(false));

        handler.onKeyDown(e);

        verify(gridCell).flush();
        verify(e).preventDefault();
        verifyCommonActions();
    }

    @Test
    public void enterKeyCanvasActions() {
        final KeyDownEvent e = mockKeyDownEvent(Optional.of(KeyCodes.KEY_ENTER),
                                                Optional.of(false),
                                                Optional.of(false));

        handler.onKeyDown(e);

        verify(gridCell).flush();
        verifyCommonActions();
    }

    @Test
    public void escapeKeyCanvasActions() {
        final KeyDownEvent e = mockKeyDownEvent(Optional.of(KeyCodes.KEY_ESCAPE),
                                                Optional.of(false),
                                                Optional.of(false));

        handler.onKeyDown(e);

        verify(gridCell,
               never()).flush();
        verifyCommonActions();
    }

    private void verifyCommonActions() {
        verify(gridCell).destroyResources();
        verify(gridPanel).setFocus(eq(true));
        verify(gridLayer).batch();
    }
}
