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
package org.drools.workbench.screens.scenariosimulation.client.popup;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.dom.client.ParagraphElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.views.pfly.resources.i18n.Constants;
import org.uberfire.mvp.Command;

@Dependent
@Templated
public class DeletePopupView extends ScenarioConfirmationPopupView implements DeletePopup {

    @Inject
    @DataField("text-danger")
    protected ParagraphElement textDanger;

    @PostConstruct
    public void init() {
        cancelButton.setText(translationService.getTranslation(Constants.ConfirmPopup_Cancel));
    }

    @Override
    public void show(final String mainTitleText,
                     final String mainQuestionText,
                     final String text1Text,
                     final String textQuestionText,
                     final String textDangerText,
                     final String okDeleteButtonText,
                     final Command okDeleteCommand) {
        textDanger.setInnerText(textDangerText);
        super.show(mainTitleText, mainQuestionText, text1Text, textQuestionText,
                   okDeleteButtonText,
                   okDeleteCommand);
    }
}
