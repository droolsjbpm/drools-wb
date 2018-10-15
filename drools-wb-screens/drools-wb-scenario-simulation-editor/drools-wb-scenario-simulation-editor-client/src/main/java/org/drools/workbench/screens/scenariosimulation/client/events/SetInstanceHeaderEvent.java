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
import org.drools.workbench.screens.scenariosimulation.client.handlers.SetInstanceHeaderEventHandler;

/**
 * <code>GwtEvent</code> to set the <i>instance</i> level header for a given column
 */
public class SetInstanceHeaderEvent extends GwtEvent<SetInstanceHeaderEventHandler> {

    public static Type<SetInstanceHeaderEventHandler> TYPE = new Type<>();

    private String fullClassName;

    /**
     * Use this constructor to modify the <i>instance</i> level header
     * 
     * @param fullClassName
     */
    public SetInstanceHeaderEvent(String fullClassName) {
        this.fullClassName = fullClassName;
    }

    @Override
    public Type<SetInstanceHeaderEventHandler> getAssociatedType() {
        return TYPE;
    }

    public String getFullClassName() {
        return fullClassName;
    }

    @Override
    protected void dispatch(SetInstanceHeaderEventHandler handler) {
        handler.onEvent(this);
    }
}
