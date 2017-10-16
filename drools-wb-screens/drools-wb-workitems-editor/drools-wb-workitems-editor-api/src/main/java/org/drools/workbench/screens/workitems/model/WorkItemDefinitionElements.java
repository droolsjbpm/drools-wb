/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.workbench.screens.workitems.model;

import java.util.Map;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.soup.commons.validation.PortablePreconditions;

@Portable
public class WorkItemDefinitionElements {

    private Map<String, String> workItemDefinitionElements;

    public WorkItemDefinitionElements() {
    }

    public WorkItemDefinitionElements(final Map<String, String> workItemDefinitionElements) {
        this.workItemDefinitionElements = PortablePreconditions.checkNotNull("workItemDefinitionElements",
                                                                             workItemDefinitionElements);
    }

    public Map<String, String> getDefinitionElements() {
        return this.workItemDefinitionElements;
    }
}
