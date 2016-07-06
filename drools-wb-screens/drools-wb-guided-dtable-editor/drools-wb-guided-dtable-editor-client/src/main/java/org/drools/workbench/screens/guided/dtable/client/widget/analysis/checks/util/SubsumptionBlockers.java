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

package org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.util;

import java.util.HashMap;

import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.keys.UUIDKey;

public class SubsumptionBlockers {

    public final HashMap<UUIDKey, SubsumptionBlocker> keyMap = new HashMap<>();
    private boolean record;

    public SubsumptionBlockers( final boolean record ) {
        this.record = record;
    }

    public void add( final SubsumptionBlocker blocker ) {
        if ( record ) {
            keyMap.put( blocker.otherUUID(), blocker );
        }
    }

    public SubsumptionBlocker get( final UUIDKey uuidKey ) {
        return keyMap.get( uuidKey );
    }

    public void remove( final SubsumptionBlocker blocker ) {
        if ( record ) {
            keyMap.remove( blocker.otherUUID() );
        }
    }

    public int size() {
        return keyMap.size();
    }
}
