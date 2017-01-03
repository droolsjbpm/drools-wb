/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.workbench.services.verifier.webworker.client;

import org.drools.workbench.services.verifier.api.client.Status;
import org.drools.workbench.services.verifier.api.client.reporting.Issues;
import org.drools.workbench.services.verifier.plugin.client.Logger;
import org.drools.workbench.services.verifier.plugin.client.api.WebWorkerException;
import org.drools.workbench.services.verifier.plugin.client.api.WebWorkerLogMessage;
import org.jboss.errai.enterprise.client.jaxrs.MarshallingWrapper;

public class Poster {

    public void post( final Issues issues ) {
        postToMainApp( issues );
    }

    public void post( final Status status ) {
        postToMainApp( status );
    }

    public void post( final WebWorkerException exception ) {
        postToMainApp( exception );
    }

    public void post( final WebWorkerLogMessage logMessage ) {
        postToMainApp( logMessage );
    }

    private void postToMainApp( final Object o ) {
        if ( !Logger.isEmpty() ) {
            post( new WebWorkerLogMessage( Logger.log() ) );
        }

        postToMainApp( MarshallingWrapper.toJSON( o ) );
    }

    public native void postToMainApp( String json )/*-{
        postMessage(json);
    }-*/;
}
