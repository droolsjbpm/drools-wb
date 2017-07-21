/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.screens.guided.rule.client.editor;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.models.datamodel.rule.ActionCallMethod;
import org.drools.workbench.models.datamodel.rule.ActionGlobalCollectionAdd;
import org.drools.workbench.models.datamodel.rule.ActionInsertFact;
import org.drools.workbench.models.datamodel.rule.ActionInsertLogicalFact;
import org.drools.workbench.models.datamodel.rule.ActionRetractFact;
import org.drools.workbench.models.datamodel.rule.ActionSetField;
import org.drools.workbench.models.datamodel.rule.ActionUpdateField;
import org.drools.workbench.models.datamodel.rule.DSLSentence;
import org.drools.workbench.models.datamodel.rule.FreeFormLine;
import org.drools.workbench.models.datamodel.rule.IAction;
import org.drools.workbench.models.datamodel.rule.RuleModel;
import org.drools.workbench.screens.guided.rule.client.editor.plugin.RuleModellerActionPlugin;
import org.drools.workbench.screens.guided.rule.client.resources.GuidedRuleEditorResources;
import org.gwtbootstrap3.client.ui.CheckBox;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.ModalBody;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.uberfire.ext.widgets.common.client.common.InfoPopup;
import org.uberfire.ext.widgets.common.client.common.popups.footers.ModalFooterOKCancelButtons;

/**
 * Pop-up for adding Actions to the (RuleModeller) guided editor
 */
public class RuleModellerActionSelectorPopup extends AbstractRuleModellerSelectorPopup {

    private Collection<RuleModellerActionPlugin> actionPlugins;

    private final Command okCommand = new Command() {
        @Override
        public void execute() {
            selectSomething();
        }
    };

    private final Command cancelCommand = new Command() {
        @Override
        public void execute() {
            hide();
        }
    };

    public RuleModellerActionSelectorPopup(final RuleModel model,
                                           final RuleModeller ruleModeller,
                                           final Collection<RuleModellerActionPlugin> actionPlugins,
                                           final Integer position,
                                           final AsyncPackageDataModelOracle oracle) {
        super(model,
              ruleModeller,
              position,
              oracle);

        this.actionPlugins = actionPlugins;

        this.add(new ModalBody() {{
            add(getContent());
        }});

        add(new ModalFooterOKCancelButtons(okCommand,
                                           cancelCommand));
    }

    @Override
    protected String getPopupTitle() {
        return GuidedRuleEditorResources.CONSTANTS.AddANewAction();
    }

