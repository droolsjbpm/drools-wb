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

package org.drools.workbench.screens.scenariosimulation.client.rightpanel;

import java.util.Optional;
import java.util.function.Supplier;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.shared.EventBus;
import org.drools.scenariosimulation.api.model.ScenarioSimulationModel;
import org.drools.scenariosimulation.api.model.Settings;
import org.drools.workbench.screens.scenariosimulation.client.dropdown.SettingsScenarioSimulationDropdown;
import org.drools.workbench.screens.scenariosimulation.client.events.ValidateScenarioEvent;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.kie.workbench.common.widgets.client.assets.dropdown.KieAssetsDropdownItem;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.mvp.Command;

import static org.drools.workbench.screens.scenariosimulation.client.rightpanel.SettingsPresenter.DEFAULT_PREFERRED_WIDHT;
import static org.drools.workbench.screens.scenariosimulation.client.rightpanel.SettingsPresenter.IDENTIFIER;

@ApplicationScoped
@WorkbenchScreen(identifier = IDENTIFIER, preferredWidth = DEFAULT_PREFERRED_WIDHT)
public class SettingsPresenter extends AbstractSubDockPresenter<SettingsView> implements SettingsView.Presenter {

    public static final int DEFAULT_PREFERRED_WIDHT = 300;

    public static final String IDENTIFIER = "org.drools.scenariosimulation.Settings";

    protected EventBus eventBus;

    protected Settings settings;

    protected Command saveCommand;

    protected SettingsScenarioSimulationDropdown settingsScenarioSimulationDropdown;

    public SettingsPresenter() {
        //Zero argument constructor for CDI
        title = ScenarioSimulationEditorConstants.INSTANCE.settings();
    }

    @Inject
    public SettingsPresenter(@Named(SettingsScenarioSimulationDropdown.BEAN_NAME) SettingsScenarioSimulationDropdown settingsScenarioSimulationDropdown,
                             SettingsView view) {
        super(view);
        title = ScenarioSimulationEditorConstants.INSTANCE.settings();
        this.settingsScenarioSimulationDropdown = settingsScenarioSimulationDropdown;
    }

    @PostConstruct
    public void init() {
        view.getSkipFromBuildLabel().setInnerText(ScenarioSimulationEditorConstants.INSTANCE.skipSimulation());
        view.setupDropdown(settingsScenarioSimulationDropdown.asWidget().asWidget().getElement());
        settingsScenarioSimulationDropdown.init();
    }

    @Override
    public void setScenarioType(ScenarioSimulationModel.Type scenarioType, Settings settings, String fileName) {
        this.settings = settings;
        view.getScenarioType().setInnerText(scenarioType.name());
        view.getFileName().setValue(fileName);
        view.getSkipFromBuild().setChecked(settings.isSkipFromBuild());
        switch (scenarioType) {
            case RULE:
                setRuleSettings(settings);
                break;
            case DMN:
                setDMNSettings(settings);
                break;
            default:
                // nop
        }
    }

    @Override
    public void reset() {
        view.reset();
        settingsScenarioSimulationDropdown.clear();
    }

    @Override
    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public SettingsView getView() {
        return view;
    }

    protected void setRuleSettings(Settings settings) {
        view.getDmnSettings().getStyle().setDisplay(Style.Display.NONE);
        view.getRuleSettings().getStyle().setDisplay(Style.Display.INLINE);
        view.getDmoSession().setValue(Optional.ofNullable(settings.getDmoSession()).orElse(""));
        view.getRuleFlowGroup().setValue(Optional.ofNullable(settings.getRuleFlowGroup()).orElse(""));
        view.getStateless().setChecked(settings.isStateless());
    }

    protected void setDMNSettings(Settings settings) {
        view.getRuleSettings().getStyle().setDisplay(Style.Display.NONE);
        view.getDmnSettings().getStyle().setDisplay(Style.Display.INLINE);
        view.getDmnName().setValue(Optional.ofNullable(settings.getDmnName()).orElse(""));
        view.getDmnNamespace().setValue(Optional.ofNullable(settings.getDmnNamespace()).orElse(""));
        view.getDmnFilePathErrorLabel().getStyle().setDisplay(Style.Display.NONE);
        view.getDmnFilePathErrorLabel().setInnerText("");
        settingsScenarioSimulationDropdown.registerOnMissingValueHandler(this::setDmnErrorPath);
        settingsScenarioSimulationDropdown.registerOnChangeHandler(this::validateScenario);
        settingsScenarioSimulationDropdown.loadAssets(settings.getDmnFilePath());
    }

    @Override
    public void syncDmoSession() {
        settings.setDmoSession(getCleanValue(() -> view.getDmoSession().getValue()));
    }

    @Override
    public void syncRuleFlowGroup() {
        settings.setRuleFlowGroup(getCleanValue(() -> view.getRuleFlowGroup().getValue()));
    }

    @Override
    public void syncStateless() {
        settings.setStateless(view.getStateless().isChecked());
    }

    @Override
    public void syncDmnFilePath() {
        String value = settingsScenarioSimulationDropdown.getValue().map(KieAssetsDropdownItem::getValue).orElse("");
        settings.setDmnFilePath(getCleanValue(() -> value));
    }

    @Override
    public void syncSkipFromBuild() {
        settings.setSkipFromBuild(view.getSkipFromBuild().isChecked());
    }

    /**
     * It sets an error message to <code>dmnPathErrorLabel</code> span element
     * This method should be called in case of INVALID DMN file path.
     */
    protected void setDmnErrorPath() {
        view.getDmnFilePathErrorLabel().getStyle().setDisplay(Style.Display.INLINE);
        view.getDmnFilePathErrorLabel().setInnerText(
                ScenarioSimulationEditorConstants.INSTANCE.dmnPathErrorLabel(settings.getDmnFilePath()));
    }

    /**
     * It checks if a user selected DMN path is valid or not. If valid, it clears the <code>dmnPathErrorLabel</code>
     * span element. If not valid, the otherwise.
     * This method should be called everytime a value is selected in <code>{@link SettingsScenarioSimulationDropdown}</code> widget
     */
    protected void validateScenario() {
        final Optional<KieAssetsDropdownItem> value = settingsScenarioSimulationDropdown.getValue();
        String selectedPath = value.map(KieAssetsDropdownItem::getValue).orElse(null);
        boolean isValid = selectedPath != null && !selectedPath.isEmpty();
        if (!isValid) {
            view.getDmnFilePathErrorLabel().getStyle().setDisplay(Style.Display.INLINE);
            view.getDmnFilePathErrorLabel().setInnerText(ScenarioSimulationEditorConstants.INSTANCE.chooseValidDMNAsset());
        } else {
            this.syncDmnFilePath();
            view.getDmnFilePathErrorLabel().getStyle().setDisplay(Style.Display.NONE);
            view.getDmnFilePathErrorLabel().setInnerText("");
            eventBus.fireEvent(new ValidateScenarioEvent());
        }
    }

    private String getCleanValue(Supplier<String> supplier) {
        String rawValue = supplier.get();
        return "".equals(rawValue) ? null : rawValue;
    }
}
