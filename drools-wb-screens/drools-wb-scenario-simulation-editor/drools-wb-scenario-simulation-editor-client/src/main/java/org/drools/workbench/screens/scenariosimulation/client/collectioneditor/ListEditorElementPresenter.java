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
package org.drools.workbench.screens.scenariosimulation.client.collectioneditor;

import java.util.Map;

import javax.inject.Inject;

import com.google.gwt.dom.client.LIElement;
import org.drools.workbench.screens.scenariosimulation.client.utils.ViewsProvider;

public class ListEditorElementPresenter implements ListEditorElementView.Presenter {

    @Inject
    protected PropertyEditorPresenter propertyEditorPresenter;

    @Inject
    protected ViewsProvider viewsProvider;

    @Override
    public LIElement getPropertiesContainer(Map<String, String> propertiesMap) {
        final ListEditorElementView listEditorElementView = viewsProvider.getListEditorElementView();
        final LIElement toReturn = listEditorElementView.getPropertiesContainer();
        propertiesMap.forEach((propertyName, propertyValue) -> toReturn.appendChild(propertyEditorPresenter.getPropertyFields(propertyName, propertyValue)));
        return toReturn;
    }

}
