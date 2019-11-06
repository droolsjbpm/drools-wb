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

import com.google.gwt.dom.client.LIElement;
import org.drools.workbench.screens.scenariosimulation.client.enums.GridWidget;
import org.drools.workbench.screens.scenariosimulation.client.events.DeleteColumnEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.DuplicateInstanceEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.InsertColumnEvent;

/**
 * This class is meant to provide common methods to <b>column-specific</b> menus {@link ExpectedContextMenu} and {@link GivenContextMenu},
 * both <b>instance</b> and <b>property</b> header.
 * It is provided to avoid code duplication in concrete implementations
 */
public abstract class AbstractColumnMenuPresenter extends AbstractHeaderMenuPresenter {

    protected String COLUMNCONTEXTMENU_COLUMN;
    protected String COLUMNCONTEXTMENU_INSERT_COLUMN_LEFT;
    protected String COLUMNCONTEXTMENU_INSERT_COLUMN_RIGHT;
    protected String COLUMNCONTEXTMENU_DELETE_COLUMN;
    protected String COLUMNCONTEXTMENU_DELETE_INSTANCE;
    protected String COLUMNCONTEXTMENU_DUPLICATE_INSTANCE;
    protected String COLUMNCONTEXTMENU_LABEL;
    protected String COLUMNCONTEXTMENU_I18N;

    protected LIElement insertColumnLeftLIElement;
    protected LIElement insertColumnRightLIElement;
    protected LIElement deleteColumnInstanceLIElement;
    protected LIElement duplicateInstanceLIElement;
    protected LIElement columnContextLIElement;

    /**
     * This method set <b>column-specific</b> menu items and common <b>SCENARIO</b> menu items
     */
    @Override
    public void initMenu() {
        columnContextLIElement = addMenuItem(COLUMNCONTEXTMENU_COLUMN, COLUMNCONTEXTMENU_LABEL, COLUMNCONTEXTMENU_I18N);
        insertColumnLeftLIElement = addExecutableMenuItem(COLUMNCONTEXTMENU_INSERT_COLUMN_LEFT, constants.insertColumnLeft(), "insertColumnLeft");
        insertColumnRightLIElement = addExecutableMenuItem(COLUMNCONTEXTMENU_INSERT_COLUMN_RIGHT, constants.insertColumnRight(), "insertColumnRight");
        deleteColumnInstanceLIElement = addExecutableMenuItem(COLUMNCONTEXTMENU_DELETE_COLUMN, constants.deleteColumn(), "deleteColumn");
        duplicateInstanceLIElement = addExecutableMenuItem(COLUMNCONTEXTMENU_DUPLICATE_INSTANCE, constants.duplicateInstance(), "duplicateInstance");
        super.initMenu();
    }

    public void show(final GridWidget gridWidget, final int mx, final int my, int columnIndex, String group, boolean asProperty, boolean showDuplicateInstance) {
        if(!showDuplicateInstance && duplicateInstanceLIElement != null) {
            removeMenuItem(duplicateInstanceLIElement);
            duplicateInstanceLIElement = null;
        }
        mapEvent(insertColumnLeftLIElement, new InsertColumnEvent(gridWidget, columnIndex, false, asProperty));
        mapEvent(insertColumnRightLIElement, new InsertColumnEvent(gridWidget, columnIndex, true, asProperty));
        if (asProperty) {
            updateMenuItemAttributes(deleteColumnInstanceLIElement, COLUMNCONTEXTMENU_DELETE_COLUMN, constants.deleteColumn(), "deleteColumn");
            mapEvent(deleteColumnInstanceLIElement, new DeleteColumnEvent(gridWidget, columnIndex, group, true));
        } else {
            updateMenuItemAttributes(deleteColumnInstanceLIElement, COLUMNCONTEXTMENU_DELETE_INSTANCE, constants.deleteInstance(), "deleteInstance");
            mapEvent(deleteColumnInstanceLIElement, new DeleteColumnEvent(gridWidget, columnIndex, group, false));
        }
        if (duplicateInstanceLIElement != null) {
            mapEvent(duplicateInstanceLIElement, new DuplicateInstanceEvent(gridWidget, columnIndex));
        }
        callSuperShow(mx, my);
    }

    protected void callSuperShow(final int mx, final int my) {
        super.show(mx, my);
    }

}
