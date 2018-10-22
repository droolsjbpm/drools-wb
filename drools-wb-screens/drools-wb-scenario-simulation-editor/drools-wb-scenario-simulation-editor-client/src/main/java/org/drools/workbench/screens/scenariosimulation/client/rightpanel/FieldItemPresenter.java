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

package org.drools.workbench.screens.scenariosimulation.client.rightpanel;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.dom.client.LIElement;
import org.drools.workbench.screens.scenariosimulation.client.utils.ViewsProvider;

@Dependent
public class FieldItemPresenter implements FieldItemView.Presenter {

    @Inject
    ViewsProvider viewsProvider;

    ListGroupItemView.Presenter listGroupItemPresenter;

    List<FieldItemView> fieldItemViews = new ArrayList<>();

    @Override
    public LIElement getLIElement(String parentPath, String factName, String fieldName, String className) {
        FieldItemView fieldItemView = viewsProvider.getFieldItemView();
        fieldItemView.setFieldData(parentPath, factName, fieldName, className);
        fieldItemView.setPresenter(this);
        fieldItemViews.add(fieldItemView);
        LIElement toReturn = fieldItemView.getLIElement();
        return toReturn;
    }

    @Override
    public void setListGroupItemPresenter(ListGroupItemView.Presenter listGroupItemPresenter) {
        this.listGroupItemPresenter = listGroupItemPresenter;
    }

    public void onFieldElementClick(FieldItemView selected) {
        listGroupItemPresenter.onSelectedElement(selected);
        fieldItemViews.stream().filter(fieldItemView -> !fieldItemView.equals(selected)).forEach(FieldItemView::unselect);
    }

    @Override
    public void unselectAll() {
        fieldItemViews.forEach(FieldItemView::unselect);
    }
}
