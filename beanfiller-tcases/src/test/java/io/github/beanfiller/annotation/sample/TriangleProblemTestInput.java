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

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import io.github.beanfiller.annotation.annotations.Value;
import io.github.beanfiller.annotation.annotations.Var;
import io.github.beanfiller.annotation.creator.AbstractTestInput;
import io.github.beanfiller.annotation.util.CustomToStringStyle;

import static io.github.beanfiller.annotation.sample.TriangleProblemTestInput.TriangleCategory.DEGENERATE;
import static io.github.beanfiller.annotation.sample.TriangleProblemTestInput.TriangleCategory.EQUILATERAL;
import static io.github.beanfiller.annotation.sample.TriangleProblemTestInput.TriangleCategory.INVALID;
import static io.github.beanfiller.annotation.sample.TriangleProblemTestInput.TriangleCategory.ISOSCELES;
import static io.github.beanfiller.annotation.sample.TriangleProblemTestInput.TriangleCategory.SCALENE;
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
            @Value(value = "ZERO", properties = FIRST_ZERO, once = true)
    })
    FirstSegmentLength a;
    @Var(value = {
            @Value(value = "NEGATIVE", type = FAILURE, properties = SECOND_NEGATIVE),
            @Value(value = "ZERO", type = FAILURE, when = FIRST_ZERO),
            @Value(value = "SAME_AS_FIRST", properties = SAME_LENGTH)
    })
    SecondSegmentLength b;
    @Var(value = {
            @Value(value = "NEGATIVE", type = FAILURE),
            @Value(value = "TOO_SHORT", type = FAILURE),
            @Value(value = "DIFFERENCE_BETWEEN_FIRST_TWO", whenNot = {SAME_LENGTH, FIRST_ZERO, SECOND_ZERO}),
            @Value(value = "SAME_AS_SECOND", whenNot = {SECOND_ZERO, SECOND_NEGATIVE}),
            @Value(value = "SHORTER_THAN_SECOND", whenNot = {SECOND_ZERO, SECOND_NEGATIVE})
    })
    ThirdSegmentLength c;

    public static TriangleCategory classifyTriangle(double a, double b, double c) {
        if (a <= 0 || b <= 0 || c <= 0) return INVALID; // added test
        if (equal(a, b) && equal(b, c)) return EQUILATERAL;
        if (equal(a, b + c) || equal(c, b + a) || equal(b, a + c)) return DEGENERATE;
        if (a >= b + c || c >= b + a || b >= a + c) return INVALID;
        if (equal(b, c) || equal(a, b) || equal(c, a)) return ISOSCELES;
        return SCALENE;
    }

    private static boolean equal(double a, double b) {
        double c = a - b;
        return Math.abs(c - 1.0) <= 0.000001;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, CustomToStringStyle.INSTANCE);
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
