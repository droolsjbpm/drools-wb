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

package org.drools.workbench.screens.scenariosimulation.backend.server.expression;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BaseExpressionOperatorTest {

    private static final ClassLoader classLoader = BaseExpressionOperatorTest.class.getClassLoader();

    @Test
    public void getValueForGivenTest() {

        Arrays.stream(BaseExpressionOperator.values())
                .filter(e -> !BaseExpressionOperator.EQUALS.equals(e))
                .forEach(operator -> {
                    assertThatThrownBy(() -> operator.getValueForGiven(String.class.getCanonicalName(), " Test ", classLoader))
                            .isInstanceOf(IllegalStateException.class)
                            .hasMessage("This operator cannot be used into a Given clause");
                });

        Assert.assertEquals("Test", BaseExpressionOperator.EQUALS.getValueForGiven(String.class.getCanonicalName(), "= Test", classLoader));
        Assert.assertEquals("", BaseExpressionOperator.EQUALS.getValueForGiven(String.class.getCanonicalName(), "= ", classLoader));
        Assert.assertEquals(null, BaseExpressionOperator.EQUALS.getValueForGiven(String.class.getCanonicalName(), "", classLoader));
    }

    @Test
    public void findOperator() {
        String rawValue = "Test";
        assertEquals(BaseExpressionOperator.EQUALS, BaseExpressionOperator.findOperator(rawValue));

        rawValue = " Test ";
        assertEquals(BaseExpressionOperator.EQUALS, BaseExpressionOperator.findOperator(rawValue));

        rawValue = "= Test ";
        assertEquals(BaseExpressionOperator.EQUALS, BaseExpressionOperator.findOperator(rawValue));

        rawValue = "!= Test ";
        assertEquals(BaseExpressionOperator.NOT_EQUALS, BaseExpressionOperator.findOperator(rawValue));
    }

    @Test
    public void equalsTest() {
        MyTestClass test1 = new MyTestClass();
        MyTestClass test2 = new MyTestClass();
        MyComparableTestClass comparableTest1 = new MyComparableTestClass();

        // Tested via Objects.equals
        assertTrue(BaseExpressionOperator.EQUALS.eval(test1, test1, classLoader));
        assertFalse(BaseExpressionOperator.EQUALS.eval(test1, test2, classLoader));
        // Tested via Comparable.compareTo
        assertTrue(BaseExpressionOperator.EQUALS.eval(comparableTest1, comparableTest1, classLoader));

        assertTrue(BaseExpressionOperator.EQUALS.eval("1", 1, classLoader));

        assertTrue(BaseExpressionOperator.EQUALS.eval(null, null, classLoader));
    }

    @Test
    public void notEqualsTest() {
        MyTestClass test1 = new MyTestClass();
        MyTestClass test2 = new MyTestClass();
        MyComparableTestClass comparableTest1 = new MyComparableTestClass();

        // Tested via Objects.equals
        assertFalse(BaseExpressionOperator.NOT_EQUALS.eval(test1, test1, classLoader));
        assertTrue(BaseExpressionOperator.NOT_EQUALS.eval(test1, test2, classLoader));
        // Tested via Comparable.compareTo
        assertFalse(BaseExpressionOperator.NOT_EQUALS.eval(comparableTest1, comparableTest1, classLoader));

        assertTrue(BaseExpressionOperator.NOT_EQUALS.eval("<> 1", 2, classLoader));

        assertFalse(BaseExpressionOperator.NOT_EQUALS.eval(null, null, classLoader));

        // NOT_EQUALS can be composed
        assertTrue(BaseExpressionOperator.NOT_EQUALS.eval("! <1", 2, classLoader));
        assertTrue(BaseExpressionOperator.NOT_EQUALS.eval("! [1, 3]", 2, classLoader));
    }

    @Test
    public void rangeTest() {
        assertFalse(BaseExpressionOperator.RANGE.eval(new Object(), "", classLoader));

        assertThatThrownBy(() -> BaseExpressionOperator.RANGE.eval("<2 , ,", "", classLoader))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Impossible to find the operator in expression ");

        assertTrue(BaseExpressionOperator.RANGE.eval(">2, <=3", 3, classLoader));
    }

    @Test
    public void listOfValuesTest() {
        assertFalse(BaseExpressionOperator.LIST_OF_VALUES.eval(new Object(), "", classLoader));

        assertThatThrownBy(() -> BaseExpressionOperator.LIST_OF_VALUES.eval("[ 2", "", classLoader))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Malformed expression: [ 2");

        assertTrue(BaseExpressionOperator.LIST_OF_VALUES.eval("[ Test, Another Test]", "Another Test", classLoader));
    }

    private class MyTestClass {

    }

    private class MyComparableTestClass implements Comparable<MyComparableTestClass> {

        @Override
        public int compareTo(MyComparableTestClass o) {
            return 0;
        }
    }
}