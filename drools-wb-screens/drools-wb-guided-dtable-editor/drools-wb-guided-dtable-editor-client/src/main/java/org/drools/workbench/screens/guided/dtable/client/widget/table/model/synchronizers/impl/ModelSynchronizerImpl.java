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

package org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import javax.enterprise.context.Dependent;

import com.google.gwt.event.shared.EventBus;
import org.drools.workbench.models.guided.dtable.shared.model.AttributeCol52;
import org.drools.workbench.models.guided.dtable.shared.model.BRLRuleModel;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumnFieldDiff;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumnFieldDiffImpl;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.models.guided.dtable.shared.model.RowNumberCol52;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.controller.ValidateEvent;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTablePresenter;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.SalienceUiColumn;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.GuidedDecisionTableUiModel;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.converters.cell.GridWidgetCellFactory;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.converters.column.GridWidgetColumnFactory;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.ModelSynchronizer;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.Synchronizer;
import org.drools.workbench.screens.guided.dtable.client.widget.table.utilities.CellUtilities;
import org.drools.workbench.screens.guided.dtable.client.widget.table.utilities.ColumnUtilities;
import org.drools.workbench.screens.guided.dtable.client.widget.table.utilities.DependentEnumsUtilities;
import org.drools.workbench.screens.guided.rule.client.editor.RuleAttributeWidget;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.CellValue;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.data.Coordinate;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.controller.AfterColumnDeleted;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.controller.AfterColumnInserted;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.AppendRowEvent;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.DeleteRowEvent;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.InsertRowEvent;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.UpdateColumnDataEvent;
import org.uberfire.commons.validation.PortablePreconditions;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.GridRow;

import static org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.impl.BaseColumnSynchronizer.MetaData;
import static org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.impl.ConditionColumnSynchronizer.MoveColumnToMetaData;
import static org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.impl.ConditionColumnSynchronizer.MoveColumnToMetaDataImpl;
import static org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.impl.ConditionColumnSynchronizer.MoveRowToMetaData;
import static org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.impl.ConditionColumnSynchronizer.MoveRowToMetaDataImpl;
import static org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.impl.ConditionColumnSynchronizer.*;
import static org.drools.workbench.screens.guided.dtable.client.widget.table.utilities.DependentEnumsUtilities.*;

/**
 * Handles synchronization of Model and UI-Model
 */
@Dependent
public class ModelSynchronizerImpl implements ModelSynchronizer {

    private GuidedDecisionTable52 model;
    private GuidedDecisionTableUiModel uiModel;
    private CellUtilities cellUtilities;
    private ColumnUtilities columnUtilities;
    private DependentEnumsUtilities dependentEnumsUtilities;
    private GridWidgetCellFactory gridWidgetCellFactory;
    private EventBus eventBus;

    private final List<Synchronizer<? extends MetaData, ? extends MetaData, ? extends MetaData, ? extends MetaData, ? extends MetaData>> synchronizers = new ArrayList<>();

    @Override
    public void setSynchronizers( final List<Synchronizer<? extends MetaData, ? extends MetaData, ? extends MetaData, ? extends MetaData, ? extends MetaData>> synchronizers ) {
        this.synchronizers.clear();
        Collections.sort( synchronizers,
                          new Comparator<Synchronizer>() {
                              @Override
                              public int compare( final Synchronizer o1,
                                                  final Synchronizer o2 ) {
                                  return o2.priority() - o1.priority();
                              }
                          } );
        this.synchronizers.addAll( synchronizers );
    }

    @Override
    public void initialise( final GuidedDecisionTable52 model,
                            final GuidedDecisionTableUiModel uiModel,
                            final CellUtilities cellUtilities,
                            final ColumnUtilities columnUtilities,
                            final DependentEnumsUtilities dependentEnumsUtilities,
                            final GridWidgetCellFactory gridWidgetCellFactory,
                            final GridWidgetColumnFactory gridWidgetColumnFactory,
                            final GuidedDecisionTableView view,
                            final BRLRuleModel rm,
                            final EventBus eventBus,
                            final GuidedDecisionTablePresenter.Access access ) {
        this.model = PortablePreconditions.checkNotNull( "model",
                                                         model );
        this.uiModel = PortablePreconditions.checkNotNull( "uiModel",
                                                           uiModel );
        this.cellUtilities = PortablePreconditions.checkNotNull( "cellUtilities",
                                                                 cellUtilities );
        this.columnUtilities = PortablePreconditions.checkNotNull( "columnUtilities",
                                                                   columnUtilities );
        this.dependentEnumsUtilities = PortablePreconditions.checkNotNull( "dependentEnumsUtilities",
                                                                           dependentEnumsUtilities );
        this.gridWidgetCellFactory = PortablePreconditions.checkNotNull( "gridWidgetCellFactory",
                                                                         gridWidgetCellFactory );
        this.eventBus = PortablePreconditions.checkNotNull( "eventBus",
                                                            eventBus );

        for ( Synchronizer<? extends MetaData, ? extends MetaData, ? extends MetaData, ? extends MetaData, ? extends MetaData> synchronizer : synchronizers ) {
            synchronizer.initialise( model,
                                     uiModel,
                                     cellUtilities,
                                     columnUtilities,
                                     gridWidgetCellFactory,
                                     gridWidgetColumnFactory,
                                     view,
                                     rm,
                                     eventBus,
                                     access );
        }
    }

