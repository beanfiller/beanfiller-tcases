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

import org.cornutum.tcases.conditions.AllOf;
import org.cornutum.tcases.conditions.AnyOf;
import org.cornutum.tcases.conditions.ContainsAll;
import org.cornutum.tcases.conditions.ContainsAny;
import org.cornutum.tcases.conditions.Not;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static io.github.beanfiller.annotation.internal.reader.ConditionReader.getCondition;
import static io.github.beanfiller.annotation.internal.reader.ConditionReader.mergeArrays;
import static org.junit.Assert.assertNull;

public class ConditionReaderTest {

    private static final String[] EMPTY = new String[0];

    private static String[] of(String... values) {
        return values;
    }

    @Test
    public void getConditionWhen() {
        assertNull(getCondition(null, EMPTY, EMPTY));
        assertThat(getCondition(null, of("foo"), EMPTY))
                .isEqualToComparingFieldByFieldRecursively(new ContainsAll("foo"));
        assertThat(getCondition(null, of("foo", "bar"), EMPTY))
                .isEqualToComparingFieldByFieldRecursively(new ContainsAll("foo", "bar"));
        assertThat(getCondition(null, of("foo,bar"), EMPTY))
                .isEqualToComparingFieldByFieldRecursively(new ContainsAll("foo", "bar"));

        assertThat(getCondition(null, EMPTY, EMPTY)).isNull();
        assertThat(getCondition(null, of("AllOf(foo)"), EMPTY))
                .isEqualToComparingFieldByFieldRecursively(new AllOf(new ContainsAll("foo")));
        assertThat(getCondition(null, of("AllOf(foo,bar)"), EMPTY))
                .isEqualToComparingFieldByFieldRecursively(new AllOf(new ContainsAll("foo", "bar")));
        assertThat(getCondition(null, of("AllOf(foo,Not(bar))"), EMPTY))
                .isEqualToComparingFieldByFieldRecursively(new AllOf(new AllOf(
                        new ContainsAll("foo"),
                        new Not(new ContainsAny("bar")))));

        assertThat(getCondition(null, of("AnyOf(foo)"), EMPTY))
                .isEqualToComparingFieldByFieldRecursively(new AllOf(new ContainsAny("foo")));
        assertThat(getCondition(null, of("AnyOf(foo,bar)"), EMPTY))
                .isEqualToComparingFieldByFieldRecursively(new AllOf(new ContainsAny("foo", "bar")));

        assertThat(getCondition(null, EMPTY, of("foo")))
                .isEqualToComparingFieldByFieldRecursively(new Not(new ContainsAny("foo")));
        assertThat(getCondition(null, EMPTY, of("foo", "bar")))
                .isEqualToComparingFieldByFieldRecursively(new Not(new ContainsAny("foo", "bar")));
        assertThat(getCondition(null, EMPTY, of("foo,bar")))
                .isEqualToComparingFieldByFieldRecursively(new Not(new ContainsAny("foo", "bar")));

        assertThat(getCondition(null, EMPTY, of("AllOf(foo,bar)")))
                .isEqualToComparingFieldByFieldRecursively(new Not(new AllOf(
                        new ContainsAny("foo", "bar"))));
        assertThat(getCondition(null, EMPTY, of("AnyOf(foo,bar)")))
                .isEqualToComparingFieldByFieldRecursively(new Not(new AnyOf(
                        new ContainsAny("foo", "bar"))));
        assertThat(getCondition(null, EMPTY, of("AnyOf(foo,Not(bar))")))
                .isEqualToComparingFieldByFieldRecursively(
                        new Not(new AnyOf(new AnyOf(new ContainsAny("foo"),
                                new Not(new ContainsAny("bar"))))));
        assertThat(getCondition(null, of("Not(foo)"), EMPTY))
                .isEqualToComparingFieldByFieldRecursively(new AllOf(
                        new Not(new ContainsAny("foo"))));
        assertThat(getCondition(null, of("Not(AllOf(foo,Not(bar)))"), EMPTY))
                .isEqualToComparingFieldByFieldRecursively(new AllOf(
                        new Not(new AllOf(new ContainsAll("foo"),
                                new Not(new ContainsAny("bar"))))));
    }

    @Test
    public void testMergeArrays() {
        assertThat(mergeArrays(null, new String[]{})).isEmpty();
        assertThat(mergeArrays(new String[]{}, new String[]{})).isEmpty();
        assertThat(mergeArrays(new String[]{"foo"}, new String[]{})).isEqualTo(new String[]{"foo"});
        assertThat(mergeArrays(new String[]{"foo"}, new String[]{"bar"})).isEqualTo(new String[]{"bar", "foo"});
    }


}