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
package org.drools.workbench.screens.guided.dtable.client.widget.analysis.index;

import java.util.Arrays;
import java.util.Collection;

import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.KeyTreeMap;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.MultiMap;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.keys.Value;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.matchers.Matcher;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.select.Listen;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.select.Select;

public class Patterns {

    public final KeyTreeMap<Pattern> patternsMap = new KeyTreeMap<>( Pattern.keyDefinitions() );

    public Patterns( final Collection<Pattern> patternsMap ) {
        for ( final Pattern pattern : patternsMap ) {
            add( pattern );
        }

    }

    public Patterns( final Pattern[] patternsMap ) {
        this( Arrays.asList( patternsMap ) );
    }

    public Patterns() {

    }

    public void merge( final Patterns patterns ) {
        this.patternsMap.merge( patterns.patternsMap );
    }

    public Where<PatternsSelect, PatternsListen> where( final Matcher matcher ) {
        return new Where<>( new PatternsSelect( matcher ),
                            new PatternsListen( matcher ) );
    }

    void add( final Pattern... patterns ) {
        for ( final Pattern pattern : patterns ) {
            this.patternsMap.put( pattern );
        }
    }

    public class PatternsSelect
            extends Select<Pattern> {

        public PatternsSelect( final Matcher matcher ) {
            super( patternsMap.get( matcher.getKeyDefinition() ),
                   matcher );
        }

        public Fields fields() {
            final Fields fields = new Fields();

            final MultiMap<Value, Pattern> subMap = asMap();
            if ( subMap != null ) {
                final Collection<Pattern> patterns = subMap.allValues();
                for ( final Pattern pattern : patterns ) {
                    fields.merge( pattern.getFields() );
                }
            }

            return fields;
        }
    }

    public class PatternsListen
            extends Listen<Pattern> {

        public PatternsListen( final Matcher matcher ) {
            super( patternsMap.get( matcher.getKeyDefinition() ),
                   matcher );
        }
    }
}
