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


import org.uberfire.commons.validation.PortablePreconditions;

public class Field
        extends FieldBase {

    private final ObjectField objectField;
    private final Conditions conditions = new Conditions();
    private final Actions actions = new Actions();

    public Field( final ObjectField objectField,
                  final String factType,
                  final String fieldType,
                  final String name ) {
        super( factType,
               fieldType,
               name );
        this.objectField = PortablePreconditions.checkNotNull( "objectField", objectField );
    }

    public ObjectField getObjectField() {
        return objectField;
    }

    public Conditions getConditions() {
        return conditions;
    }

    public Actions getActions() {
        return actions;
    }

    public void remove( final Column column ) {
        this.conditions.remove( column );
        this.actions.remove( column );
    }

}