    @Override
    public void setCell( final GridData.Range rowRange,
                         final int columnIndex,
                         final GridCellValue<?> value ) {
        final int minRowIndex = rowRange.getMinRowIndex();
        final int maxRowIndex = rowRange.getMaxRowIndex();
        for ( int rowIndex = minRowIndex; rowIndex <= maxRowIndex; rowIndex++ ) {
            final List<DTCellValue52> modelRow = model.getData().get( rowIndex );
            final DTCellValue52 modelCell = modelRow.get( columnIndex );
            if ( value == null ) {
                modelCell.clearValues();
            } else {
                modelRow.set( columnIndex,
                              new DTCellValue52( value.getValue() ) );
            }
        }
        final Set<Integer> columnRange = updateDependentEnumerationColumns( rowRange,
                                                                            columnIndex );
        fireValidateEvent( rowRange,
                           columnRange );
    }

    @Override
    public void deleteCell( final GridData.Range rowRange,
                            final int columnIndex ) {
        final int minRowIndex = rowRange.getMinRowIndex();
        final int maxRowIndex = rowRange.getMaxRowIndex();
        for ( int rowIndex = minRowIndex; rowIndex <= maxRowIndex; rowIndex++ ) {
            final List<DTCellValue52> modelRow = model.getData().get( rowIndex );
            final DTCellValue52 modelCell = modelRow.get( columnIndex );
            modelCell.clearValues();
        }
        final Set<Integer> columnRange = updateDependentEnumerationColumns( rowRange,
                                                                            columnIndex );
        fireValidateEvent( rowRange,
                           columnRange );
    }

