/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.workbench.screens.globals.client.editor;

import java.util.List;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.drools.workbench.screens.globals.client.type.GlobalResourceType;
import org.drools.workbench.screens.globals.model.GlobalsEditorContent;
import org.drools.workbench.screens.globals.model.GlobalsModel;
import org.drools.workbench.screens.globals.service.GlobalsEditorService;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.widgets.client.callbacks.AssetValidatedCallback;
import org.kie.workbench.common.widgets.metadata.client.KieEditor;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartTitleDecoration;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.ext.widgets.common.client.callbacks.CommandErrorCallback;
import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnMayClose;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.Menus;

/**
 * Globals Editor Presenter
 */
@WorkbenchEditor(identifier = "org.kie.guvnor.globals", supportedTypes = {GlobalResourceType.class}, priority = 101)
public class GlobalsEditorPresenter
        extends KieEditor {

    @Inject
    protected Caller<GlobalsEditorService> globalsEditorService;

    private GlobalsEditorView view;

    @Inject
    private Event<NotificationEvent> notification;

    @Inject
    private GlobalResourceType type;

    private GlobalsModel model;

    public GlobalsEditorPresenter() {
    }

    @Inject
    public GlobalsEditorPresenter(final GlobalsEditorView baseView) {
        super(baseView);
        view = baseView;
    }

    @OnStartup
    public void onStartup(final ObservablePath path,
                          final PlaceRequest place) {
        super.init(path,
                   place,
                   type);
    }

    protected void loadContent() {
        view.showLoading();
        globalsEditorService.call(getModelSuccessCallback(),
                                  getNoSuchFileExceptionErrorCallback()).loadContent(versionRecordManager.getCurrentPath());
    }

    private RemoteCallback<GlobalsEditorContent> getModelSuccessCallback() {
        return new RemoteCallback<GlobalsEditorContent>() {

            @Override
            public void callback(final GlobalsEditorContent content) {
                //Path is set to null when the Editor is closed (which can happen before async calls complete).
                if (versionRecordManager.getCurrentPath() == null) {
                    return;
                }

                model = content.getModel();

                resetEditorPages(content.getOverview());
                addSourcePage();

                final List<String> fullyQualifiedClassNames = content.getFullyQualifiedClassNames();

                view.setContent(content.getModel().getGlobals(),
                                fullyQualifiedClassNames,
                                isReadOnly);

                createOriginalHash(model);
                view.hideBusyIndicator();
            }
        };
    }

    @Override
    protected void onValidate(final Command callFinished) {
        globalsEditorService
                .call(new AssetValidatedCallback(callFinished, notification),
                      new CommandErrorCallback(callFinished))
                .validate(versionRecordManager.getCurrentPath(),
                          model);
    }

    @Override
    protected void save(String commitMessage) {
        globalsEditorService.call(getSaveSuccessCallback(model.hashCode()),
                                  new HasBusyIndicatorDefaultErrorCallback(view)).save(versionRecordManager.getCurrentPath(),
                                                                                       model,
                                                                                       metadata,
                                                                                       commitMessage);
    }

    @Override
    public void onSourceTabSelected() {
        globalsEditorService.call(new RemoteCallback<String>() {
            @Override
            public void callback(String source) {
                updateSource(source);
            }
        }).toSource(versionRecordManager.getCurrentPath(),
                    model);
    }

    @WorkbenchPartView
    public IsWidget getWidget() {
        return super.getWidget();
    }

    @OnClose
    public void onClose() {
        this.versionRecordManager.clear();
    }

    @OnMayClose
    public boolean mayClose() {
        return super.mayClose(model);
    }

    @WorkbenchPartTitleDecoration
    public IsWidget getTitle() {
        return super.getTitle();
    }

    @WorkbenchPartTitle
    public String getTitleText() {
        return super.getTitleText();
    }

    @WorkbenchMenu
    public Menus getMenus() {
        return menus;
    }
}
