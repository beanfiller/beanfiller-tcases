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

package io.github.beanfiller.annotation.internal.reader;

import org.apache.commons.lang3.StringUtils;
import org.cornutum.tcases.conditions.AllOf;
import org.cornutum.tcases.conditions.AnyOf;
import org.cornutum.tcases.conditions.ContainsAll;
import org.cornutum.tcases.conditions.ContainsAny;
import org.cornutum.tcases.conditions.ICondition;
import org.cornutum.tcases.conditions.Not;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.StringTokenizer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Since Java annotations do not allow nesting of @AllOf, @AnyOf, @Not,
 * this class parses Strings of the form "AllOf(AnyOf(x, y))" to ICondition
 */
class ConditionReader {

    @Nullable
    static ICondition getCondition(@Nullable String[] when1, @Nonnull String[] when2, @Nonnull String[] whenNot) {
        String[] when = mergeArrays(when1, when2);

        ICondition condition = null;

        if (when.length > 0) {
            condition = allOf(StringUtils.join(when, ','));
        }

        if (whenNot.length > 0) {
            ICondition excludes = new Not().add(anyOf(StringUtils.join(whenNot, ',')));

            condition = (condition == null) ? excludes : new AllOf(condition, excludes);
        }

        return condition;
    }

    @Nonnull
    static String[] mergeArrays(@Nullable String[] when1, @Nonnull String[] when2) {
        String[] when = when2;
        if (when1 != null) {
            if (when2.length == 0) {
                when = when1;
            } else {
                int oldsize = when2.length;
                when = Arrays.copyOf(when2, when1.length + when2.length);
                System.arraycopy(when1, 0, when, oldsize, when1.length);
            }
        }
        return when;
    }

    /**
     * For simplicity, "foo,bar" in the context of "all of" means "AllOf(foo, bar)"
     */
    @Nonnull
    private static ICondition anyOf(@Nonnull String con) {
        if (NO_PARENS.matcher(con).matches()) {
            return new ContainsAny(split(con));
        }
        return new AnyOf(parseValue(con).toArray(new ICondition[0]));
    }

    /**
     * For simplicity, "foo,bar" in the context of "any of" means "AnyOf(foo, bar)"
     */
    @Nonnull
    private static ICondition allOf(@Nonnull String con) {
        if (NO_PARENS.matcher(con).matches()) {
            return new ContainsAll(split(con));
        }
        return new AllOf(parseValue(con).toArray(new ICondition[0]));
    }

    @Nonnull
    private static String[] split(@Nonnull String cons) {
        // TODO: need to handle quoting and escaping?
        return cons.split(",");
    }


    /**
     * @param con like "AllOf(a,b)", "AllOf(a,b),AnyOf(c,d),", "AnyOf(AllOf(foo,bar))"
     */
    @Nonnull
    private static List<ICondition> parseValue(@Nonnull String con) {
        StringTokenizer tokenizer = new StringTokenizer(con, "(),", true);
        LinkedList<String> lexemes = new LinkedList<>();
        while (tokenizer.hasMoreTokens()) {
            lexemes.add(tokenizer.nextToken());
        }
        return consumeListOfConditions(lexemes);
    }

    @Nonnull
    private static List<ICondition> consumeListOfConditions(@Nonnull Queue<String> lexemes) {
        List<ICondition> result = new ArrayList<>();
        while (!lexemes.isEmpty()) {
            result.add(consumeSingleCondition(lexemes));
            if (!lexemes.isEmpty()) {
                String lookahead = lexemes.peek();
                if (")".equals(lookahead)) {
                    break;
                }
                if (!",".equals(lookahead)) {
                    throw new IllegalStateException("Invalid expression, expected ',' or ')'");
                }
                lexemes.remove();
            }
        }
        return result;
    }

    @Nonnull
    private static ICondition consumeSingleCondition(@Nonnull Queue<String> lexemes) {
        String nextToken = lexemes.poll();
        if (nextToken == null) {
            throw new IllegalStateException("Bug: should never be null, invokers responsibility");
        }
        ICondition nextCondition;
        if ("AllOf".equals(nextToken)) {
            ICondition[] conditions = consumeArgList(lexemes);
            if (Arrays.stream(conditions).allMatch(c -> c instanceof Is)) {
                nextCondition = new ContainsAll(Arrays.stream(conditions).map(c -> ((Is) c).value).collect(Collectors.toList()));
            } else {
                nextCondition = new AllOf(Arrays.stream(conditions).map(c -> c instanceof Is ? new ContainsAll(((Is) c).value) : c).toArray(ICondition[]::new));
            }
        } else if ("AnyOf".equals(nextToken)) {
            ICondition[] conditions = consumeArgList(lexemes);
            if (Arrays.stream(conditions).allMatch(c -> c instanceof Is)) {
                nextCondition = new ContainsAny(Arrays.stream(conditions).map(c -> ((Is) c).value).collect(Collectors.toList()));
            } else {
                nextCondition = new AnyOf(Arrays.stream(conditions).map(c -> c instanceof Is ? new ContainsAll(((Is) c).value) : c).toArray(ICondition[]::new));
            }
        } else if ("Not".equals(nextToken)) {
            ICondition[] conditions = consumeArgList(lexemes);
            if (Arrays.stream(conditions).allMatch(c -> c instanceof Is)) {
                nextCondition = new Not(new ContainsAny(Arrays.stream(conditions).map(c -> ((Is) c).value).collect(Collectors.toList())));
            } else {
                nextCondition = new Not(Arrays.stream(conditions).map(c -> c instanceof Is ? new ContainsAll(((Is) c).value) : c).toArray(ICondition[]::new));
            }
        } else if (ATOMIC_CONDITION.matcher(nextToken).matches()) {
            nextCondition = new Is(nextToken);
        } else {
            throw new IllegalStateException("Bad next token in expression, expected Condition: " + nextToken);
        }
        return nextCondition;
    }

    @Nonnull
    private static ICondition[] consumeArgList(@Nonnull Queue<String> lexemes) {
        if (lexemes.isEmpty()) {
            throw new IllegalStateException("Unexpected end of expression, expected '('");
        }
        String nextToken = lexemes.poll();
        if (!"(".equals(nextToken)) {
            throw new IllegalStateException("Bad next token in expression, expected '('");
        }
        List<ICondition> arguments = new ArrayList<>();
        while (!lexemes.isEmpty()) {
            String lookahead = lexemes.peek();
            if (")".equals(lookahead)) {
                break;
            }
            arguments.addAll(consumeListOfConditions(lexemes));
        }
        return arguments.toArray(new ICondition[0]);
    }

    private static class Is extends ContainsAll {
        private final String value;

        private Is(String value) {
            super(value);
            this.value = value;
        }
    }

    private static final Pattern NO_PARENS = Pattern.compile("^[\\w_\\-,]*$");
    private static final Pattern ATOMIC_CONDITION = Pattern.compile("^\\w+$");
}
