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

package org.drools.workbench.screens.guided.dtable.client.widget.table.themes;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Line;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableViewImpl;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.GuidedDecisionTableUiModel;
import org.uberfire.ext.wires.core.grids.client.model.Bounds;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseBounds;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRendererHelper;

public class GuidedDecisionTableRenderer extends BaseGridRenderer {

    private static final int HEADER_HEIGHT = 96;

    public GuidedDecisionTableRenderer( final GuidedDecisionTableUiModel uiModel,
                                        final GuidedDecisionTable52 model ) {
        super( new GuidedDecisionTableTheme( uiModel,
                                             model ) );
    }

    @Override
    public double getHeaderHeight() {
        return HEADER_HEIGHT;
    }

    @Override
    public Group renderSelector( final double width,
                                 final double height,
                                 final BaseGridRendererHelper.RenderingInformation renderingInformation ) {
        final Group g = new Group();
        final Bounds bounds = getSelectorBounds( width,
                                                 height,
                                                 renderingInformation );
        final MultiPath selector = theme.getSelector()
                .M( bounds.getX() + 0.5,
                    bounds.getY() + 0.5 )
                .L( bounds.getX() + 0.5,
                    height )
                .L( width,
                    height )
                .L( width,
                    bounds.getY() + GuidedDecisionTableViewImpl.HEADER_CAPTION_HEIGHT )
                .L( bounds.getX() + GuidedDecisionTableViewImpl.HEADER_CAPTION_WIDTH,
                    bounds.getY() + GuidedDecisionTableViewImpl.HEADER_CAPTION_HEIGHT )
                .L( bounds.getX() + GuidedDecisionTableViewImpl.HEADER_CAPTION_WIDTH,
                    bounds.getY() + 0.5 )
                .L( bounds.getX() + GuidedDecisionTableViewImpl.HEADER_CAPTION_WIDTH,
                    bounds.getY() + 0.5 )
                .L( bounds.getX() + 0.5,
                    bounds.getY() + 0.5 )
                .setListening( false );
        g.add( selector );
        return g;
    }

    private Bounds getSelectorBounds( final double width,
                                      final double height,
                                      final BaseGridRendererHelper.RenderingInformation renderingInformation ) {
        final BaseGridRendererHelper.RenderingBlockInformation bodyBlockInformation = renderingInformation.getBodyBlockInformation();
        final BaseGridRendererHelper.RenderingBlockInformation floatingBlockInformation = renderingInformation.getFloatingBlockInformation();
        double boundsX = 0.0;
        double boundsY = 0.0;
        double boundsWidth = width;
        double boundsHeight = height;
        if ( !floatingBlockInformation.getColumns().isEmpty() ) {
            boundsX = floatingBlockInformation.getX();
            boundsWidth = boundsWidth - floatingBlockInformation.getX();
        }
        if ( renderingInformation.isFloatingHeader() ) {
            boundsY = bodyBlockInformation.getHeaderY();
            boundsHeight = boundsHeight - bodyBlockInformation.getHeaderY();
        }
        return new BaseBounds( boundsX,
                               boundsY,
                               boundsWidth,
                               boundsHeight );
    }

    @Override
    public Group renderHeaderBodyDivider( final double width ) {
        final Group g = new Group();
        final Line dividerLine1 = theme.getGridHeaderBodyDivider();
        final Line dividerLine2 = theme.getGridHeaderBodyDivider();
        dividerLine1.setPoints( new Point2DArray( new Point2D( 0,
                                                               getHeaderHeight() - 1.5 ),
                                                  new Point2D( width,
                                                               getHeaderHeight() - 1.5 ) ) );
        dividerLine2.setPoints( new Point2DArray( new Point2D( 0,
                                                               getHeaderHeight() + 0.5 ),
                                                  new Point2D( width,
                                                               getHeaderHeight() + 0.5 ) ) );
        g.add( dividerLine1 );
        g.add( dividerLine2 );
        return g;
    }

}
