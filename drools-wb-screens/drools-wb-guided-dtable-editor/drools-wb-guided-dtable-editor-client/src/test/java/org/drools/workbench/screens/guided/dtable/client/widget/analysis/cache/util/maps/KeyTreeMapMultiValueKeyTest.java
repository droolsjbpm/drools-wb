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

package org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.util.maps;

import java.util.List;

import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.util.HasKeys;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.keys.Key;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.keys.UUIDKey;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.keys.UpdatableKey;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.keys.Value;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.keys.Values;
import org.junit.Before;
import org.junit.Test;

import static org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.Util.*;
import static org.junit.Assert.*;

public class KeyTreeMapMultiValueKeyTest {

    private KeyTreeMap<Country> map;

    private final KeyDefinition NAME      = KeyDefinition.newKeyDefinition().withId( "name" ).build();
    private final KeyDefinition AREA_CODE = KeyDefinition.newKeyDefinition().withId( "areaCode" ).build();

    private Country norway;
    private Country finland;
    private Country sweden;

    @Before
    public void setUp() throws Exception {
        map = new KeyTreeMap<>( NAME,
                                AREA_CODE );

        finland = new Country( "Finland", 48100 );
        sweden = new Country( "Sweden", 12345, 51000 );
        norway = new Country( "Norway", 00000, 51000 );

        map.put( finland );
        map.put( sweden );
        map.put( norway );
    }

    @Test
    public void testFindByUUID() throws Exception {
        assertMapContent( map.get( UUIDKey.UNIQUE_UUID ), finland.uuidKey, sweden.uuidKey, norway.uuidKey );
    }

    @Test
    public void testFindByAreaCodeKey() throws Exception {
        assertMapContent( map.get( AREA_CODE ), 48100, 12345, 51000, 00000 );
    }

    @Test
    public void testFindByAreaCode() throws Exception {
        final MultiMap<Value, Country, List<Country>> areaCode = map.get( AREA_CODE );
        assertEquals( 1, areaCode.get( new Value( 48100 ) ).size() );
        assertTrue( areaCode.get( new Value( 48100 ) ).contains( finland ) );
        assertEquals( 1, areaCode.get( new Value( 12345 ) ).size() );
        assertTrue( areaCode.get( new Value( 12345 ) ).contains( sweden ) );
        assertEquals( 2, areaCode.get( new Value( 51000 ) ).size() );
        assertTrue( areaCode.get( new Value( 51000 ) ).contains( sweden ) );
        assertTrue( areaCode.get( new Value( 51000 ) ).contains( norway ) );
    }

    class Country
            implements HasKeys {

        private final UUIDKey uuidKey = new UUIDKey( this );

        final String name;

        private UpdatableKey areaCode;

        public Country( final String name,
                        final Integer... areaCodes ) {
            this.name = name;
            this.areaCode = new UpdatableKey( AREA_CODE,
                                              new Values( areaCodes ) );
        }

        @Override
        public Key[] keys() {
            return new Key[]{
                    uuidKey,
                    new Key( NAME,
                             name ),
                    areaCode
            };
        }

        public void setAge( final Integer... areaCodes ) {
            final UpdatableKey oldKey = areaCode;

            final UpdatableKey<Country> newKey = new UpdatableKey( AREA_CODE,
                                                                   new Values( areaCodes ) );
            areaCode = newKey;

            oldKey.update( newKey,
                           this );

        }

        @Override
        public UUIDKey getUuidKey() {
            return uuidKey;
        }
    }
}