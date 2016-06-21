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
package org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.select;

import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.MultiMap;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.keys.Value;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.matchers.ExactMatcher;

public class ExactMatcherSearch<T> {

    private ExactMatcher       matcher;
    private MultiMap<Value, T> map;

    public ExactMatcherSearch( final ExactMatcher matcher,
                               final MultiMap<Value, T> map ) {
        this.matcher = matcher;
        this.map = map;
    }

    public MultiMap<Value, T> search() {

        if ( matcher.isNegate() ) {

            if ( map.containsKey( matcher.getValue() ) ) {

                final MultiMap<Value, T> result = map.subMap( map.firstKey(), true,
                                                              matcher.getValue(), false );
                result.merge( map.subMap( matcher.getValue(), false,
                                          map.lastKey(), true ) );

                return result;
            } else {
                return map;
            }
        } else {
            return map.subMap( matcher.getValue(), true,
                               matcher.getValue(), true );
        }
    }
}
