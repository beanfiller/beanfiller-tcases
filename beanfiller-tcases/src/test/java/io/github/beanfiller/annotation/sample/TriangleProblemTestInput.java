/* Copyright 2018 The Beanfiller Authors. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
==============================================================================*/

package io.github.beanfiller.annotation.sample;

import io.github.beanfiller.annotation.annotations.Value;
import io.github.beanfiller.annotation.annotations.Var;
import io.github.beanfiller.annotation.creator.AbstractTestInput;
import io.github.beanfiller.annotation.creator.FunctionTestsCreator;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static io.github.beanfiller.annotation.sample.TriangleProblemTestInput.TriangleCategory.DEGENERATE;
import static io.github.beanfiller.annotation.sample.TriangleProblemTestInput.TriangleCategory.EQUILATERAL;
import static io.github.beanfiller.annotation.sample.TriangleProblemTestInput.TriangleCategory.INVALID;
import static io.github.beanfiller.annotation.sample.TriangleProblemTestInput.TriangleCategory.ISOSCELES;
import static io.github.beanfiller.annotation.sample.TriangleProblemTestInput.TriangleCategory.SCALENE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.cornutum.tcases.TestCase.Type.FAILURE;

public class TriangleProblemTestInput extends AbstractTestInput {

    public static final String FUNCTION_NAME = "TriangleProblemTestInput";
    public static final String SAME_LENGTH = "SAME_LENGTH";
    private static final String ALL_ZERO = "ALL_ZERO";
    private static final String FIRST_ZERO = "FIRST_ZERO";
    private static final String SECOND_ZERO = "SECOND_ZERO";
    private static final String SECOND_NEGATIVE = "SECOND_NEGATIVE";

    @Var(value = {
            @Value(value = "NEGATIVE", type = FAILURE),
            @Value(value = "ZERO", type = FAILURE, properties = FIRST_ZERO, once = true)
    }, nullable = false)
    FirstSegmentLength aCase;

    @Var(value = {
            @Value(value = "NEGATIVE", type = FAILURE, properties = SECOND_NEGATIVE),
            @Value(value = "ZERO", type = FAILURE, whenNot = FIRST_ZERO),
            @Value(value = "SAME_AS_FIRST", properties = SAME_LENGTH, whenNot = FIRST_ZERO)
    }, nullable = false)
    SecondSegmentLength bCase;

    @Var(value = {
            @Value(value = "NEGATIVE", type = FAILURE),
            @Value(value = "TOO_SHORT", type = FAILURE),
            @Value(value = "DIFFERENCE_BETWEEN_FIRST_TWO", whenNot = {SAME_LENGTH, FIRST_ZERO, SECOND_ZERO}),
            @Value(value = "SAME_AS_SECOND", whenNot = {SECOND_ZERO, SECOND_NEGATIVE}),
            @Value(value = "SHORTER_THAN_SECOND", whenNot = {SECOND_ZERO, SECOND_NEGATIVE, FIRST_ZERO})
    }, nullable = false)
    ThirdSegmentLength cCase;

    private Triangle toTriangle() {
        final double a;
        switch (aCase) {
            case NEGATIVE:
                a = -1;
                break;
            case ZERO:
                a = 0;
                break;
            default:
            case POSITIVE:
                a = 2;
        }
        final double b;
        switch (bCase) {
            case NEGATIVE:
                b = -1;
                break;
            case ZERO:
                b = 0;
                break;
            case SAME_AS_FIRST:
                b = a;
                break;
            default:
            case LARGER_THAN_FIRST:
                b = 3;
                break;
        }
        final double c;
        switch (cCase) {
            case NEGATIVE:
                c = -1;
                break;
            case DIFFERENCE_BETWEEN_FIRST_TWO:
                c = b - a;
                break;
            case SAME_AS_SECOND:
                c = b;
                break;
            default:
            case SHORTER_THAN_SECOND:
                c = b - 0.5;
                break;
            case TOO_SHORT:
                c = 0.1;
                break;
        }
        return new Triangle(a, b, c);
    }

    private static class Triangle {
        double a, b, c;

        Triangle(double a, double b, double c) {
            this.a = a;
            this.b = b;
            this.c = c;
        }

        TriangleCategory classifyTriangle() {
            if (a <= 0 || b <= 0 || c <= 0) return INVALID; // added test
            if (equal(a, b) && equal(b, c)) return EQUILATERAL;
            if (equal(a, b + c) || equal(c, b + a) || equal(b, a + c)) return DEGENERATE;
            if (a >= b + c || c >= b + a || b >= a + c) return INVALID;
            if (equal(b, c) || equal(a, b) || equal(c, a)) return ISOSCELES;
            return SCALENE;
        }

        private static boolean equal(double a, double b) {
            final double c = a - b;
            return Math.abs(c) <= 0.000001;
        }

        @Override
        public String toString() {
            return "Triangle{"
                    + "a=" + a
                    + ", b=" + b
                    + ", c=" + c
                    + '}';
        }
    }



    @Override
    public String toString() {
        return "TestInput" + getTestCaseId() + "{"
                + "aCase=" + aCase
                + ", bCase=" + bCase
                + ", cCase=" + cCase
                + '}' + (isFailure() ? "Error" : "");
    }

    @Test
    public void testTupleSize() {
        final int tupleSize = 2;
        final List<TriangleProblemTestInput> testCases = new FunctionTestsCreator<>(TriangleProblemTestInput.class)
                .tupleGenerator(tupleSize)
                .createDefs();
        testCases.forEach(test -> System.out.println(test.toString() + " " + test.toTriangle() + " " + test.toTriangle().classifyTriangle()));
        assertThat(testCases.size()).isGreaterThan(9);
        assertThat(testCases.stream().map(tcase -> tcase.toTriangle().classifyTriangle()).distinct().collect(Collectors.toList()))
                .contains(TriangleCategory.values());
    }

    enum TriangleCategory {
        ISOSCELES,
        EQUILATERAL,
        SCALENE,
        DEGENERATE,
        INVALID
    }

    enum FirstSegmentLength {
        NEGATIVE,
        ZERO,
        POSITIVE
    }

    enum SecondSegmentLength {
        NEGATIVE,
        ZERO,
        LARGER_THAN_FIRST,
        SAME_AS_FIRST
    }


    enum ThirdSegmentLength {
        NEGATIVE,
        TOO_SHORT,
        DIFFERENCE_BETWEEN_FIRST_TWO,
        SAME_AS_SECOND,
        SHORTER_THAN_SECOND
    }

}
