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
package org.drools.workbench.screens.scenariosimulation.client.utils;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.drools.workbench.screens.scenariosimulation.client.collectioneditor.ListEditorElementView;
import org.drools.workbench.screens.scenariosimulation.client.collectioneditor.PropertyEditorView;
import org.drools.workbench.screens.scenariosimulation.client.editor.menu.MenuItemView;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.FieldItemView;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.ListGroupItemView;

@ApplicationScoped
/**
 * Class used as Provider for <i>Views</i> that has to be dynamically created
 */
public class ViewsProvider {

    @Inject
    private Instance<MenuItemView> menuItemViewInstance;

    @Inject
    private Instance<FieldItemView> fieldItemViewInstance;

    @Inject
    private Instance<ListGroupItemView> listGroupItemViewInstance;

    @Inject
    private Instance<ListEditorElementView> listEditorElementViewInstance;

    @Inject
    private Instance<PropertyEditorView> propertyEditorViewInstance;

    public MenuItemView getMenuItemView() {
        return menuItemViewInstance.get();
    }

    public FieldItemView getFieldItemView() {
        return fieldItemViewInstance.get();
    }

    public ListGroupItemView getListGroupItemView() {
        return listGroupItemViewInstance.get();
    }

    public ListEditorElementView getListEditorElementView() {
        return listEditorElementViewInstance.get();
    }

    public PropertyEditorView getPropertyEditorView() {
        return propertyEditorViewInstance.get();
    }

}
