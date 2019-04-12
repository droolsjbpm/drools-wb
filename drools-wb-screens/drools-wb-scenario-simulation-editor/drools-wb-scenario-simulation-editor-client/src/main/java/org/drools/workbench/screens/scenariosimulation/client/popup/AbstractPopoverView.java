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

package org.drools.workbench.screens.scenariosimulation.client.popup;

import java.util.Objects;
import java.util.Optional;

import com.google.gwt.user.client.ui.RootPanel;
import org.jboss.errai.common.client.dom.CSSStyleDeclaration;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.uberfire.client.views.pfly.widgets.JQueryProducer;
import org.uberfire.client.views.pfly.widgets.Popover;
import org.uberfire.client.views.pfly.widgets.PopoverOptions;

public abstract class AbstractPopoverView implements PopoverView {


    public final static String TOP = "top";
    public final static String LEFT = "left";

    @DataField("popover")
    protected Div popoverElement;

    @DataField("popover-container")
    protected Div popoverContainerElement;

    @DataField("popover-content")
    protected Div popoverContentElement;

    protected JQueryProducer.JQuery<Popover> jQueryPopover;

    protected Popover popover;

    protected ElementWrapperWidget<?> wrappedWidget;


    public AbstractPopoverView() {
        //CDI proxy
    }

    public AbstractPopoverView(final Div popoverElement,
                               final Div popoverContainerElement,
                               final Div popoverContentElement,
                               final JQueryProducer.JQuery<Popover> jQueryPopover) {
        this.popoverElement = popoverElement;
        this.popoverContainerElement = popoverContainerElement;
        this.popoverContentElement = popoverContentElement;
        this.jQueryPopover = jQueryPopover;
    }

    @Override
    public void show(final Optional<String> editorTitle, final int mx, final int my) {
        wrappedWidget = ElementWrapperWidget.getWidget(getElement());
        RootPanel.get().add(wrappedWidget);
        final PopoverOptions options = new PopoverOptions();
        options.setContent((element) -> popoverContentElement);
        options.setAnimation(false);
        options.setHtml(true);

        editorTitle.ifPresent(t -> popoverElement.setAttribute("title", t));
        final HTMLElement element = this.getElement();
        popover = jQueryPopover.wrap(element);
        popover.popover(options);
        popover.show();
        final CSSStyleDeclaration style = popover.getStyle();
        style.setProperty(LEFT, mx + "px");
        style.setProperty(TOP, my + "px");
    }

    @Override
    public void hide() {
        if (Objects.nonNull(popover)) {
            popover.hide();
            popover.destroy();
        }
        RootPanel.get().remove(wrappedWidget);
    }
}
