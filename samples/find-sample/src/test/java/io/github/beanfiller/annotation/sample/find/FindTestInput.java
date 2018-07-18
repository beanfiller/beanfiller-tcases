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

package io.github.beanfiller.annotation.sample.find;

import io.github.beanfiller.annotation.annotations.VarValueTemplate;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import io.github.beanfiller.annotation.creator.AbstractTestInput;
import io.github.beanfiller.annotation.annotations.FunctionDef;
import io.github.beanfiller.annotation.annotations.SystemDef;
import io.github.beanfiller.annotation.annotations.Value;
import io.github.beanfiller.annotation.annotations.Var;
import io.github.beanfiller.annotation.annotations.VarSet;

import static org.cornutum.tcases.TestCase.Type.FAILURE;

/**
 * Usage: find pattern file
 * <p>
 * Locates one or more instances of a given pattern in a text file.
 * <p>
 * All lines in the file that contain the pattern are written to standard output.
 * A line containing the pattern is written only once,
 * regardless of the number of times the pattern occurs in it.
 * <p>
 * The pattern is any sequence of characters whose length does not exceed
 * the maximum length of a line in the file.
 * To include a blank in the pattern, the entire pattern must be enclosed in quotes (").
 * To include a quotation mark in the pattern, two quotes in a row ("") must be used.
 */
@SystemDef({FindTestInput.class})
@FunctionDef(value = FindTestInput.FUNCTION_NAME,
        having = "description:The find function")
public class FindTestInput extends AbstractTestInput {

    // properties for value conditions
    public static final String PATTERN_EMPTY = "empty";
    public static final String PATTERN_SINGLE_CHAR = "singleChar";
    public static final String PATTERN_QUOTED = "quoted";
    public static final String FILE_EXISTS = "fileExists";
    public static final String FUNCTION_NAME = "Find";

    /**
     * Input argument
     */
    @VarSet(when = FILE_EXISTS, nullable = false)
    public Pattern pattern;

    /**
     * Input argument
     */
    @Var({@Value(value = "false",
            type = FAILURE,
            having = "failure:No Filename")})
    public boolean filenameDefined; // boolean always has values true, false

    /**
     * Test environment (target file)
     */
    @VarSet(nullable = false)
    public FileWithPatternedLines file;

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }

    // ValueSet, reusable
    public enum CardinalityZeroToN {
        NONE,
        ONE,
        MANY;
    }

    /**
     * A file with contents to be searched with a pattern
     */
    // VarSet
    public static class FileWithPatternedLines {
        @Var(value = {
                @Value(value = "true", properties = FILE_EXISTS),
                @Value(value = "false", type = FAILURE, having = "failure:FileNotFound")},
                nullable = false)
        public Boolean exists; // boolean always has values true, false

        @VarSet(nullable = false)
        public FileContentsWithPattern contents;

        /**
         * file content type
         */
        // Nested VarSet
        public static class FileContentsWithPattern {
            @Var(value = @Value(value = "false",
                    type = FAILURE,
                    having = "failure:Pattern too long",
                    whenNot = PATTERN_EMPTY),
                    nullable = false)
            public Boolean linesLongerThanPattern;

            @Var(nullable = false)
            public CardinalityZeroToN patterns;

            @Var(exclude = "NONE", nullable = false)
            public CardinalityZeroToN patternsInLine;

        }
    }

    // VarSet
    public static class Pattern {
        @Var(value = {
                @Value(value = "NONE", properties = {PATTERN_EMPTY},
                        type = FAILURE,
                        having = "failure:Pattern empty"),
                @Value(value = "ONE", properties = {PATTERN_SINGLE_CHAR})
        }, nullable = false)
        public CardinalityZeroToN size;

        @Var(value = {
                @Value(value = "YES", whenNot = PATTERN_EMPTY),
                @Value(value = "NO", whenNot = PATTERN_EMPTY),
                @Value(value = "UNTERMINATED", whenNot = PATTERN_EMPTY)
        }, nullable = false)
        public QuotedType quoted;

        @Var(value = {
                @Value(value = "ONE", when = {PATTERN_QUOTED, PATTERN_SINGLE_CHAR}),
                @Value(value = "MANY", when = {PATTERN_QUOTED}, whenNot = {PATTERN_SINGLE_CHAR})
        },
                whenNot = PATTERN_EMPTY, nullable = false)
        public CardinalityZeroToN blanks;

        @Var(whenNot = {PATTERN_EMPTY, PATTERN_SINGLE_CHAR}, nullable = false)
        public CardinalityZeroToN embeddedQuotes;

        public enum QuotedType {
            @VarValueTemplate(properties = PATTERN_QUOTED)
            YES,
            NO,
            @VarValueTemplate(type = FAILURE, having = "failure:QuoteMismatch")
            UNTERMINATED;
        }

    }
}
