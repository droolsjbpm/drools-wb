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

package org.drools.workbench.screens.scenariosimulation.client.editor.menu;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.core.client.GWT;

/**
 * The contextual menu of the <i>OTHER</i> group (both top level and specific column).
 */
@ApplicationScoped
public class OtherContextMenu extends AbstractHeaderMenuPresenter {

    @PostConstruct
    @Override
    public void initMenu() {
        // SCENARIO
        addMenuItem("header-scenario", "SCENARIO", "");
        addExecutableMenuItem("header-insert-below", "Insert row below", "", () -> GWT.log("OtherContextMenu INSERT ROW BELOW"));
    }
}
