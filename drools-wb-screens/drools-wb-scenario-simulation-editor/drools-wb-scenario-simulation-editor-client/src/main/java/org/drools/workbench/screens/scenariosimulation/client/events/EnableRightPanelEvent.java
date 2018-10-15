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
package org.drools.workbench.screens.scenariosimulation.client.events;

import com.google.gwt.event.shared.GwtEvent;
import org.drools.workbench.screens.scenariosimulation.client.handlers.EnableRightPanelEventHandler;

/**
 * <code>GwtEvent</code> to <b>enable</b> the <code>RightPanelView</code>
 */
public class EnableRightPanelEvent extends GwtEvent<EnableRightPanelEventHandler> {

    public static Type<EnableRightPanelEventHandler> TYPE = new Type<>();

    private final String factName;

    /**
     * Fire this event to show all the first-level data models <b>enabled</b> (i.e. <b>double-clickable</b> to map to an <i>instance</i> header/column)
     * and their properties <b>disabled</b> (i.e. <b>not double-clickable</b>)
     */
    public EnableRightPanelEvent() {
        factName = null;
    }

    /**
     * Fire this event to show only the data model with the given name, <b>disabled</b> (i.e. <b>not double-clickable</b>)
     * and their properties <b>enabled</b> (i.e. <b>double-clickable</b> to map to a <i>property</i> header/column below the belonging data model instance one)
     *
     * @param factName
     */
    public EnableRightPanelEvent(String factName) {
        this.factName = factName;
    }

    @Override
    public Type<EnableRightPanelEventHandler> getAssociatedType() {
        return TYPE;
    }

    public String getFactName() {
        return factName;
    }

    @Override
    protected void dispatch(EnableRightPanelEventHandler handler) {
        handler.onEvent(this);
    }
}
