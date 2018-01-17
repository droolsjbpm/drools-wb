/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.testscenario.client.page.audit;

import java.util.Set;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.HTMLTableElement;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated
@Dependent
public class AuditLogTable implements IsElement {

    @DataField("items")
    private HTMLTableElement itemsContainer;

    private ManagedInstance<AuditLogTableItem> items;

    @Inject
    public AuditLogTable(final HTMLTableElement itemsContainer,
                         final ManagedInstance<AuditLogTableItem> items) {
        this.itemsContainer = itemsContainer;
        this.items = items;
    }

    public void redrawFiredRules(final Set<String> log) {
        log.stream()
                .forEach(logMessage -> {
                    final AuditLogTableItem newItem = items.get();
                    newItem.setText(logMessage);
                    itemsContainer.appendChild(newItem.getElement());
                });
    }
}
