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
package org.drools.workbench.screens.guided.dtable.client.widget.table.columns.dom.listbox;

import java.util.Collections;

import com.ait.lienzo.client.core.types.Transform;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.gwtbootstrap3.client.ui.ListBox;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridData;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.GridLayerRedrawManager.PrioritizedCommand;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.GridLienzoPanel;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class ListBoxSingletonDOMElementFactoryTest {

    @Mock
    private ListBox listBox;

    @Mock
    private Element listBoxElement;

    @Mock
    private AbsolutePanel domElementContainer;

    @Mock
    private GridLienzoPanel gridLienzoPanel;

    @Mock
    private GridLayer gridLayer;

    @Mock
    private GuidedDecisionTableView gridWidget;

    @Mock
    private GridBodyCellRenderContext cellRenderContext;

    private ListBoxSingletonDOMElementFactory<String, ListBox> factory;

    @Before
    public void setup() {
        when(listBox.getElement()).thenReturn(listBoxElement);
        when(listBoxElement.getStyle()).thenReturn(mock(Style.class));
        when(domElementContainer.iterator()).thenReturn(Collections.<Widget>emptyList().iterator());
        when(gridLayer.getDomElementContainer()).thenReturn(domElementContainer);
        when(gridWidget.getModel()).thenReturn(new BaseGridData());

        doAnswer((i) -> {
            final PrioritizedCommand command = (PrioritizedCommand) i.getArguments()[0];
            command.execute();
            return null;
        }).when(gridLayer).batch(any(PrioritizedCommand.class));

        factory = spy(new ListBoxSingletonDOMElementFactoryMock(gridLienzoPanel,
                                                                gridLayer,
                                                                gridWidget));
    }

    @Test
    public void checkDOMElementCreationChecksListBoxIsMultipleSelect() {
        factory.createDomElement(gridLayer,
                                 gridWidget,
                                 cellRenderContext);

        verify(listBox).addMouseDownHandler(any(MouseDownHandler.class));
        verify(listBox).addKeyDownHandler(any(KeyDownHandler.class));
        verify(listBox).addBlurHandler(any(BlurHandler.class));
        verify(listBox).isMultipleSelect();
    }

    @Test
    public void checkDOMElementCreationMouseDownHandler() {
        factory.createDomElement(gridLayer,
                                 gridWidget,
                                 cellRenderContext);

        final ArgumentCaptor<MouseDownHandler> mouseDownHandlerCaptor = ArgumentCaptor.forClass(MouseDownHandler.class);

        verify(listBox).addMouseDownHandler(mouseDownHandlerCaptor.capture());

        final MouseDownEvent e = mock(MouseDownEvent.class);
        final MouseDownHandler mouseDownHandler = mouseDownHandlerCaptor.getValue();
        mouseDownHandler.onMouseDown(e);

        verify(e).stopPropagation();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void checkDOMElementCreationBlurHandler() {
        final InOrder order = inOrder(factory);

        final GridBodyCellRenderContext context = mock(GridBodyCellRenderContext.class);
        final Callback<ListBoxDOMElement<String, ListBox>> onCreation = mock(Callback.class);
        final Callback<ListBoxDOMElement<String, ListBox>> onDisplay = mock(Callback.class);
        when(context.getTransform()).thenReturn(mock(Transform.class));

        factory.attachDomElement(context,
                                 onCreation,
                                 onDisplay);

        final ArgumentCaptor<BlurHandler> blurHandlerCaptor = ArgumentCaptor.forClass(BlurHandler.class);

        verify(listBox).addBlurHandler(blurHandlerCaptor.capture());

        final BlurEvent e = mock(BlurEvent.class);
        final BlurHandler blurHandler = blurHandlerCaptor.getValue();
        blurHandler.onBlur(e);

        order.verify(factory).flush();
        order.verify(factory).destroyResources();
        verify(gridLayer).batch();
        verify(gridLienzoPanel).setFocus(eq(true));
    }

    private class ListBoxSingletonDOMElementFactoryMock extends ListBoxSingletonDOMElementFactory<String, ListBox> {

        public ListBoxSingletonDOMElementFactoryMock(final GridLienzoPanel gridPanel,
                                                     final GridLayer gridLayer,
                                                     final GuidedDecisionTableView gridWidget) {
            super(gridPanel,
                  gridLayer,
                  gridWidget);
        }

        @Override
        public String convert(final String value) {
            return "";
        }

        @Override
        public ListBox createWidget() {
            return listBox;
        }
    }
}
