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
package org.drools.workbench.screens.scenariosimulation.backend.server;

import javax.enterprise.context.ApplicationScoped;

import org.drools.scenariosimulation.api.model.ScenarioSimulationModel;
import org.drools.scenariosimulation.api.model.SimulationRunMetadata;
import org.drools.workbench.screens.scenariosimulation.backend.server.downloadreport.ScenarioCsvDownloadReport;
import org.drools.workbench.screens.scenariosimulation.service.RunnerReportService;
import org.jboss.errai.bus.server.annotations.Service;

@Service
@ApplicationScoped
public class RunnerReportServiceImpl implements RunnerReportService {

    protected ScenarioCsvDownloadReport scenarioCsvDownloadReport = new ScenarioCsvDownloadReport();

    @Override
    public Object getReport(SimulationRunMetadata scenarioRunMetadata, ScenarioSimulationModel.Type modelType) {
        return scenarioCsvDownloadReport.getReport(scenarioRunMetadata, modelType);
    }
}
