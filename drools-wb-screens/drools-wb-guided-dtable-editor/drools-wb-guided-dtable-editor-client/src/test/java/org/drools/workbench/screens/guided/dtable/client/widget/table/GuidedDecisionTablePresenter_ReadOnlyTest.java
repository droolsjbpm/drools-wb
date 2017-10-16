/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.dtable.client.widget.table;

import java.util.Set;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.models.guided.dtable.shared.model.ActionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.AttributeCol52;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.MetadataCol52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.ModelSynchronizer.VetoException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.ext.wires.core.grids.client.model.GridData;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class GuidedDecisionTablePresenter_ReadOnlyTest extends BaseGuidedDecisionTablePresenterTest {

    @Before
    public void setup() {
        super.setup();
        dtPresenter.setReadOnly(true);
    }

    @Test
    public void appendAttributeColumn() throws VetoException {
        dtPresenter.appendColumn(mock(AttributeCol52.class));

        verify(synchronizer,
               never()).appendColumn(any(AttributeCol52.class));
    }

    @Test
    public void appendMetadataColumn() throws VetoException {
        dtPresenter.appendColumn(mock(MetadataCol52.class));

        verify(synchronizer,
               never()).appendColumn(any(MetadataCol52.class));
    }

    @Test
    public void appendPatternAndConditionColumn() throws VetoException {
        dtPresenter.appendColumn(mock(Pattern52.class),
                                 mock(ConditionCol52.class));

        verify(synchronizer,
               never()).appendColumn(any(Pattern52.class),
                                     any(ConditionCol52.class));
    }

    @Test
    public void appendConditionColumn() throws VetoException {
        dtPresenter.appendColumn(mock(ConditionCol52.class));

        verify(synchronizer,
               never()).appendColumn(any(ConditionCol52.class));
    }

    @Test
    public void appendActionColumn() throws VetoException {
        dtPresenter.appendColumn(mock(ActionCol52.class));

        verify(synchronizer,
               never()).appendColumn(any(ActionCol52.class));
    }

    @Test
    public void appendRow() throws VetoException {
        dtPresenter.onAppendRow();

        verify(synchronizer,
               never()).appendRow();
    }

    @Test
    public void deleteAttributeColumn() throws VetoException {
        dtPresenter.deleteColumn(mock(AttributeCol52.class));

        verify(synchronizer,
               never()).deleteColumn(any(AttributeCol52.class));
    }

    @Test
    public void deleteMetadataColumn() throws VetoException {
        dtPresenter.deleteColumn(mock(MetadataCol52.class));

        verify(synchronizer,
               never()).deleteColumn(any(MetadataCol52.class));
    }

    @Test
    public void deleteConditionColumn() throws VetoException {
        dtPresenter.deleteColumn(mock(ConditionCol52.class));

        verify(synchronizer,
               never()).deleteColumn(any(ConditionCol52.class));
    }

    @Test
    public void deleteActionColumn() throws VetoException {
        dtPresenter.deleteColumn(mock(ActionCol52.class));

        verify(synchronizer,
               never()).deleteColumn(any(ActionCol52.class));
    }

    @Test
    public void updateAttributeColumn() throws VetoException {
        dtPresenter.updateColumn(mock(AttributeCol52.class),
                                 mock(AttributeCol52.class));

        verify(synchronizer,
               never()).updateColumn(any(AttributeCol52.class),
                                     any(AttributeCol52.class));
    }

    @Test
    public void updateMetadataColumn() throws VetoException {
        dtPresenter.updateColumn(mock(MetadataCol52.class),
                                 mock(MetadataCol52.class));

        verify(synchronizer,
               never()).updateColumn(any(MetadataCol52.class),
                                     any(MetadataCol52.class));
    }

    @Test
    public void updatePatternAndConditionColumn() throws VetoException {
        dtPresenter.updateColumn(mock(Pattern52.class),
                                 mock(ConditionCol52.class),
                                 mock(Pattern52.class),
                                 mock(ConditionCol52.class));

        verify(synchronizer,
               never()).updateColumn(any(Pattern52.class),
                                     any(ConditionCol52.class),
                                     any(Pattern52.class),
                                     any(ConditionCol52.class));
    }

    @Test
    public void updateConditionColumn() throws VetoException {
        dtPresenter.updateColumn(mock(ConditionCol52.class),
                                 mock(ConditionCol52.class));

        verify(synchronizer,
               never()).updateColumn(any(ConditionCol52.class),
                                     any(ConditionCol52.class));
    }

    @Test
    public void updateActionColumn() throws VetoException {
        dtPresenter.updateColumn(mock(ActionCol52.class),
                                 mock(ActionCol52.class));

        verify(synchronizer,
               never()).updateColumn(any(ActionCol52.class),
                                     any(ActionCol52.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void onCutWithSelection() {
        when(dtPresenter.isSelectionEmpty()).thenReturn(false);

        dtPresenter.onCut();

        verify(clipboard,
               never()).setData(any(Set.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void onCutWithoutSelection() {
        when(dtPresenter.isSelectionEmpty()).thenReturn(true);

        dtPresenter.onCut();

        verify(clipboard,
               never()).setData(any(Set.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void onCopyWithSelection() {
        when(dtPresenter.isSelectionEmpty()).thenReturn(false);

        dtPresenter.onCopy();

        verify(clipboard,
               never()).setData(any(Set.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void onCopyWithoutSelection() {
        when(dtPresenter.isSelectionEmpty()).thenReturn(true);

        dtPresenter.onCopy();

        verify(clipboard,
               never()).setData(any(Set.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void onPasteWithClipboardDataWithSelection() {
        when(clipboard.hasData()).thenReturn(true);
        when(dtPresenter.isSelectionEmpty()).thenReturn(false);

        dtPresenter.onPaste();

        verify(clipboard,
               never()).getData();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void onPasteWithClipboardDataWithoutSelection() {
        when(clipboard.hasData()).thenReturn(true);
        when(dtPresenter.isSelectionEmpty()).thenReturn(true);

        dtPresenter.onPaste();

        verify(clipboard,
               never()).getData();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void onPasteWithoutClipboardDataWithoutSelection() {
        when(clipboard.hasData()).thenReturn(false);
        when(dtPresenter.isSelectionEmpty()).thenReturn(true);

        dtPresenter.onPaste();

        verify(clipboard,
               never()).getData();
    }

    @Test
    public void onDeleteSelectedCells() {
        dtPresenter.onDeleteSelectedCells();

        verify(synchronizer,
               never()).deleteCell(any(GridData.Range.class),
                                   any(Integer.class));
    }

    @Test
    public void onDeleteSelectedColumns() throws VetoException {
        dtPresenter.onDeleteSelectedColumns();

        verify(synchronizer,
               never()).deleteColumn(any(BaseColumn.class));
    }

    @Test
    public void onDeleteSelectedRows() throws VetoException {
        dtPresenter.onDeleteSelectedRows();

        verify(synchronizer,
               never()).deleteRow(any(Integer.class));
    }

    @Test
    public void onInsertRowAbove() throws VetoException {
        dtPresenter.onInsertRowAbove();

        verify(synchronizer,
               never()).insertRow(any(Integer.class));
    }

    @Test
    public void onInsertRowBelow() throws VetoException {
        dtPresenter.onInsertRowBelow();

        verify(synchronizer,
               never()).insertRow(any(Integer.class));
    }

    @Test
    public void onOtherwiseCell() throws VetoException {
        dtPresenter.onOtherwiseCell();

        verify(synchronizer,
               never()).setCellOtherwiseState(any(Integer.class),
                                              any(Integer.class));
    }
}
