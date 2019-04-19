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

package org.drools.workbench.screens.scenariosimulation.service;

import java.util.List;

import org.drools.workbench.screens.scenariosimulation.model.ScenarioSimulationModel;
import org.drools.workbench.screens.scenariosimulation.model.ScenarioSimulationModelContent;
import org.drools.workbench.screens.scenariosimulation.model.ScenarioWithIndex;
import org.drools.workbench.screens.scenariosimulation.model.SimulationDescriptor;
import org.drools.workbench.screens.scenariosimulation.model.TestRunResult;
import org.drools.workbench.screens.scenariosimulation.model.SimulationRunResult;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.jboss.errai.bus.server.annotations.Remote;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.service.support.SupportsCopy;
import org.uberfire.ext.editor.commons.service.support.SupportsCreate;
import org.uberfire.ext.editor.commons.service.support.SupportsDelete;
import org.uberfire.ext.editor.commons.service.support.SupportsRead;
import org.uberfire.ext.editor.commons.service.support.SupportsSaveAndRename;

@Remote
public interface ScenarioSimulationService
        extends
        SupportsCreate<ScenarioSimulationModel>,
        SupportsRead<ScenarioSimulationModel>,
        SupportsSaveAndRename<ScenarioSimulationModel, Metadata>,
        SupportsDelete,
        SupportsCopy {

    ScenarioSimulationModelContent loadContent(final Path path);

    SimulationRunResult runScenario(Path path,
                                    SimulationDescriptor simulationDescriptor,
                                    List<ScenarioWithIndex> scenarios);

    Path create(final Path context,
                final String fileName,
                final ScenarioSimulationModel content,
                final String comment,
                final ScenarioSimulationModel.Type type,
                final String value);
}
