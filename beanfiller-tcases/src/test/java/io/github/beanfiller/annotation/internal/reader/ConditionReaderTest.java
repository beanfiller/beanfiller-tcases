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
import org.cornutum.tcases.conditions.ConditionSet;
import org.cornutum.tcases.conditions.ContainsAll;
import org.cornutum.tcases.conditions.ContainsAny;
import org.cornutum.tcases.conditions.ICondition;
import org.cornutum.tcases.conditions.Not;
import org.cornutum.tcases.conditions.PropertyExpr;
import org.junit.Test;

import javax.annotation.Nullable;
import java.util.Iterator;

import static io.github.beanfiller.annotation.internal.reader.ConditionReader.getCondition;
import static io.github.beanfiller.annotation.internal.reader.ConditionReader.mergeArrays;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNull;

public class ConditionReaderTest {

    private static final String[] EMPTY = new String[0];

    private static String[] of(String... values) {
        return values;
    }

    @Test
    public void getConditionWhen() {
        assertNull(getCondition(null, EMPTY, EMPTY));
        assertEqualConditions(getCondition(null, of("foo"), EMPTY),
                new ContainsAll("foo"));
        assertEqualConditions(getCondition(null, of("foo", "bar"), EMPTY),
                new ContainsAll("foo", "bar"));
        assertEqualConditions(getCondition(null, of("foo,bar"), EMPTY),
                new ContainsAll("foo", "bar"));

        assertThat(getCondition(null, EMPTY, EMPTY)).isNull();
        assertEqualConditions(getCondition(null, of("AllOf(foo)"), EMPTY),
                new AllOf(new ContainsAll("foo")));
        assertEqualConditions(getCondition(null, of("AllOf(foo,bar)"), EMPTY),
                new AllOf(new ContainsAll("foo", "bar")));
        assertEqualConditions(getCondition(null, of("AllOf(foo,Not(bar))"), EMPTY),
                new AllOf(new AllOf(new ContainsAll("foo"), new Not(new ContainsAny("bar")))));

        assertEqualConditions(getCondition(null, of("AnyOf(foo)"), EMPTY),
                new AllOf(new ContainsAny("foo")));
        assertEqualConditions(getCondition(null, of("AnyOf(foo,bar)"), EMPTY),
                new AllOf(new ContainsAny("foo", "bar")));

        assertEqualConditions(getCondition(null, EMPTY, of("foo")),
                new Not(new ContainsAny("foo")));
        assertEqualConditions(getCondition(null, EMPTY, of("foo", "bar")),
                new Not(new ContainsAny("foo", "bar")));
        assertEqualConditions(getCondition(null, EMPTY, of("foo,bar")),
                new Not(new ContainsAny("foo", "bar")));

        assertEqualConditions(getCondition(null, EMPTY, of("AllOf(foo,bar)")),
                new Not(new AnyOf(new ContainsAll("foo", "bar"))));
        assertEqualConditions(getCondition(null, EMPTY, of("AnyOf(foo,bar)")),
                new Not(new AnyOf(new ContainsAny("foo", "bar"))));
        assertEqualConditions(getCondition(null, EMPTY, of("AnyOf(foo,Not(bar))")),
                new Not(new AnyOf(new AnyOf(
                        new ContainsAll("foo"),
                        new Not(new ContainsAny("bar"))))));
        assertEqualConditions(getCondition(null, of("Not(foo)"), EMPTY),
                new AllOf(new Not(new ContainsAny("foo"))));
        assertEqualConditions(getCondition(null, of("Not(AllOf(foo,Not(bar)))"), EMPTY),
                new AllOf(new Not(new AllOf(
                        new ContainsAll("foo"),
                        new Not(new ContainsAny("bar"))))));
    }


    private void assertEqualConditions(@Nullable ICondition actual, ICondition expected) {
        if (actual == null) {
            assertThat(expected).isNull();
            return;
        }

        assertThat(actual).isEqualToComparingFieldByFieldRecursively(expected);
        assertThat(actual.getClass()).isEqualTo(expected.getClass());

        if (actual instanceof ConditionSet) {
            final Iterator<ICondition> a = ((ConditionSet) actual).getConditions();
            final Iterator<ICondition> b = ((ConditionSet) expected).getConditions();
            while (a.hasNext() && b.hasNext()) {
                assertEqualConditions(a.next(), b.next());
            }
            assertThat(a.hasNext()).isFalse();
            assertThat(b.hasNext()).isFalse();
        } else if (actual instanceof PropertyExpr) {
            final Iterator<String> a = ((PropertyExpr) actual).getProperties();
            final Iterator<String> b = ((PropertyExpr) expected).getProperties();
            while (a.hasNext() && b.hasNext()) {
                assertThat(a.next()).isEqualTo(b.next());
            }
            assertThat(a.hasNext()).isFalse();
            assertThat(b.hasNext()).isFalse();
        }
    }

    @Test
    public void testMergeArrays() {
        assertThat(mergeArrays(null, new String[]{})).isEmpty();
        assertThat(mergeArrays(new String[]{}, new String[]{})).isEmpty();
        assertThat(mergeArrays(new String[]{"foo"}, new String[]{})).isEqualTo(new String[]{"foo"});
        assertThat(mergeArrays(new String[]{"foo"}, new String[]{"bar"})).isEqualTo(new String[]{"bar", "foo"});
    }


}