    private Set<Integer> updateDependentEnumerationColumns( final GridData.Range rowRange,
                                                            final int columnIndex ) {
        final int minRowIndex = rowRange.getMinRowIndex();
        final int maxRowIndex = rowRange.getMaxRowIndex();
        final Context context = new Context( rowRange.getMinRowIndex(),
                                             columnIndex );
        final Set<Integer> dependentColumnIndexes = dependentEnumsUtilities.getDependentColumnIndexes( context );
        for ( int dependentColumnIndex : dependentColumnIndexes ) {
            for ( int rowIndex = minRowIndex; rowIndex <= maxRowIndex; rowIndex++ ) {
                final List<DTCellValue52> modelRow = model.getData().get( rowIndex );
                final DTCellValue52 modelCell = modelRow.get( dependentColumnIndex );
                modelCell.clearValues();
                uiModel.deleteCellInternal( rowIndex,
                                            dependentColumnIndex );
            }
            uiModel.indexColumn( dependentColumnIndex );
        }
        dependentColumnIndexes.add( columnIndex );
        return dependentColumnIndexes;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void appendColumn( final BaseColumn column ) throws MoveColumnVetoException {
        final MetaData metaData = new BaseColumnSynchronizer.ColumnMetaDataImpl( column );
        for ( Synchronizer synchronizer : synchronizers ) {
            if ( synchronizer.handlesAppend( metaData ) ) {
                synchronizer.append( metaData );
                break;
            }
        }

        fireAfterColumnInsertedEvent( column );
    }

    @Override
    @SuppressWarnings("unchecked")
    public void appendColumn( final Pattern52 pattern,
                              final ConditionCol52 column ) throws MoveColumnVetoException {
        final PatternConditionMetaData metaData = new PatternConditionMetaData( pattern,
                                                                                column );
        for ( Synchronizer synchronizer : synchronizers ) {
            if ( synchronizer.handlesAppend( metaData ) ) {
                synchronizer.append( metaData );
                break;
            }
        }
        fireAfterColumnInsertedEvent( column );
    }

    @Override
    @SuppressWarnings("unchecked")
    public void deleteColumn( final BaseColumn column ) throws MoveColumnVetoException {
        final int columnIndex = model.getExpandedColumns().indexOf( column );
        final MetaData metaData = new BaseColumnSynchronizer.ColumnMetaDataImpl( column );
        for ( Synchronizer synchronizer : synchronizers ) {
            if ( synchronizer.handlesDelete( metaData ) ) {
                synchronizer.delete( metaData );
                break;
            }
        }
        fireAfterColumnDeletedEvent( columnIndex );
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<BaseColumnFieldDiff> updateColumn( final Pattern52 originalPattern,
                                                   final ConditionCol52 originalColumn,
                                                   final Pattern52 editedPattern,
                                                   final ConditionCol52 editedColumn ) throws MoveColumnVetoException {
        final PatternConditionMetaData originalMetaData = new PatternConditionMetaData( originalPattern,
                                                                                        originalColumn );

        final PatternConditionMetaData editedMetaData = new PatternConditionMetaData( editedPattern,
                                                                                      editedColumn );
        for ( Synchronizer synchronizer : synchronizers ) {
            if ( synchronizer.handlesUpdate( originalMetaData ) ) {
                return synchronizer.update( originalMetaData,
                                            editedMetaData );
            }
        }
        return Collections.emptyList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<BaseColumnFieldDiff> updateColumn( final BaseColumn originalColumn,
                                                   final BaseColumn editedColumn ) throws MoveColumnVetoException {
        final MetaData originalMetaData = new BaseColumnSynchronizer.ColumnMetaDataImpl( originalColumn );
        final MetaData editedMetaData = new BaseColumnSynchronizer.ColumnMetaDataImpl( editedColumn );
        for ( Synchronizer synchronizer : synchronizers ) {
            if ( synchronizer.handlesUpdate( originalMetaData ) ) {
                final List<BaseColumnFieldDiff> diffs = synchronizer.update( originalMetaData,
                                                                             editedMetaData );
                final boolean isSalienceUseRowNumberUpdated = BaseColumnFieldDiffImpl.hasChanged( AttributeCol52.FIELD_USE_ROW_NUMBER,
                                                                                                  diffs );
                final boolean isSalienceUseReverseOrderUpdated = BaseColumnFieldDiffImpl.hasChanged( AttributeCol52.FIELD_REVERSE_ORDER,
                                                                                                     diffs );
                if ( isSalienceUseRowNumberUpdated || isSalienceUseReverseOrderUpdated ) {
                    updateSystemControlledColumnValues();
                }
                return diffs;
            }
        }
        return Collections.emptyList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void appendRow() throws MoveColumnVetoException {
        final MetaData metaData = new RowSynchronizer.RowMetaDataImpl();
        for ( Synchronizer synchronizer : synchronizers ) {
            if ( synchronizer.handlesAppend( metaData ) ) {
                synchronizer.append( metaData );
                break;
            }
        }
        fireAppendRowEvent();
        updateSystemControlledColumnValues();
        fireUpdateColumnDataEvent();
    }

    @Override
    public void insertRow( final int rowIndex ) throws MoveColumnVetoException {
        final MetaData metaData = new RowSynchronizer.RowMetaDataImpl( rowIndex );
        for ( Synchronizer synchronizer : synchronizers ) {
            if ( synchronizer.handlesInsert( metaData ) ) {
                synchronizer.insert( metaData );
                break;
            }
        }
        fireInsertRowEvent( rowIndex );
        updateSystemControlledColumnValues();
        fireUpdateColumnDataEvent();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void deleteRow( final int rowIndex ) throws MoveColumnVetoException {
        final MetaData metaData = new RowSynchronizer.RowMetaDataImpl( rowIndex );
        for ( Synchronizer synchronizer : synchronizers ) {
            if ( synchronizer.handlesDelete( metaData ) ) {
                synchronizer.delete( metaData );
                break;
            }
        }
        fireDeleteRowEvent( rowIndex );
        updateSystemControlledColumnValues();
        fireUpdateColumnDataEvent();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void moveColumnTo( final int targetColumnIndex,
                              final GridColumn<?> column ) throws MoveColumnVetoException {
        final int sourceColumnIndex = uiModel.getColumns().indexOf( column );
        if ( sourceColumnIndex == targetColumnIndex ) {
            throw new MoveColumnVetoException();
        }

        final BaseColumn modelColumn = model.getExpandedColumns().get( sourceColumnIndex );
        final List<MoveColumnToMetaData> metaData = new ArrayList<MoveColumnToMetaData>() {
            {
                add( new MoveColumnToMetaDataImpl( sourceColumnIndex,
                                                   targetColumnIndex,
                                                   modelColumn ) );
            }
        };

        final List<Synchronizer> handlers = new ArrayList<Synchronizer>();
        for ( Synchronizer synchronizer : synchronizers ) {
            if ( synchronizer.handlesMoveColumnsTo( metaData ) ) {
                handlers.add( synchronizer );
            }
        }

        if ( handlers.isEmpty() ) {
            throw new MoveColumnVetoException();
        }

        for ( Synchronizer synchronizer : handlers ) {
            synchronizer.moveColumnsTo( metaData );
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void moveColumnsTo( final int targetColumnIndex,
                               final List<GridColumn<?>> columns ) throws MoveColumnVetoException {
        //Generate meta-data to handle moves
        final List<MoveColumnToMetaData> metaData = new ArrayList<MoveColumnToMetaData>();
        for ( int index = 0; index < columns.size(); index++ ) {
            final GridColumn<?> column = columns.get( index );
            final int sourceColumnIndex = uiModel.getColumns().indexOf( column );
            if ( sourceColumnIndex == targetColumnIndex ) {
                throw new MoveColumnVetoException();
            }

            final BaseColumn modelColumn = model.getExpandedColumns().get( sourceColumnIndex );
            metaData.add( new MoveColumnToMetaDataImpl( sourceColumnIndex,
                                                        targetColumnIndex + index,
                                                        modelColumn ) );
        }

        Synchronizer handler = null;
        for ( Synchronizer synchronizer : synchronizers ) {
            if ( synchronizer.handlesMoveColumnsTo( metaData ) ) {
                handler = synchronizer;
                break;
            }
        }

        if ( handler == null ) {
            throw new MoveColumnVetoException();
        }

        handler.moveColumnsTo( metaData );
    }

    @Override
    @SuppressWarnings("unchecked")
    public void moveRowsTo( final int targetRowIndex,
                            final List<GridRow> rows ) throws MoveColumnVetoException {
        //Generate meta-data to handle moves
        final List<MoveRowToMetaData> metaData = new ArrayList<MoveRowToMetaData>();
        for ( int index = 0; index < rows.size(); index++ ) {
            final GridRow row = rows.get( index );
            final int sourceRowIndex = uiModel.getRows().indexOf( row );
            if ( sourceRowIndex == targetRowIndex ) {
                throw new MoveColumnVetoException();
            }

            final List<DTCellValue52> modelRow = model.getData().get( sourceRowIndex );
            metaData.add( new MoveRowToMetaDataImpl( sourceRowIndex,
                                                     targetRowIndex + index,
                                                     modelRow ) );
        }

        final List<Synchronizer> handlers = new ArrayList<Synchronizer>();
        for ( Synchronizer synchronizer : synchronizers ) {
            if ( synchronizer.handlesMoveRowsTo( metaData ) ) {
                handlers.add( synchronizer );
            }
        }

        if ( handlers.isEmpty() ) {
            throw new MoveColumnVetoException();
        }

        for ( Synchronizer synchronizer : handlers ) {
            synchronizer.moveRowsTo( metaData );
        }

    }

    @Override
    public void updateSystemControlledColumnValues() {
        for ( BaseColumn column : model.getExpandedColumns() ) {
            if ( column instanceof RowNumberCol52 ) {
                updateRowNumberColumnValues( (RowNumberCol52) column );

            } else if ( column instanceof AttributeCol52 ) {
                final AttributeCol52 attrCol = (AttributeCol52) column;
                if ( attrCol.getAttribute().equals( RuleAttributeWidget.SALIENCE_ATTR ) ) {
                    updateSalienceColumnValues( attrCol );
                }
            }
        }
    }

    // Update Row Number column values
    private void updateRowNumberColumnValues( final RowNumberCol52 modelColumn ) {
        final int iModelColumn = model.getExpandedColumns().indexOf( modelColumn );
        for ( int rowNumber = 0; rowNumber < model.getData().size(); rowNumber++ ) {
            final List<DTCellValue52> modelRow = model.getData().get( rowNumber );
            final DTCellValue52 modelCell = modelRow.get( iModelColumn );
            modelCell.setNumericValue( rowNumber + 1 );

            uiModel.setCellInternal( rowNumber,
                                     iModelColumn,
                                     gridWidgetCellFactory.convertCell( modelCell,
                                                                        modelColumn,
                                                                        cellUtilities,
                                                                        columnUtilities ) );
        }
    }

    // Update Salience column definition and values
    private void updateSalienceColumnValues( final AttributeCol52 modelColumn ) {
        final int iModelColumn = model.getExpandedColumns().indexOf( modelColumn );
        final GridColumn<?> uiColumn = uiModel.getColumns().get( iModelColumn );
        if ( uiColumn instanceof SalienceUiColumn ) {
            ( (SalienceUiColumn) uiColumn ).setUseRowNumber( modelColumn.isUseRowNumber() );
        }

        //If Salience values are-user defined, exit
        if ( !modelColumn.isUseRowNumber() ) {
            return;
        }

        //If Salience values are reverse order derive them and update column
        int salience = ( modelColumn.isReverseOrder() ? model.getData().size() : 1 );
        for ( int rowNumber = 0; rowNumber < model.getData().size(); rowNumber++ ) {
            final List<DTCellValue52> modelRow = model.getData().get( rowNumber );
            final DTCellValue52 modelCell = modelRow.get( iModelColumn );
            modelCell.setNumericValue( salience );

            uiModel.setCellInternal( rowNumber,
                                     iModelColumn,
                                     gridWidgetCellFactory.convertCell( modelCell,
                                                                        modelColumn,
                                                                        cellUtilities,
                                                                        columnUtilities ) );
            if ( modelColumn.isReverseOrder() ) {
                salience--;
            } else {
                salience++;
            }
        }
        uiModel.indexColumn( iModelColumn );
    }

    @Override
    public void setCellOtherwiseState( final int rowIndex,
                                       final int columnIndex ) {
        final BaseColumn modelColumn = model.getExpandedColumns().get( columnIndex );
        final DTCellValue52 modelCell = model.getData().get( rowIndex ).get( columnIndex );
        modelCell.clearValues();
        modelCell.setOtherwise( true );

        //BaseGridData is sparsely populated; only add values if needed.
        if ( modelCell.hasValue() ) {
            uiModel.setCellInternal( rowIndex,
                                     columnIndex,
                                     gridWidgetCellFactory.convertCell( modelCell,
                                                                        modelColumn,
                                                                        cellUtilities,
                                                                        columnUtilities ) );
        }
        uiModel.indexColumn( columnIndex );
    }

    protected void fireAfterColumnInsertedEvent( final BaseColumn column ) {
        eventBus.fireEvent( new AfterColumnInserted( column ) );
    }

    protected void fireAfterColumnDeletedEvent( final int columnIndex ) {
        eventBus.fireEvent( new AfterColumnDeleted( columnIndex,
                                                    1 ) );
    }

    protected void fireAppendRowEvent() {
        eventBus.fireEvent( new AppendRowEvent() );
    }

    protected void fireDeleteRowEvent( final int rowIndex ) {
        eventBus.fireEvent( new DeleteRowEvent( rowIndex ) );
    }

    protected void fireInsertRowEvent( final int rowIndex ) {
        eventBus.fireEvent( new InsertRowEvent( rowIndex ) );
    }

    protected void fireValidateEvent( final GridData.Range rowRange,
                                      final Set<Integer> columnRange ) {
        final int minRowIndex = rowRange.getMinRowIndex();
        final int maxRowIndex = rowRange.getMaxRowIndex();
        final List<Coordinate> updates = new ArrayList<>();
        for ( final Integer columnIndex : columnRange ) {
            for ( int rowIndex = minRowIndex; rowIndex <= maxRowIndex; rowIndex++ ) {
                updates.add( new Coordinate( rowIndex,
                                             columnIndex ) );
            }
        }
        final ValidateEvent event = new ValidateEvent( updates );
        eventBus.fireEvent( event );
    }

    protected void fireUpdateColumnDataEvent() {
        final List<CellValue<? extends Comparable<?>>> columnData = new ArrayList<CellValue<? extends Comparable<?>>>();
        for ( int rowIndex = 0; rowIndex < model.getData().size(); rowIndex++ ) {
            columnData.add( null );
        }
        final UpdateColumnDataEvent event = new UpdateColumnDataEvent( 0,
                                                                       columnData );
        eventBus.fireEvent( event );
    }

}
