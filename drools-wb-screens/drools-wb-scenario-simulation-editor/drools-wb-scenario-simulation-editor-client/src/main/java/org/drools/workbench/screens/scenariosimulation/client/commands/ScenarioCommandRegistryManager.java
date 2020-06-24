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
package org.drools.workbench.screens.scenariosimulation.client.commands;

import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.appformer.client.stateControl.registry.DefaultRegistry;
import org.appformer.client.stateControl.registry.Registry;
import org.drools.workbench.screens.scenariosimulation.client.commands.actualcommands.AbstractScenarioGridCommand;
import org.kie.workbench.common.command.client.CommandResult;
import org.kie.workbench.common.command.client.CommandResultBuilder;
import org.kie.workbench.common.command.client.impl.CommandResultImpl;

/**
 * This class is used to store <code>Queue</code>es of <b>executed/undone</b> <code>Command</code>s
 */
@Dependent
public class ScenarioCommandRegistryManager {

    @Inject
    protected Registry<AbstractScenarioGridCommand> doneCommands;
    @Inject
    protected DefaultRegistry<AbstractScenarioGridCommand> undoneCommands;

    /**
     * Method to register the status as it was soon before the command execution,
     * to be used for undo/redo
     * @param context
     * @param command
     */
    public void register(ScenarioSimulationContext context, AbstractScenarioGridCommand command) {
        doneCommands.register(command);
        undoneCommands.clear();
        setUndoRedoButtonStatus(context);
    }

    /**
     * Calls <b>undo</b> on the last executed <code>Command</code>
     * @param scenarioSimulationContext
     * @throws NoSuchElementException
     */
    public CommandResult<ScenarioSimulationViolation> undo(ScenarioSimulationContext scenarioSimulationContext) {
        CommandResult<ScenarioSimulationViolation> toReturn;
        if (!doneCommands.isEmpty()) {
            // to restore to implement tab switching
            final Optional<CommandResult<ScenarioSimulationViolation>> optionalPreexecuted = commonUndoRedoPreexecution(scenarioSimulationContext, doneCommands.peek());
            if (optionalPreexecuted.isPresent()) {
                return optionalPreexecuted.get();
            } else {
                final AbstractScenarioGridCommand toUndo = doneCommands.pop();
                toReturn = commonUndoRedoOperation(scenarioSimulationContext, toUndo, true);
                if (Objects.equals(CommandResultBuilder.SUCCESS, toReturn)) {
                    undoneCommands.register(toUndo);
                }
            }
        } else {
            toReturn = new CommandResultImpl<>(CommandResult.Type.WARNING, Collections.singletonList(new ScenarioSimulationViolation("No commands to undo")));
        }
        setUndoRedoButtonStatus(scenarioSimulationContext);
        return toReturn;
    }

    /**
     * Re-execute the last <b>undone</b> <code>Command</code>
     * @param scenarioSimulationContext
     * @throws NoSuchElementException
     */
    public CommandResult<ScenarioSimulationViolation> redo(ScenarioSimulationContext scenarioSimulationContext) {
        CommandResult<ScenarioSimulationViolation> toReturn;
        if (!undoneCommands.isEmpty()) {
            // to restore to implement tab switching
            final Optional<CommandResult<ScenarioSimulationViolation>> optionalPreexecuted = commonUndoRedoPreexecution(scenarioSimulationContext, undoneCommands.peek());
            if (optionalPreexecuted.isPresent()) {
                return optionalPreexecuted.get();
            } else {
                final AbstractScenarioGridCommand toRedo = undoneCommands.pop();
                toReturn = commonUndoRedoOperation(scenarioSimulationContext, toRedo, false);
                if (Objects.equals(CommandResultBuilder.SUCCESS, toReturn)) {
                    doneCommands.register(toRedo);
                }
            }
        } else {
            toReturn = new CommandResultImpl<>(CommandResult.Type.WARNING, Collections.singletonList(new ScenarioSimulationViolation("No commands to redo")));
        }
        setUndoRedoButtonStatus(scenarioSimulationContext);
        return toReturn;
    }

    /**
     * Method called soon before actual <b>undo</b> and <b>redo</b> operations to preliminary execute a tab switch <b>without</b>
     * altering the call stack.
     * If the command change the status of a not shown grid, this switches the tab and returns without removing now executing the actual command.
     * @param scenarioSimulationContext
     * @param command
     */
    protected Optional<CommandResult<ScenarioSimulationViolation>> commonUndoRedoPreexecution(final ScenarioSimulationContext scenarioSimulationContext, final AbstractScenarioGridCommand command) {
        return command.commonUndoRedoPreexecution(scenarioSimulationContext);
    }

    /**
     * Common method called by <b>undo</b> and <b>redo</b> events
     * @param scenarioSimulationContext
     * @param command
     * @param isUndo
     * @return
     */
    protected CommandResult<ScenarioSimulationViolation> commonUndoRedoOperation(final ScenarioSimulationContext scenarioSimulationContext, final AbstractScenarioGridCommand command, boolean isUndo) {
        CommandResult<ScenarioSimulationViolation> toReturn;
        if (isUndo) {
            toReturn = command.undo(scenarioSimulationContext);
        } else {
            toReturn = command.redo(scenarioSimulationContext);
        }
        return toReturn;
    }

    protected void setUndoRedoButtonStatus(final ScenarioSimulationContext scenarioSimulationContext) {
        scenarioSimulationContext.scenarioSimulationEditorPresenter.setUndoButtonEnabledStatus(!doneCommands.isEmpty());
        scenarioSimulationContext.scenarioSimulationEditorPresenter.setRedoButtonEnabledStatus(!undoneCommands.isEmpty());
    }
}
