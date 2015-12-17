/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.workbench.jcr2vfsmigration.jcrExport.asset;

import javax.inject.Inject;

import org.drools.workbench.jcr2vfsmigration.common.FileManager;
import org.drools.workbench.jcr2vfsmigration.xml.model.asset.AttachmentAsset;

public class AttachmentAssetExporter
        extends BaseAssetExporter
        implements AssetExporter<AttachmentAsset, ExportContext> {

    @Inject
    private FileManager fileManager;

    private static int attachmentFileNameCounter = 1;

    @Override
    public AttachmentAsset export( ExportContext exportContext ) {
        String attachmentName = exportContext.getAssetExportFileName() + "_" + attachmentFileNameCounter++;
        if ( !fileManager.writeBinaryContent( attachmentName, exportContext.getJcrAssetItem().getBinaryContentAsBytes() ) )
            System.out.println( "WARNING: no binary content was written for asset " + exportContext.getJcrAssetItem().getName() );
        return new AttachmentAsset( exportContext.getJcrAssetItem().getName(),
                                    exportContext.getJcrAssetItem().getFormat(),
                                    exportContext.getJcrAssetItem().getLastContributor(),
                                    exportContext.getJcrAssetItem().getCheckinComment(),
                                    exportContext.getJcrAssetItem().getLastModified().getTime(),
                                    attachmentName );
    }
}
