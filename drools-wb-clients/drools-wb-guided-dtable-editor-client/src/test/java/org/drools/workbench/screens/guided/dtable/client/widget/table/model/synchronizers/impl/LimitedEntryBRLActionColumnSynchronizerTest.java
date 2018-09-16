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

package org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.impl;

import java.util.ArrayList;
import java.util.Collections;

import org.drools.workbench.models.guided.dtable.shared.model.LimitedEntryBRLActionColumn;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.BaseMultipleDOMElementUiColumn;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.BooleanUiColumn;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.ModelSynchronizer.VetoException;
import org.junit.Test;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCellValue;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LimitedEntryBRLActionColumnSynchronizerTest extends BaseSynchronizerTest {

    @Test
    public void testAppend() throws VetoException {
        final LimitedEntryBRLActionColumn column = new LimitedEntryBRLActionColumn();
        column.setHeader("col1");

        modelSynchronizer.appendColumn(column);

        assertEquals(1,
                     model.getActionCols().size());

        assertEquals(3,
                     uiModel.getColumns().size());
        assertTrue(uiModel.getColumns().get(2) instanceof BooleanUiColumn);
        assertEquals(true,
                     ((BaseMultipleDOMElementUiColumn) uiModel.getColumns().get(2)).isEditable());
    }

    @Test
    public void testUpdate() throws VetoException {
        final LimitedEntryBRLActionColumn column = new LimitedEntryBRLActionColumn();
        column.setHeader("col1");

        modelSynchronizer.appendColumn(column);

        final LimitedEntryBRLActionColumn edited = new LimitedEntryBRLActionColumn();
        edited.setHideColumn(true);
        edited.setHeader("updated");

        modelSynchronizer.updateColumn(column,
                                       edited);

        assertEquals(1,
                     model.getActionCols().size());

        assertEquals(3,
                     uiModel.getColumns().size());
        assertTrue(uiModel.getColumns().get(2) instanceof BooleanUiColumn);
        assertEquals("updated",
                     uiModel.getColumns().get(2).getHeaderMetaData().get(0).getTitle());
        assertEquals(false,
                     uiModel.getColumns().get(2).isVisible());
    }

    @Test
    public void testDelete() throws VetoException {
        final LimitedEntryBRLActionColumn column = new LimitedEntryBRLActionColumn();
        column.setHeader("col1");

        modelSynchronizer.appendColumn(column);

        assertEquals(1,
                     model.getActionCols().size());
        assertEquals(3,
                     uiModel.getColumns().size());

        modelSynchronizer.deleteColumn(column);
        assertEquals(0,
                     model.getActionCols().size());
        assertEquals(2,
                     uiModel.getColumns().size());
    }

    @Test
    public void testMoveColumnTo_MoveLeft() throws VetoException {
        final LimitedEntryBRLActionColumn column1 = new LimitedEntryBRLActionColumn();
        column1.setHeader("age");
        final LimitedEntryBRLActionColumn column2 = new LimitedEntryBRLActionColumn();
        column2.setHeader("name");

        modelSynchronizer.appendColumn(column1);
        modelSynchronizer.appendColumn(column2);

        modelSynchronizer.appendRow();
        uiModel.setCellValue(0,
                             2,
                             new BaseGridCellValue<Boolean>(true));
        uiModel.setCellValue(0,
                             3,
                             new BaseGridCellValue<Boolean>(false));

        assertEquals(2,
                     model.getActionCols().size());
        assertEquals(column1,
                     model.getActionCols().get(0));
        assertEquals(column2,
                     model.getActionCols().get(1));
        assertEquals(true,
                     model.getData().get(0).get(2).getBooleanValue());
        assertEquals(false,
                     model.getData().get(0).get(3).getBooleanValue());

        assertEquals(4,
                     uiModel.getColumns().size());
        final GridColumn<?> uiModelColumn1_1 = uiModel.getColumns().get(2);
        final GridColumn<?> uiModelColumn2_1 = uiModel.getColumns().get(3);
        assertEquals("age",
                     uiModelColumn1_1.getHeaderMetaData().get(0).getTitle());
        assertEquals("name",
                     uiModelColumn2_1.getHeaderMetaData().get(0).getTitle());
        assertTrue(uiModelColumn1_1 instanceof BooleanUiColumn);
        assertTrue(uiModelColumn2_1 instanceof BooleanUiColumn);
        assertEquals(2,
                     uiModelColumn1_1.getIndex());
        assertEquals(3,
                     uiModelColumn2_1.getIndex());
        assertEquals(true,
                     uiModel.getRow(0).getCells().get(uiModelColumn1_1.getIndex()).getValue().getValue());
        assertEquals(false,
                     uiModel.getRow(0).getCells().get(uiModelColumn2_1.getIndex()).getValue().getValue());

        uiModel.moveColumnTo(2,
                             uiModelColumn2_1);

        assertEquals(2,
                     model.getActionCols().size());
        assertEquals(column2,
                     model.getActionCols().get(0));
        assertEquals(column1,
                     model.getActionCols().get(1));
        assertEquals(false,
                     model.getData().get(0).get(2).getBooleanValue());
        assertEquals(true,
                     model.getData().get(0).get(3).getBooleanValue());

        assertEquals(4,
                     uiModel.getColumns().size());
        final GridColumn<?> uiModelColumn1_2 = uiModel.getColumns().get(2);
        final GridColumn<?> uiModelColumn2_2 = uiModel.getColumns().get(3);
        assertEquals("name",
                     uiModelColumn1_2.getHeaderMetaData().get(0).getTitle());
        assertEquals("age",
                     uiModelColumn2_2.getHeaderMetaData().get(0).getTitle());
        assertTrue(uiModelColumn1_2 instanceof BooleanUiColumn);
        assertTrue(uiModelColumn2_2 instanceof BooleanUiColumn);
        assertEquals(3,
                     uiModelColumn1_2.getIndex());
        assertEquals(2,
                     uiModelColumn2_2.getIndex());
        assertEquals(false,
                     uiModel.getRow(0).getCells().get(uiModelColumn1_2.getIndex()).getValue().getValue());
        assertEquals(true,
                     uiModel.getRow(0).getCells().get(uiModelColumn2_2.getIndex()).getValue().getValue());
    }

    @Test
    public void testMoveColumnTo_MoveRight() throws VetoException {
        final LimitedEntryBRLActionColumn column1 = new LimitedEntryBRLActionColumn();
        column1.setHeader("age");
        final LimitedEntryBRLActionColumn column2 = new LimitedEntryBRLActionColumn();
        column2.setHeader("name");

        modelSynchronizer.appendColumn(column1);
        modelSynchronizer.appendColumn(column2);

        modelSynchronizer.appendRow();
        uiModel.setCellValue(0,
                             2,
                             new BaseGridCellValue<Boolean>(true));
        uiModel.setCellValue(0,
                             3,
                             new BaseGridCellValue<Boolean>(false));

        assertEquals(2,
                     model.getActionCols().size());
        assertEquals(column1,
                     model.getActionCols().get(0));
        assertEquals(column2,
                     model.getActionCols().get(1));
        assertEquals(true,
                     model.getData().get(0).get(2).getBooleanValue());
        assertEquals(false,
                     model.getData().get(0).get(3).getBooleanValue());

        assertEquals(4,
                     uiModel.getColumns().size());
        final GridColumn<?> uiModelColumn1_1 = uiModel.getColumns().get(2);
        final GridColumn<?> uiModelColumn2_1 = uiModel.getColumns().get(3);
        assertEquals("age",
                     uiModelColumn1_1.getHeaderMetaData().get(0).getTitle());
        assertEquals("name",
                     uiModelColumn2_1.getHeaderMetaData().get(0).getTitle());
        assertTrue(uiModelColumn1_1 instanceof BooleanUiColumn);
        assertTrue(uiModelColumn2_1 instanceof BooleanUiColumn);
        assertEquals(2,
                     uiModelColumn1_1.getIndex());
        assertEquals(3,
                     uiModelColumn2_1.getIndex());
        assertEquals(true,
                     uiModel.getRow(0).getCells().get(uiModelColumn1_1.getIndex()).getValue().getValue());
        assertEquals(false,
                     uiModel.getRow(0).getCells().get(uiModelColumn2_1.getIndex()).getValue().getValue());

        uiModel.moveColumnTo(3,
                             uiModelColumn1_1);

        assertEquals(2,
                     model.getActionCols().size());
        assertEquals(column2,
                     model.getActionCols().get(0));
        assertEquals(column1,
                     model.getActionCols().get(1));
        assertEquals(false,
                     model.getData().get(0).get(2).getBooleanValue());
        assertEquals(true,
                     model.getData().get(0).get(3).getBooleanValue());

        assertEquals(4,
                     uiModel.getColumns().size());
        final GridColumn<?> uiModelColumn1_2 = uiModel.getColumns().get(2);
        final GridColumn<?> uiModelColumn2_2 = uiModel.getColumns().get(3);
        assertEquals("name",
                     uiModelColumn1_2.getHeaderMetaData().get(0).getTitle());
        assertEquals("age",
                     uiModelColumn2_2.getHeaderMetaData().get(0).getTitle());
        assertTrue(uiModelColumn1_2 instanceof BooleanUiColumn);
        assertTrue(uiModelColumn2_2 instanceof BooleanUiColumn);
        assertEquals(3,
                     uiModelColumn1_2.getIndex());
        assertEquals(2,
                     uiModelColumn2_2.getIndex());
        assertEquals(false,
                     uiModel.getRow(0).getCells().get(uiModelColumn1_2.getIndex()).getValue().getValue());
        assertEquals(true,
                     uiModel.getRow(0).getCells().get(uiModelColumn2_2.getIndex()).getValue().getValue());
    }

    @Test
    public void testMoveColumnTo_OutOfBounds() throws VetoException {
        final LimitedEntryBRLActionColumn column1 = new LimitedEntryBRLActionColumn();
        column1.setHeader("age");
        final LimitedEntryBRLActionColumn column2 = new LimitedEntryBRLActionColumn();
        column2.setHeader("name");

        modelSynchronizer.appendColumn(column1);
        modelSynchronizer.appendColumn(column2);

        modelSynchronizer.appendRow();
        uiModel.setCellValue(0,
                             2,
                             new BaseGridCellValue<Boolean>(true));
        uiModel.setCellValue(0,
                             3,
                             new BaseGridCellValue<Boolean>(false));

        assertEquals(2,
                     model.getActionCols().size());
        assertEquals(column1,
                     model.getActionCols().get(0));
        assertEquals(column2,
                     model.getActionCols().get(1));
        assertEquals(true,
                     model.getData().get(0).get(2).getBooleanValue());
        assertEquals(false,
                     model.getData().get(0).get(3).getBooleanValue());

        assertEquals(4,
                     uiModel.getColumns().size());
        final GridColumn<?> uiModelColumn1_1 = uiModel.getColumns().get(2);
        final GridColumn<?> uiModelColumn2_1 = uiModel.getColumns().get(3);
        assertEquals("age",
                     uiModelColumn1_1.getHeaderMetaData().get(0).getTitle());
        assertEquals("name",
                     uiModelColumn2_1.getHeaderMetaData().get(0).getTitle());
        assertTrue(uiModelColumn1_1 instanceof BooleanUiColumn);
        assertTrue(uiModelColumn2_1 instanceof BooleanUiColumn);
        assertEquals(2,
                     uiModelColumn1_1.getIndex());
        assertEquals(3,
                     uiModelColumn2_1.getIndex());
        assertEquals(true,
                     uiModel.getRow(0).getCells().get(uiModelColumn1_1.getIndex()).getValue().getValue());
        assertEquals(false,
                     uiModel.getRow(0).getCells().get(uiModelColumn2_1.getIndex()).getValue().getValue());

        uiModel.moveColumnTo(0,
                             uiModelColumn1_1);

        assertEquals(2,
                     model.getActionCols().size());
        assertEquals(column1,
                     model.getActionCols().get(0));
        assertEquals(column2,
                     model.getActionCols().get(1));
        assertEquals(true,
                     model.getData().get(0).get(2).getBooleanValue());
        assertEquals(false,
                     model.getData().get(0).get(3).getBooleanValue());

        assertEquals(4,
                     uiModel.getColumns().size());
        final GridColumn<?> uiModelColumn1_2 = uiModel.getColumns().get(2);
        final GridColumn<?> uiModelColumn2_2 = uiModel.getColumns().get(3);
        assertEquals("age",
                     uiModelColumn1_2.getHeaderMetaData().get(0).getTitle());
        assertEquals("name",
                     uiModelColumn2_2.getHeaderMetaData().get(0).getTitle());
        assertTrue(uiModelColumn1_2 instanceof BooleanUiColumn);
        assertTrue(uiModelColumn2_2 instanceof BooleanUiColumn);
        assertEquals(2,
                     uiModelColumn1_2.getIndex());
        assertEquals(3,
                     uiModelColumn2_2.getIndex());
        assertEquals(true,
                     uiModel.getRow(0).getCells().get(uiModelColumn1_2.getIndex()).getValue().getValue());
        assertEquals(false,
                     uiModel.getRow(0).getCells().get(uiModelColumn2_2.getIndex()).getValue().getValue());
    }

    @Test
    public void checkHandlesMoveColumnsToWithEmptyMetadata() throws VetoException {
        final LimitedEntryBRLActionColumnSynchronizer synchronizer = new LimitedEntryBRLActionColumnSynchronizer();

        assertFalse(synchronizer.handlesMoveColumnsTo(Collections.emptyList()));
    }

    @Test
    public void checkHandlesMoveColumnsToWithMultipleMetadata() throws VetoException {
        final BaseSynchronizer.MoveColumnToMetaData md0 = mock(BaseSynchronizer.MoveColumnToMetaData.class);
        final BaseSynchronizer.MoveColumnToMetaData md1 = mock(BaseSynchronizer.MoveColumnToMetaData.class);
        final LimitedEntryBRLActionColumnSynchronizer synchronizer = new LimitedEntryBRLActionColumnSynchronizer();
        when(md0.getColumn()).thenReturn(mock(LimitedEntryBRLActionColumn.class));
        when(md1.getColumn()).thenReturn(mock(LimitedEntryBRLActionColumn.class));

        assertFalse(synchronizer.handlesMoveColumnsTo(new ArrayList<BaseSynchronizer.MoveColumnToMetaData>() {{
            add(md0);
            add(md1);
        }}));
    }

    @Test
    public void checkHandlesMoveColumnsToWithSingleMetadata() throws VetoException {
        final BaseSynchronizer.MoveColumnToMetaData md0 = mock(BaseSynchronizer.MoveColumnToMetaData.class);
        final LimitedEntryBRLActionColumnSynchronizer synchronizer = new LimitedEntryBRLActionColumnSynchronizer();
        when(md0.getColumn()).thenReturn(mock(LimitedEntryBRLActionColumn.class));

        assertTrue(synchronizer.handlesMoveColumnsTo(Collections.singletonList(md0)));
    }
}
