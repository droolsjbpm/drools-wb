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

package org.drools.workbench.screens.scenariosimulation.client.producers;

//@RunWith(LienzoMockitoTestRunner.class)
public class ScenarioSimulationViewProducerTest extends AbstractProducerTest {

//    @Mock
//    private ScenarioGridPanelProducer scenarioGridPanelProducerMock;
//
//    @Mock
//    private ScenarioSimulationMainGridPanelClickHandler clickHandlerMock;
//
//    @Mock
//    private ScenarioSimulationMainGridPanelMouseMoveHandler mouseMoveHandlerMock;
//
//    @Mock
//    private ErrorReportPopoverPresenter errorReportPopupPresenterMock;
//
//    private ScenarioSimulationViewProducer scenarioSimulationViewProducer;
//
//    @Before
//    public void setUp() {
//        super.setup();
//        when(scenarioGridPanelProducerMock.getScenarioMainGridPanel()).thenReturn(scenarioGridPanelMock);
//        when(scenarioGridPanelProducerMock.getScenarioBackgroundGridPanel()).thenReturn(scenarioGridPanelMock);
//        when(scenarioGridPanelProducerMock.getScenarioContextMenuRegistry()).thenReturn(scenarioContextMenuRegistryMock);
//        scenarioSimulationViewProducer = spy(new ScenarioSimulationViewProducer() {
//            {
//                this.scenarioMainGridWidget = scenarioGridWidgetSpy;
//                this.scenarioBackgroundGridWidget = scenarioGridWidgetSpy;
//                this.scenarioSimulationView = scenarioSimulationViewMock;
//                this.scenarioGridPanelProducer = scenarioGridPanelProducerMock;
//                this.errorReportPopupPresenter = errorReportPopupPresenterMock;
//                this.scenarioMainGridPanelClickHandler = clickHandlerMock;
//                this.scenarioMainGridPanelMouseMoveHandler = mouseMoveHandlerMock;
//                this.scenarioBackgroundGridPanelClickHandler = clickHandlerMock;
//                this.scenarioBackgroundGridPanelMouseMoveHandler = mouseMoveHandlerMock;
//            }
//        });
//    }
//
//    @Test
//    public void getScenarioSimulationView() {
//        final ScenarioSimulationView retrieved = scenarioSimulationViewProducer.getScenarioSimulationView(eventBusMock);
//        assertEquals(scenarioSimulationViewMock, retrieved);
//        verify(scenarioSimulationViewProducer, times(1)).getScenarioMainGridWidget(eq(eventBusMock));
//        verify(scenarioSimulationViewMock, times(1)).setScenarioGridWidget(scenarioGridWidgetSpy);
//    }
//
//    @Test
//    public void getScenarioBackgroundGridWidget() {
//        ScenarioGridWidget retrieved = scenarioSimulationViewProducer.getScenarioBackgroundGridWidget(eventBusMock);
//        assertEquals(scenarioGridWidgetSpy, retrieved);
//        verify(scenarioSimulationViewProducer, times(1)).initGridWidget(eq(scenarioGridWidgetSpy), eq(scenarioGridPanelMock), eq(clickHandlerMock), eq(mouseMoveHandlerMock), eq(eventBusMock));
//    }
//
//    @Test
//    public void initGridWidget() {
//        scenarioSimulationViewProducer.initGridWidget(scenarioGridWidgetSpy, scenarioGridPanelMock, clickHandlerMock, mouseMoveHandlerMock, eventBusMock);
//        verify(scenarioGridPanelMock, times(1)).setEventBus(eq(eventBusMock));
//        verify(scenarioGridPanelProducerMock, times(1)).getScenarioContextMenuRegistry();
//        verify(scenarioContextMenuRegistryMock).setEventBus(eventBusMock);
//        verify(clickHandlerMock, times(1)).setScenarioContextMenuRegistry(eq(scenarioSimulationViewProducer.getScenarioContextMenuRegistry()));
//        verify(clickHandlerMock, times(1)).setScenarioGridPanel(eq(scenarioGridPanelMock));
//        verify(clickHandlerMock, times(1)).setEventBus(eq(eventBusMock));
//        verify(scenarioContextMenuRegistryMock, times(1)).setErrorReportPopoverPresenter(errorReportPopupPresenterMock);
//        verify(mouseMoveHandlerMock, times(1)).setScenarioGridPanel(eq(scenarioGridPanelMock));
//        verify(mouseMoveHandlerMock, times(1)).setErrorReportPopupPresenter(eq(errorReportPopupPresenterMock));
//        verify(scenarioGridPanelMock, times(1)).addHandlers(eq(clickHandlerMock), eq(mouseMoveHandlerMock));
//        verify(scenarioGridWidgetSpy, times(1)).setScenarioGridPanel(eq(scenarioGridPanelMock));
//    }
//
//    @Test
//    public void getScenarioContextMenuRegistry() {
//        scenarioSimulationViewProducer.getScenarioContextMenuRegistry();
//        verify(scenarioGridPanelProducerMock, times(1)).getScenarioContextMenuRegistry();
//    }
}