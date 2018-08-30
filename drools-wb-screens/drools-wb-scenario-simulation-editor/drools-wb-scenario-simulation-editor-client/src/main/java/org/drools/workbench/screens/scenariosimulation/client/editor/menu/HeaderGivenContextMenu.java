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
import javax.enterprise.context.Dependent;

/**
 * The contextual menu of the top level <i>EXPECTED</i> group.
 * It differ from <code>GivenContextMenu</code> because it manage column (insert/remove) in different way
 */
@Dependent
public class HeaderGivenContextMenu extends AbstractHeaderMenuPresenter {
    
    private final String HEADERGIVENCONTEXTMENU_GIVEN = "headergivencontextmenu-given";
    private final String HEADERGIVENCONTEXTMENU_SCENARIO = "headergivencontextmenu-scenario";
    private final String HEADERGIVENCONTEXTMENU_INSERT_COLUMN_LEFT = "headergivencontextmenu-insert-column-left";
    private final String HEADERGIVENCONTEXTMENU_INSERT_COLUMN_RIGHT = "headergivencontextmenu-insert-column-right";
    private final String HEADERGIVENCONTEXTMENU_DELETE_COLUMN = "headergivencontextmenu-delete-column";
    private final String HEADERGIVENCONTEXTMENU_INSERT_ROW_BELOW = "headergivencontextmenu-insert-row-below";

    @PostConstruct
    @Override
    public void initMenu() {
//        addMenuItem(HEADERGIVENCONTEXTMENU_GIVEN, constants.given(), "given");
//        addExecutableMenuItem(HEADERGIVENCONTEXTMENU_INSERT_COLUMN_LEFT, constants.insertColumnLeft(), "insertColumnLeft", () -> GWT.log(HEADERGIVENCONTEXTMENU_INSERT_COLUMN_LEFT));
//        addExecutableMenuItem(HEADERGIVENCONTEXTMENU_INSERT_COLUMN_RIGHT, constants.insertColumnRight(), "insertColumnRight", () -> GWT.log(HEADERGIVENCONTEXTMENU_INSERT_COLUMN_RIGHT));
//        addExecutableMenuItem(HEADERGIVENCONTEXTMENU_DELETE_COLUMN, constants.deleteColumn(), "deleteColumn", () -> GWT.log(HEADERGIVENCONTEXTMENU_DELETE_COLUMN));
        // SCENARIO
        addMenuItem(HEADERGIVENCONTEXTMENU_SCENARIO, constants.scenario(), "scenario");
        addExecutableMenuItem(HEADERGIVENCONTEXTMENU_INSERT_ROW_BELOW, constants.insertRowBelow(), "insertRowBelow", appendRowEvent);
    }


}
