/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.dtable.client.resources.css;

import org.kie.workbench.common.widgets.decoratedgrid.client.resources.GridResources;

/**
 * General Decision Table CSS
 */
public interface CssResources
        extends
        GridResources.GridStyle {

    String metaColumn();

    String conditionColumn();

    String actionColumn();

    String patternConditionSectionHeader();

    String columnLabelHidden();

    String openedAccordion();

    String ruleInheritance();

    String ruleInheritanceLabel();
}
