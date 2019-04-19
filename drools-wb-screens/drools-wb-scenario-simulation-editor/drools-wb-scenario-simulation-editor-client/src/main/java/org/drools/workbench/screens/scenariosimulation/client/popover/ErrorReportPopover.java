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
package org.drools.workbench.screens.scenariosimulation.client.popover;

import org.uberfire.mvp.Command;

public interface ErrorReportPopover extends PopoverView {

    interface Presenter {

        /**
         * Makes the <code>ErrorReportPopover</code> visible with Keep/Apply buttons.
         * @param errorTitleText
         * @param errorContentText
         * @param keepText
         * @param applyText
         * @param applyCommand
         * @param keepCommand
         * @param mx x position of the replace
         * @param my y position of the popover
         * @param position position where the popover is put (LEFT or RIGHT)
         */
        void show(final String errorTitleText,
                  final String errorContentText,
                  final String keepText,
                  final String applyText,
                  final Command applyCommand,
                  final Command keepCommand,
                  final int mx,
                  final int my,
                  final Position position);

        /**
         * Makes this popover container(and the main content along with it) invisible. Has no effect if the popover is not
         * already showing.
         */
        void hide();
    }

    /**
     * Makes the <code>ErrorReportPopover</code> visible with Keep/Apply buttons.
     * @param errorTitleText
     * @param errorContentText
     * @param keepText
     * @param applyText
     * @param applyCommand
     * @param keepCommand
     * @param mx x position of the popover
     * @param my y position of the popover
     * @param position position where the popover is put (LEFT or RIGHT)
     */
    void show(final String errorTitleText,
              final String errorContentText,
              final String keepText,
              final String applyText,
              final Command applyCommand,
              final Command keepCommand,
              final int mx,
              final int my,
              final Position position);
    /**
     * Makes this popover container(and the main content along with it) invisible. Has no effect if the popover is not
     * already showing.
     */
    void hide();
}
