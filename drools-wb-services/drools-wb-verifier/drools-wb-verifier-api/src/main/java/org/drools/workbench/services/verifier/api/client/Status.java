/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.services.verifier.api.client;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class Status {

    private final String webWorkerUUID;

    private final int startCheckIndex;
    private final int endCheckIndex;
    private final int totalCheckCount;

    public Status( @MapsTo("webWorkerUUID") final String webWorkerUUID,
                   @MapsTo("startCheckIndex") final int startCheckIndex,
                   @MapsTo("endCheckIndex") final int endCheckIndex,
                   @MapsTo("totalCheckCount") final int totalCheckCount ) {
        this.webWorkerUUID = webWorkerUUID;
        this.startCheckIndex = startCheckIndex;
        this.endCheckIndex = endCheckIndex;
        this.totalCheckCount = totalCheckCount;
    }

    public String getWebWorkerUUID() {
        return webWorkerUUID;
    }

    public int getStart() {
        return startCheckIndex;
    }

    public int getEnd() {
        return endCheckIndex;
    }

    public int getTotalCheckCount() {
        return totalCheckCount;
    }

}
