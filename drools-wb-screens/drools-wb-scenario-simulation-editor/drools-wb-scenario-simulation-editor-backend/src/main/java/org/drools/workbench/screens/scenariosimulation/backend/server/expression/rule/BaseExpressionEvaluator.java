/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.scenariosimulation.backend.server.expression.rule;

import org.drools.workbench.screens.scenariosimulation.backend.server.expression.ExpressionEvaluator;

public class BaseExpressionEvaluator implements ExpressionEvaluator {

    private final ClassLoader classLoader;

    public BaseExpressionEvaluator(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean evaluate(Object raw, Object resultValue, Class<?> resultClass) {
        if (!(raw instanceof String)) {
            return BaseExpressionOperator.EQUALS.eval(raw, resultValue, resultClass, classLoader);
        }

        String rawValue = (String) raw;
        return BaseExpressionOperator.findOperator(rawValue).eval(rawValue, resultValue, resultClass, classLoader);
    }

    @Override
    public Object getValueForGiven(String className, Object raw, ClassLoader classLoader) {
        if (!(raw instanceof String)) {
            return raw;
        }
        String rawValue = (String) raw;
        return BaseExpressionOperator.findOperator(rawValue).getValueForGiven(className, rawValue, classLoader);
    }
}
