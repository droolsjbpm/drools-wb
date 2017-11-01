/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.rule.client.editor;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gwt.event.shared.EventBus;
import org.drools.workbench.models.datamodel.rule.ActionCallMethod;
import org.drools.workbench.models.datamodel.rule.ActionGlobalCollectionAdd;
import org.drools.workbench.models.datamodel.rule.ActionInsertFact;
import org.drools.workbench.models.datamodel.rule.ActionRetractFact;
import org.drools.workbench.models.datamodel.rule.ActionSetField;
import org.drools.workbench.models.datamodel.rule.CompositeFactPattern;
import org.drools.workbench.models.datamodel.rule.DSLSentence;
import org.drools.workbench.models.datamodel.rule.ExpressionFormLine;
import org.drools.workbench.models.datamodel.rule.FactPattern;
import org.drools.workbench.models.datamodel.rule.FreeFormLine;
import org.drools.workbench.models.datamodel.rule.FromAccumulateCompositeFactPattern;
import org.drools.workbench.models.datamodel.rule.FromCollectCompositeFactPattern;
import org.drools.workbench.models.datamodel.rule.FromCompositeFactPattern;
import org.drools.workbench.models.datamodel.rule.FromEntryPointFactPattern;
import org.drools.workbench.models.datamodel.rule.IAction;
import org.drools.workbench.models.datamodel.rule.IPattern;
import org.drools.workbench.screens.guided.rule.client.editor.plugin.RuleModellerActionPlugin;
import org.drools.workbench.screens.guided.rule.client.widget.ActionCallMethodWidget;
import org.drools.workbench.screens.guided.rule.client.widget.ActionInsertFactWidget;
import org.drools.workbench.screens.guided.rule.client.widget.ActionRetractFactWidget;
import org.drools.workbench.screens.guided.rule.client.widget.ActionSetFieldWidget;
import org.drools.workbench.screens.guided.rule.client.widget.CompositeFactPatternWidget;
import org.drools.workbench.screens.guided.rule.client.widget.DSLSentenceWidget;
import org.drools.workbench.screens.guided.rule.client.widget.ExpressionBuilder;
import org.drools.workbench.screens.guided.rule.client.widget.FactPatternWidget;
import org.drools.workbench.screens.guided.rule.client.widget.FreeFormLineWidget;
import org.drools.workbench.screens.guided.rule.client.widget.FromAccumulateCompositeFactPatternWidget;
import org.drools.workbench.screens.guided.rule.client.widget.FromCollectCompositeFactPatternWidget;
import org.drools.workbench.screens.guided.rule.client.widget.FromCompositeFactPatternWidget;
import org.drools.workbench.screens.guided.rule.client.widget.FromEntryPointFactPatternWidget;
import org.drools.workbench.screens.guided.rule.client.widget.GlobalCollectionAddWidget;
import org.drools.workbench.screens.guided.rule.client.widget.RuleModellerWidget;
import org.kie.soup.commons.validation.PortablePreconditions;

