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
import org.drools.workbench.screens.scenariosimulation.client.handlers.InsertColumnLeftEventHandler;

/**
 * <code>GwtEvent</code> to <b>insert</b> a column to the left of the selected one
 */
public class InsertColumnLeftEvent extends GwtEvent<InsertColumnLeftEventHandler> {

    public static Type<InsertColumnLeftEventHandler> TYPE = new Type<>();

    public InsertColumnLeftEvent() {
    }

    @Override
    public Type<InsertColumnLeftEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(InsertColumnLeftEventHandler handler) {
        handler.onEvent(this);
    }

}
