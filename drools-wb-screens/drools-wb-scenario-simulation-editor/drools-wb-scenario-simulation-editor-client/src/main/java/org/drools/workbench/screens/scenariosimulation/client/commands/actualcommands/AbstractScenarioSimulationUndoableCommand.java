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
package org.drools.workbench.screens.scenariosimulation.client.commands.actualcommands;

import java.util.Optional;

import org.drools.workbench.screens.scenariosimulation.client.commands.ScenarioSimulationContext;
import org.drools.workbench.screens.scenariosimulation.client.commands.ScenarioSimulationViolation;
import org.kie.workbench.common.command.client.CommandResult;

public abstract class AbstractScenarioSimulationUndoableCommand<S> extends AbstractScenarioSimulationCommand {

    /**
     * The <code>ScenarioSimulationContext.Status</code> to restore when calling <b>undo/redo</b>.
     * Needed only for <b>undoable</b> commands.
     */
    protected S restorableStatus = null;

    @Override
    public CommandResult<ScenarioSimulationViolation> execute(ScenarioSimulationContext context) {
        restorableStatus = setRestorableStatusPreExecution(context);
        return super.execute(context);
    }

    @Override
    public CommandResult<ScenarioSimulationViolation> undo(ScenarioSimulationContext context) {
        return commonUndoRedo(context);
    }

    public CommandResult<ScenarioSimulationViolation> redo(ScenarioSimulationContext context) {
        return commonUndoRedo(context);
    }

    private CommandResult<ScenarioSimulationViolation> commonUndoRedo(ScenarioSimulationContext context) {
        if (restorableStatus == null) {
            String message = this.getClass().getSimpleName() + "restorableStatus status is null";
            throw new IllegalStateException(message);
        }
        return setCurrentContext(context);
    }

    protected abstract CommandResult<ScenarioSimulationViolation> setCurrentContext(ScenarioSimulationContext context);

    protected abstract S setRestorableStatusPreExecution(ScenarioSimulationContext context);

    public abstract Optional<CommandResult<ScenarioSimulationViolation>> commonUndoRedoPreExecution(ScenarioSimulationContext context);
}
