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

import java.util.Collection;

import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.IndexedKeyTreeMap;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.matchers.Matcher;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.select.Listen;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.select.Select;

public class Rules {

    public final IndexedKeyTreeMap<Rule> map = new IndexedKeyTreeMap<>( Rule.keyDefinitions() );

    public Rules() {

    }

    public Rules( final Collection<Rule> rules ) {
        for ( final Rule rule : rules ) {
            add( rule );
        }
    }

    public Where<RulesSelect, RulesListen> where( final Matcher matcher ) {
        return new Where<>( new RulesSelect( matcher ),
                            new RulesListen( matcher ) );
    }

    public void merge( final Rules rules ) {
        map.merge( rules.map );
    }

    public void add( final Rule rule ) {
        map.put( rule,
                 rule.getIndex() );
    }

    public void remove( final Rule rule ) {
        rule.getUuidKey().retract();
    }

    public class RulesSelect
            extends Select<Rule> {

        public RulesSelect( final Matcher matcher ) {
            super( map.get( matcher.getKeyDefinition() ),
                   matcher );
        }

        public Patterns patterns() {
            final Collection<Rule> rules = all();

            final Patterns patterns = new Patterns();

            for ( final Rule rule : rules ) {
                patterns.merge( rule.getPatterns() );
            }

            return patterns;
        }
    }

    public class RulesListen
            extends Listen<Rule> {

        public RulesListen( final Matcher matcher ) {
            super( map.get( matcher.getKeyDefinition() ),
                   matcher );
        }

    }
}
