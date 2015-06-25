/*
 * Copyright 2015 JBoss Inc
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

package org.drools.workbench.screens.testscenario.client.reporting;

import java.util.Date;
import javax.inject.Inject;

import com.google.gwt.cell.client.ClickableTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.screens.testscenario.client.resources.i18n.TestScenarioConstants;
import org.drools.workbench.screens.testscenario.client.service.TestRuntimeReportingService;
import org.guvnor.common.services.shared.test.Failure;
import org.kie.workbench.common.widgets.client.resources.CommonImages;

public class TestRunnerReportingViewImpl
        extends Composite
        implements TestRunnerReportingView,
                   RequiresResize {

    interface SuccessTemplate extends SafeHtmlTemplates {

        @Template("<span style='color:{0}'>{1}</span>") SafeHtml title(String color,
                                                                       String text);
    }

    private static final SuccessTemplate SUCCESS_TEMPLATE = GWT.create(SuccessTemplate.class);

    private static Binder uiBinder = GWT.create(Binder.class);
    private Presenter presenter;

    interface Binder extends UiBinder<Widget, TestRunnerReportingViewImpl> {

    }

    @UiField(provided = true)
    DataGrid<Failure> dataGrid;

    @UiField
    VerticalPanel panel;

    @UiField
    HTML successPanel;

    @UiField
    Label stats;

    @UiField
    Label explanationLabel;

    @Inject
    public TestRunnerReportingViewImpl() {
        dataGrid = new DataGrid<Failure>();
        dataGrid.setWidth("100%");

        dataGrid.setAutoHeaderRefreshDisabled(true);

        dataGrid.setEmptyTableWidget(new Label("---"));

        setUpColumns();

        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public void onResize() {
        dataGrid.setPixelSize((int) (getParent().getOffsetWidth() * 0.60),
                              getParent().getOffsetHeight());
        dataGrid.onResize();
    }

    private void setUpColumns() {
        addSuccessColumn();
        addTextColumn();
    }

    private void addSuccessColumn() {
        Column<Failure, ImageResource> column = new Column<Failure, ImageResource>(new ImageResourceCell()) {
            @Override
            public ImageResource getValue(Failure failure) {
                presenter.onAddingFailure(failure);
                return CommonImages.INSTANCE.error();
            }
        };
        dataGrid.addColumn(column);
        dataGrid.setColumnWidth(column, 10, Style.Unit.PCT);
    }

    private void addTextColumn() {
        Column<Failure, String> column = new Column<Failure, String>(new ClickableTextCell()) {
            @Override
            public String getValue(Failure failure) {
                return makeMessage(failure);
            }

            private String makeMessage(Failure failure) {
                final String displayName = failure.getDisplayName();
                final String message = failure.getMessage();
                return displayName + (!(message == null || message.isEmpty()) ? " : " + message : "");
            }
        };

        column.setFieldUpdater(new FieldUpdater<Failure, String>() {
            @Override
            public void update(int index,
                               Failure failure,
                               String value) {
                presenter.onMessageSelected(failure);
            }
        });
        dataGrid.addColumn(column, TestScenarioConstants.INSTANCE.Text());
        dataGrid.setColumnWidth(column, 60, Style.Unit.PCT);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void bindDataGridToService(TestRuntimeReportingService testRuntimeReportingService) {
        testRuntimeReportingService.addDataDisplay(dataGrid);
    }

    @Override
    public void showSuccess() {
        successPanel.setHTML(SUCCESS_TEMPLATE.title("green", TestScenarioConstants.INSTANCE.Success()));
    }

    @Override
    public void showFailure() {
        successPanel.setHTML(SUCCESS_TEMPLATE.title("red", TestScenarioConstants.INSTANCE.ThereWereTestFailures()));
    }

    @Override
    public void setExplanation(String explanation) {
        explanationLabel.setText(explanation);
    }

    @Override
    public void setRunStatus(int runCount, long runTime) {
        Date date = new Date(runTime);
        DateTimeFormat minutesFormat = DateTimeFormat.getFormat("m");
        DateTimeFormat secondsFormat = DateTimeFormat.getFormat("s");

        stats.setText(TestScenarioConstants.INSTANCE.XTestsRanInYMinutesZSeconds(runCount, minutesFormat.format(date), secondsFormat.format(date)));
    }
}
