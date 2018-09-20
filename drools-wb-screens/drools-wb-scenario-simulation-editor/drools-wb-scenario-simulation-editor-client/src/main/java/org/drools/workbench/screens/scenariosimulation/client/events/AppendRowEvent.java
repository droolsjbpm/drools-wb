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
import org.drools.workbench.screens.scenariosimulation.client.handlers.AppendRowEventHandler;

/**
 * <code>GwtEvent</code> to <b>append</b> (i.e. put in the last position) a row
 */
public class AppendRowEvent extends GwtEvent<AppendRowEventHandler> {

    public static Type<AppendRowEventHandler> TYPE = new Type<>();


    public AppendRowEvent() {
    }

    @Override
    public Type<AppendRowEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(AppendRowEventHandler handler) {
        handler.onEvent(this);
    }


}
