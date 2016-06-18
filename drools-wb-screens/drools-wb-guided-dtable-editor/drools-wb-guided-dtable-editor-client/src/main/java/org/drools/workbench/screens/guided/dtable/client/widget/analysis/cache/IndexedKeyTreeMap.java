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
package org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache;

import java.util.ArrayList;
import java.util.Collection;

import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.keys.IndexKey;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.keys.Key;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.keys.UUIDKey;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.matchers.FromMatcher;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.select.Select;
import org.uberfire.client.callbacks.Callback;

public class IndexedKeyTreeMap<T extends HasIndex & HasKeys>
        extends KeyTreeMap<T> {

    public IndexedKeyTreeMap( final KeyDefinition... keyIDs ) {
        super( keyIDs );
    }

    public void put( final T object,
                     final int index ) {
        final ArrayList<Key> keys = new ArrayList<>();
        for ( final Key additionalKey : object.keys() ) {
            keys.add( additionalKey );
        }

        doForAll( index,
                  new Callback<T>() {
                      @Override
                      public void callback( final T t ) {
                          t.setIndex( t.getIndex() + 1 );
                      }
                  } );

        object.setIndex( index );

        super.put( object );
    }

    private void doForAll( final int index,
                           final Callback<T> callback ) {
        final ChangeHandledMultiMap<T> map = get( IndexKey.INDEX_ID );
        final Collection<T> all = new Select<T>( map,
                                                 new FromMatcher( IndexKey.INDEX_ID,
                                                                  index,
                                                                  true ) ).all();
        for ( final T t : all ) {
            callback.callback( t );
        }
    }

    @Override
    protected T remove( final UUIDKey uuidKey ) {
        final T remove = super.remove( uuidKey );

        doForAll( remove.getIndex(),
                  new Callback<T>() {
                      @Override
                      public void callback( final T t ) {
                          t.setIndex( t.getIndex() - 1 );
                      }
                  } );

        return remove;
    }

    @Override
    public void put( final T object ) {
        put( object,
             resolveMapByKeyId( IndexKey.INDEX_ID ).size() );
    }

}
