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

package org.drools.workbench.screens.guided.rule.client.type;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import org.drools.workbench.screens.guided.rule.client.resources.GuidedRuleEditorResources;
import org.drools.workbench.screens.guided.rule.type.GuidedRuleDRLResourceTypeDefinition;
import org.guvnor.common.services.project.categories.Decision;
import org.uberfire.client.workbench.type.ClientResourceType;

@ApplicationScoped
public class GuidedRuleDRLResourceType
        extends GuidedRuleDRLResourceTypeDefinition
        implements ClientResourceType {

    private static final Image IMAGE = new Image(GuidedRuleEditorResources.INSTANCE.images().typeGuidedRule());

    public GuidedRuleDRLResourceType() {
    }

    @Inject
    public GuidedRuleDRLResourceType(final Decision category) {
        super(category);
    }

    @Override
    public IsWidget getIcon() {
        return IMAGE;
    }

    @Override
    public String getDescription() {
        String desc = GuidedRuleEditorResources.CONSTANTS.guidedRuleDRLResourceTypeDescription();
        if (desc == null || desc.isEmpty()) {
            return super.getDescription();
        }
        return desc;
    }
}