    @Override
    public Widget getContent() {
        if (position == null) {
            positionCbo.addItem(GuidedRuleEditorResources.CONSTANTS.Bottom(),
                                String.valueOf(this.model.rhs.length));
            positionCbo.addItem(GuidedRuleEditorResources.CONSTANTS.Top(),
                                "0");
            for (int i = 1; i < model.rhs.length; i++) {
                positionCbo.addItem(GuidedRuleEditorResources.CONSTANTS.Line0(i),
                                    String.valueOf(i));
            }
        } else {
            //if position is fixed, we just add one element to the drop down.
            positionCbo.addItem(String.valueOf(position));
            positionCbo.setSelectedIndex(0);
        }

        if (oracle.getDSLConditions().size() == 0 && oracle.getFactTypes().length == 0) {
            layoutPanel.addRow(new HTML("<div class='highlight'>" + GuidedRuleEditorResources.CONSTANTS.NoModelTip() + "</div>"));
        }

        //only show the drop down if we are not using fixed position.
        if (position == null) {
            HorizontalPanel hp0 = new HorizontalPanel();
            hp0.add(new HTML(GuidedRuleEditorResources.CONSTANTS.PositionColon()));
            hp0.add(positionCbo);
            hp0.add(new InfoPopup(GuidedRuleEditorResources.CONSTANTS.PositionColon(),
                                  GuidedRuleEditorResources.CONSTANTS.ActionPositionExplanation()));
            layoutPanel.addRow(hp0);
            layoutPanel.addRow(new HTML("<hr/>"));
        }

        choices = makeChoicesListBox();
        choicesPanel.add(choices);
        layoutPanel.addRow(choicesPanel);

        //DSL might be prohibited (e.g. editing a DRL file. Only DSLR files can contain DSL)
        if (ruleModeller.isDSLEnabled()) {
            CheckBox chkOnlyDisplayDSLConditions = new CheckBox();
            chkOnlyDisplayDSLConditions.setText(GuidedRuleEditorResources.CONSTANTS.OnlyDisplayDSLActions());
            chkOnlyDisplayDSLConditions.setValue(onlyShowDSLStatements);
            chkOnlyDisplayDSLConditions.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

                public void onValueChange(ValueChangeEvent<Boolean> event) {
                    onlyShowDSLStatements = event.getValue();
                    choicesPanel.setWidget(makeChoicesListBox());
                }
            });
            layoutPanel.addRow(chkOnlyDisplayDSLConditions);
        }

        return layoutPanel;
    }

    @Override
    public void show() {
        super.show();
        choices.setFocus(true);
    }

    private ListBox makeChoicesListBox() {
        choices = new ListBox(true);
        choices.setPixelSize(getChoicesWidth(),
                             getChoicesHeight());

        choices.addKeyUpHandler(new KeyUpHandler() {
            public void onKeyUp(com.google.gwt.event.dom.client.KeyUpEvent event) {
                if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
                    selectSomething();
                }
            }
        });

        addDSLSentences();
        if (!onlyShowDSLStatements) {
            addUpdateNotModify();
            addGlobals();
            addRetractions();
            addModifies();
            addInsertions();
            addLogicalInsertions();
            addGlobalCollections();
            addFreeFormDRL();
            addCallMethodOn();
            addCustomActionPlugins();
        }

        return choices;
    }

    // Add DSL sentences
    void addDSLSentences() {
        //DSL might be prohibited (e.g. editing a DRL file. Only DSLR files can contain DSL)
        if (!ruleModeller.isDSLEnabled()) {
            return;
        }

        for (final DSLSentence sen : oracle.getDSLActions()) {
            final String sentence = sen.toString();
            final String key = "DSL" + sentence;
            choices.addItem(sentence,
                            key);
            cmds.put(key,
                     new Command() {

                         public void execute() {
                             addNewDSLRhs(sen,
                                          Integer.parseInt(positionCbo.getValue(positionCbo.getSelectedIndex())));
                             hide();
                         }
                     });
        }
    }

    //Add update, not modify
    void addUpdateNotModify() {
        List<String> vars = model.getLHSPatternVariables();
        if (vars.size() == 0) {
            return;
        }

        choices.addItem(SECTION_SEPARATOR);
        for (Iterator<String> iter = vars.iterator(); iter.hasNext(); ) {
            final String v = iter.next();

            choices.addItem(GuidedRuleEditorResources.CONSTANTS.ChangeFieldValuesOf0(v),
                            "VAR" + v);
            cmds.put("VAR" + v,
                     new Command() {

                         public void execute() {
                             addActionSetField(v,
                                               Integer.parseInt(positionCbo.getValue(positionCbo.getSelectedIndex())));
                             hide();
                         }
                     });
        }
    }

    //Add Globals
    void addGlobals() {
        String[] globals = oracle.getGlobalVariables();
        if (globals.length == 0) {
            return;
        }
        choices.addItem(SECTION_SEPARATOR);
        for (int i = 0; i < globals.length; i++) {
            final String v = globals[i];
            choices.addItem(GuidedRuleEditorResources.CONSTANTS.ChangeFieldValuesOf0(v),
                            "GLOBVAR" + v);
            cmds.put("GLOBVAR" + v,
                     new Command() {

                         public void execute() {
                             addActionSetField(v,
                                               Integer.parseInt(positionCbo.getValue(positionCbo.getSelectedIndex())));
                             hide();
                         }
                     });
        }
    }

    //Add Retractions
    void addRetractions() {
        List<String> vars = model.getLHSPatternVariables();
        if (vars.size() == 0) {
            return;
        }
        choices.addItem(SECTION_SEPARATOR);
        for (Iterator<String> iter = vars.iterator(); iter.hasNext(); ) {
            final String v = iter.next();
            choices.addItem(GuidedRuleEditorResources.CONSTANTS.Delete0(v),
                            "RET" + v);
            cmds.put("RET" + v,
                     new Command() {

                         public void execute() {
                             addRetract(v,
                                        Integer.parseInt(positionCbo.getValue(positionCbo.getSelectedIndex())));
                             hide();
                         }
                     });
        }
    }

    //Add Modifies
    void addModifies() {
        List<String> vars = model.getAllLHSVariables();
        if (vars.size() == 0) {
            return;
        }
        choices.addItem(SECTION_SEPARATOR);
        for (Iterator<String> iter = vars.iterator(); iter.hasNext(); ) {
            final String v = iter.next();

            choices.addItem(GuidedRuleEditorResources.CONSTANTS.Modify0(v),
                            "MOD" + v);
            cmds.put("MOD" + v,
                     new Command() {

                         public void execute() {
                             addModify(v,
                                       Integer.parseInt(positionCbo.getValue(positionCbo.getSelectedIndex())));
                             hide();
                         }
                     });
        }
    }

    //Add insertions
    void addInsertions() {
        if (oracle.getFactTypes().length == 0) {
            return;
        }
        choices.addItem(SECTION_SEPARATOR);
        for (int i = 0; i < oracle.getFactTypes().length; i++) {
            final String item = oracle.getFactTypes()[i];
            choices.addItem(GuidedRuleEditorResources.CONSTANTS.InsertFact0(item),
                            "INS" + item);
            cmds.put("INS" + item,
                     new Command() {

                         public void execute() {
                             model.addRhsItem(new ActionInsertFact(item),
                                              Integer.parseInt(positionCbo.getValue(positionCbo.getSelectedIndex())));
                             hide();
                         }
                     });
        }
    }

    //Add logical insertions
    void addLogicalInsertions() {
        if (oracle.getFactTypes().length == 0) {
            return;
        }
        choices.addItem(SECTION_SEPARATOR);
        for (int i = 0; i < oracle.getFactTypes().length; i++) {
            final String item = oracle.getFactTypes()[i];
            choices.addItem(GuidedRuleEditorResources.CONSTANTS.LogicallyInsertFact0(item),
                            "LINS" + item);
            cmds.put("LINS" + item,
                     new Command() {

                         public void execute() {
                             model.addRhsItem(new ActionInsertLogicalFact(item),
                                              Integer.parseInt(positionCbo.getValue(positionCbo.getSelectedIndex())));
                             hide();
                         }
                     });
        }
    }

    //Add global collections
    void addGlobalCollections() {
        List<String> vars = model.getLHSBoundFacts();
        if (vars.size() == 0) {
            return;
        }
        if (oracle.getGlobalCollections().length == 0) {
            return;
        }
        choices.addItem(SECTION_SEPARATOR);
        for (String bf : vars) {
            for (int i = 0; i < oracle.getGlobalCollections().length; i++) {
                final String glob = oracle.getGlobalCollections()[i];
                final String var = bf;
                choices.addItem(GuidedRuleEditorResources.CONSTANTS.Append0ToList1(var,
                                                                                   glob),
                                "GLOBCOL" + glob + var);
                cmds.put("GLOBCOL" + glob + var,
                         new Command() {

                             public void execute() {
                                 ActionGlobalCollectionAdd gca = new ActionGlobalCollectionAdd();
                                 gca.setGlobalName(glob);
                                 gca.setFactName(var);
                                 model.addRhsItem(gca,
                                                  Integer.parseInt(positionCbo.getValue(positionCbo.getSelectedIndex())));
                                 hide();
                             }
                         });
            }
        }
    }

    //Add free-form DRL
    void addFreeFormDRL() {
        choices.addItem(SECTION_SEPARATOR);
        choices.addItem(GuidedRuleEditorResources.CONSTANTS.AddFreeFormDrl(),
                        "FF");
        cmds.put("FF",
                 new Command() {

                     public void execute() {
                         model.addRhsItem(new FreeFormLine(),
                                          Integer.parseInt(positionCbo.getValue(positionCbo.getSelectedIndex())));
                         hide();
                     }
                 });
    }

    //Add "Call method on.." options
    void addCallMethodOn() {
        List<String> lhsVars = model.getAllLHSVariables();
        List<String> rhsVars = model.getRHSBoundFacts();
        String[] globals = oracle.getGlobalVariables();

        //Add globals
        if (globals.length > 0) {
            choices.addItem(SECTION_SEPARATOR);
        }
        for (int i = 0; i < globals.length; i++) {
            final String v = globals[i];
            choices.addItem(GuidedRuleEditorResources.CONSTANTS.CallMethodOn0(v),
                            "GLOBCALL" + v);
            cmds.put("GLOBCALL" + v,
                     new Command() {

                         public void execute() {
                             addCallMethod(v,
                                           Integer.parseInt(positionCbo.getValue(positionCbo.getSelectedIndex())));
                             hide();
                         }
                     });
        }

        //Method calls
        if (lhsVars.size() > 0) {
            choices.addItem(SECTION_SEPARATOR);
        }
        for (Iterator<String> iter = lhsVars.iterator(); iter.hasNext(); ) {
            final String v = iter.next();

            choices.addItem(GuidedRuleEditorResources.CONSTANTS.CallMethodOn0(v),
                            "CALL" + v);
            cmds.put("CALL" + v,
                     new Command() {

                         public void execute() {
                             addCallMethod(v,
                                           Integer.parseInt(positionCbo.getValue(positionCbo.getSelectedIndex())));
                             hide();
                         }
                     });
        }

        //Update, not modify
        if (rhsVars.size() > 0) {
            choices.addItem(SECTION_SEPARATOR);
        }
        for (Iterator<String> iter = rhsVars.iterator(); iter.hasNext(); ) {
            final String v = iter.next();

            choices.addItem(GuidedRuleEditorResources.CONSTANTS.CallMethodOn0(v),
                            "CALL" + v);
            cmds.put("CALL" + v,
                     new Command() {

                         public void execute() {
                             addCallMethod(v,
                                           Integer.parseInt(positionCbo.getValue(positionCbo.getSelectedIndex())));
                             hide();
                         }
                     });
        }
    }

    private void addCustomActionPlugins() {
        if (actionPlugins != null) {
            for (RuleModellerActionPlugin actionPlugin : actionPlugins) {

                final IAction iAction = actionPlugin.createIAction(ruleModeller);
                actionPlugin.addPluginToActionList(ruleModeller,
                                                   () -> {
                                                       choices.addItem(actionPlugin.getActionAddDescription(),
                                                                       actionPlugin.getId());
                                                       cmds.put(actionPlugin.getId(),
                                                                () -> {
                                                                    model.addRhsItem(iAction,
                                                                                     Integer.parseInt(positionCbo.getValue(positionCbo.getSelectedIndex())));
                                                                    hide();
                                                                });
                                                   });
            }
        }
    }

    private void addNewDSLRhs(DSLSentence sentence,
                              int position) {
        this.model.addRhsItem(sentence.copy(),
                              position);
    }

    private void addRetract(String var,
                            int position) {
        this.model.addRhsItem(new ActionRetractFact(var),
                              position);
    }

    private void addActionSetField(String itemText,
                                   int position) {
        this.model.addRhsItem(new ActionSetField(itemText),
                              position);
    }

    private void addCallMethod(String itemText,
                               int position) {
        this.model.addRhsItem(new ActionCallMethod(itemText),
                              position);
    }

    private void addModify(String itemText,
                           int position) {
        this.model.addRhsItem(new ActionUpdateField(itemText),
                              position);
    }
}
