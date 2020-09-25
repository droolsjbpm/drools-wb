/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.scenariosimulation.kogito.client.editor.strategies;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.gwt.event.shared.EventBus;
import jsinterop.base.Js;
import org.drools.workbench.scenariosimulation.kogito.marshaller.mapper.JsUtils;
import org.drools.workbench.screens.scenariosimulation.client.commands.ScenarioSimulationContext;
import org.drools.workbench.screens.scenariosimulation.client.editor.strategies.AbstractDMNDataManagementStrategy;
import org.drools.workbench.screens.scenariosimulation.client.enums.GridWidget;
import org.drools.workbench.screens.scenariosimulation.client.events.ScenarioNotificationEvent;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.TestToolsView;
import org.drools.workbench.screens.scenariosimulation.kogito.client.dmn.KogitoDMNService;
import org.drools.workbench.screens.scenariosimulation.model.typedescriptor.FactModelTuple;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.MainJs;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.callbacks.DMN12UnmarshallCallback;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDefinitions;

import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITItemDefinition;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.workbench.events.NotificationEvent;

public class KogitoDMNDataManagementStrategy extends AbstractDMNDataManagementStrategy {

    private KogitoDMNService dmnTypeService;

    public KogitoDMNDataManagementStrategy(EventBus eventBus, KogitoDMNService dmnTypeService) {
        super(eventBus);
        this.dmnTypeService = dmnTypeService;
    }

    @Override
    protected void retrieveFactModelTuple(TestToolsView.Presenter testToolsPresenter, ScenarioSimulationContext context, GridWidget gridWidget, String dmnFilePath) {
        RemoteCallback<FactModelTuple> callback = getSuccessCallback(testToolsPresenter, context, gridWidget);
        String fileName = dmnFilePath.substring(dmnFilePath.lastIndexOf('/') + 1);
        final Path dmnPath = PathFactory.newPath(fileName, dmnFilePath);
        dmnTypeService.getDMNContent(dmnPath, getDMNContentRemoteCallback(callback, dmnPath), getDMNContentErrorCallback(dmnFilePath));
    }

    protected RemoteCallback<String> getDMNContentRemoteCallback(final RemoteCallback<FactModelTuple> callback,
                                                                 final Path dmnPath) {
        return dmnContent -> {
            DMN12UnmarshallCallback dmn12UnmarshallCallback = getDMN12UnmarshallCallback(callback, dmnPath);
            MainJs.unmarshall(dmnContent, "", dmn12UnmarshallCallback);
        };
    }

    protected ErrorCallback<Object> getDMNContentErrorCallback(String dmnFilePath) {
        return (message, throwable) -> {
            eventBus.fireEvent(new ScenarioNotificationEvent(ScenarioSimulationEditorConstants.INSTANCE.dmnPathErrorDetailedLabel(dmnFilePath),
                                                             NotificationEvent.NotificationType.ERROR));
            return false;
        };
    }

    protected DMN12UnmarshallCallback getDMN12UnmarshallCallback(final RemoteCallback<FactModelTuple> callback,
                                                                 final Path dmnFilePath) {
        return dmn12 -> {
            final JSITDefinitions jsitDefinitions = Js.uncheckedCast(JsUtils.getUnwrappedElement(dmn12));

            if (jsitDefinitions.getImport() != null && !jsitDefinitions.getImport().isEmpty()) {
                final Map<String, Path> includedDMNImportsPaths = jsitDefinitions.getImport().stream()
                        .filter(jsitImport -> jsitImport.getLocationURI().endsWith(".dmn"))
                        .collect(Collectors.toMap(jsitImport -> jsitImport.getName(),
                                                  jsitImport -> PathFactory.newPath(jsitImport.getLocationURI(), dmnFilePath.toURI().replace(dmnFilePath.getFileName(), jsitImport.getLocationURI()))));


                for (Map.Entry<String, Path> importPath : includedDMNImportsPaths.entrySet()) {
                    final Map<String, JSITDefinitions> importedItemDefinitions = new HashMap<>();
                    dmnTypeService.getDMNContent(importPath.getValue(), getDMNImportContentRemoteCallback(callback, jsitDefinitions, importedItemDefinitions, importPath.getKey(), includedDMNImportsPaths.size()), null);
                }
            }
        };
    }

    protected RemoteCallback<String> getDMNImportContentRemoteCallback(final RemoteCallback<FactModelTuple> callback,
                                                                       final JSITDefinitions definitions,
                                                                       final Map<String, JSITDefinitions> importedDefinitions,
                                                                       final String importName,
                                                                       final int importsNumber) {
        return dmnContent -> {
            DMN12UnmarshallCallback dmn12UnmarshallCallback = getDMN12ImportsUnmarshallCallback(callback, definitions, importedDefinitions, importName, importsNumber);
            MainJs.unmarshall(dmnContent, "", dmn12UnmarshallCallback);
        };
    }

    protected DMN12UnmarshallCallback getDMN12ImportsUnmarshallCallback(final RemoteCallback<FactModelTuple> callback,
                                                                        final JSITDefinitions definitions,
                                                                        final Map<String, JSITDefinitions> importedDefinitions,
                                                                        final String importName,
                                                                        final int importsNumber) {
        return dmn12 -> {
            final JSITDefinitions jsitDefinitions = Js.uncheckedCast(JsUtils.getUnwrappedElement(dmn12));
            importedDefinitions.put(importName, jsitDefinitions);

            if (importsNumber == importedDefinitions.size()) {
                List<JSITItemDefinition> itemDefinitions = new ArrayList<>();

                importedDefinitions.entrySet().stream().forEach(entry -> {
                    final JSITDefinitions def = Js.uncheckedCast(entry.getValue());
                    List<JSITItemDefinition> itemDefinitionsRaw = def.getItemDefinition();
                    String prefix = entry.getKey();

                    for (int i = 0; i < itemDefinitionsRaw.size(); i++) {
                        JSITItemDefinition value = Js.uncheckedCast(itemDefinitionsRaw.get(i));
                        value.setName(prefix + "." + value.getName());
                        itemDefinitions.add(value);
                    }

                });

                final FactModelTuple factModelTuple = dmnTypeService.getFactModelTuple(definitions, itemDefinitions);
                callback.callback(factModelTuple);
            }
        };
    }

}
