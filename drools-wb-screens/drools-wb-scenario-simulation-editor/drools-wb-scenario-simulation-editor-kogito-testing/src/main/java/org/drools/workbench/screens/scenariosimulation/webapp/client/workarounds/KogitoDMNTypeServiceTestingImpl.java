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
package org.drools.workbench.screens.scenariosimulation.webapp.client.workarounds;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.drools.scenariosimulation.api.model.Settings;
import org.drools.workbench.screens.scenariosimulation.kogito.client.fakes.AbstractKogitoDMNTypeService;
import org.drools.workbench.screens.scenariosimulation.model.typedescriptor.FactModelTuple;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.kogito.webapp.base.client.workarounds.TestingVFSService;
import org.uberfire.backend.vfs.Path;

@ApplicationScoped
public class KogitoDMNTypeServiceTestingImpl extends AbstractKogitoDMNTypeService {

    @Inject
    private TestingVFSService testingVFSService;

    @Override
    public FactModelTuple retrieveFactModelTuple(Path path, String dmnPath) {
        String dmnContent = getDMNContent(path);
        return getFactModelTuple(dmnContent);
    }

    @Override
    public void initializeNameAndNamespace(Settings settings, Path path, String dmnPath) {

    }

    public String getDMNContent(final Path path) {
        return testingVFSService.loadFile(path);
    }

    public void getDMNContent(final Path path, final RemoteCallback<String> remoteCallback, final ErrorCallback<Object> errorCallback) {
        testingVFSService.loadFile(path, remoteCallback, errorCallback);
    }

}
