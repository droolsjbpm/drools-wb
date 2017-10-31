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

package org.drools.workbench.screens.globals.backend.server;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.drools.workbench.screens.globals.type.GlobalResourceTypeDefinition;
import org.guvnor.common.services.project.builder.events.InvalidateDMOPackageCacheEvent;
import org.kie.workbench.common.services.backend.helpers.AbstractInvalidateDMOPackageCacheDeleteHelper;

/**
 * DeleteHelper for Globals to invalidate LRUDataModelOracleCache entries when a Global is deleted.
 */
@ApplicationScoped
public class GlobalsInvalidateDMOPackageCacheDeleteHelper extends AbstractInvalidateDMOPackageCacheDeleteHelper<GlobalResourceTypeDefinition> {

    @Inject
    public GlobalsInvalidateDMOPackageCacheDeleteHelper( final GlobalResourceTypeDefinition resourceType,
                                                         final Event<InvalidateDMOPackageCacheEvent> invalidateDMOPackageCache ) {
        super( resourceType,
               invalidateDMOPackageCache );
    }

}