public class RuleModellerWidgetFactory
        implements
        ModellerWidgetFactory {

    private final Collection<RuleModellerActionPlugin> actionPlugins;

    public RuleModellerWidgetFactory() {
        this(Collections.emptyList());
    }

    public RuleModellerWidgetFactory(final Collection<RuleModellerActionPlugin> actionPlugins) {
        PortablePreconditions.checkNotNull("actionPlugins",
                                           actionPlugins);

        this.actionPlugins = actionPlugins;
    }

    public RuleModellerWidget getWidget(RuleModeller ruleModeller,
                                        EventBus eventBus,
                                        IAction action,
                                        Boolean readOnly) {
        if (action instanceof ActionCallMethod) {
            return new ActionCallMethodWidget(ruleModeller,
                                              eventBus,
                                              (ActionCallMethod) action,
                                              readOnly);
        }
        if (action instanceof ActionSetField) {
            return new ActionSetFieldWidget(ruleModeller,
                                            eventBus,
                                            (ActionSetField) action,
                                            readOnly);
        }
        if (action instanceof ActionInsertFact) {
            return new ActionInsertFactWidget(ruleModeller,
                                              eventBus,
                                              (ActionInsertFact) action,
                                              readOnly);
        }
        if (action instanceof ActionRetractFact) {
            return new ActionRetractFactWidget(ruleModeller,
                                               eventBus,
                                               (ActionRetractFact) action,
                                               readOnly);
        }
        if (action instanceof DSLSentence) {
            RuleModellerWidget w = new DSLSentenceWidget(ruleModeller,
                                                         eventBus,
                                                         (DSLSentence) action,
                                                         readOnly);
            return w;
        }
        if (action instanceof FreeFormLine) {
            return new FreeFormLineWidget(ruleModeller,
                                          eventBus,
                                          (FreeFormLine) action,
                                          readOnly);
        }
        if (action instanceof ActionGlobalCollectionAdd) {
            return new GlobalCollectionAddWidget(ruleModeller,
                                                 eventBus,
                                                 (ActionGlobalCollectionAdd) action,
                                                 readOnly);
        }

        // All hardcoded action widgets have been checked, perform a plugin lookup
        List<RuleModellerActionPlugin> matchingActionPlugins = actionPlugins.stream().filter(p -> p.accept(action)).collect(Collectors.toList());

        if (matchingActionPlugins.size() > 1) {
            throw new IllegalStateException("Ambigious " + RuleModellerActionPlugin.class.getName() + " implementations for action " + action);
        }

        if (matchingActionPlugins.size() == 1) {
            RuleModellerActionPlugin actionPlugin = matchingActionPlugins.get(0);

            RuleModellerWidget ruleModellerWidget = actionPlugin.createWidget(ruleModeller,
                                                                              eventBus,
                                                                              action,
                                                                              readOnly);
            return ruleModellerWidget;
        }

        throw new RuntimeException("I don't know what type of action is: " + action); //NON-NLS
    }

    public RuleModellerWidget getWidget(RuleModeller ruleModeller,
                                        EventBus eventBus,
                                        IPattern pattern,
                                        Boolean readOnly) {
        if (pattern instanceof FactPattern) {
            return new FactPatternWidget(ruleModeller,
                                         eventBus,
                                         pattern,
                                         true,
                                         readOnly);
        }
        if (pattern instanceof CompositeFactPattern) {
            return new CompositeFactPatternWidget(ruleModeller,
                                                  eventBus,
                                                  (CompositeFactPattern) pattern,
                                                  readOnly);
        }
        if (pattern instanceof FromAccumulateCompositeFactPattern) {
            return new FromAccumulateCompositeFactPatternWidget(ruleModeller,
                                                                eventBus,
                                                                (FromAccumulateCompositeFactPattern) pattern,
                                                                readOnly);
        }
        if (pattern instanceof FromCollectCompositeFactPattern) {
            return new FromCollectCompositeFactPatternWidget(ruleModeller,
                                                             eventBus,
                                                             (FromCollectCompositeFactPattern) pattern,
                                                             readOnly);
        }
        if (pattern instanceof FromEntryPointFactPattern) {
            return new FromEntryPointFactPatternWidget(ruleModeller,
                                                       eventBus,
                                                       (FromEntryPointFactPattern) pattern,
                                                       readOnly);
        }
        if (pattern instanceof FromCompositeFactPattern) {
            return new FromCompositeFactPatternWidget(ruleModeller,
                                                      eventBus,
                                                      (FromCompositeFactPattern) pattern,
                                                      readOnly);
        }
        if (pattern instanceof DSLSentence) {
            return new DSLSentenceWidget(ruleModeller,
                                         eventBus,
                                         (DSLSentence) pattern,
                                         readOnly);
        }
        if (pattern instanceof FreeFormLine) {
            return new FreeFormLineWidget(ruleModeller,
                                          eventBus,
                                          (FreeFormLine) pattern,
                                          readOnly);
        }
        if (pattern instanceof ExpressionFormLine) {
            return new ExpressionBuilder(ruleModeller,
                                         eventBus,
                                         (ExpressionFormLine) pattern,
                                         readOnly);
        }
        throw new RuntimeException("I don't know what type of pattern is: " + pattern);
    }

    public boolean isTemplate() {
        return false;
    }
}
