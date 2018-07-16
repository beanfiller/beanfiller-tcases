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

import io.github.beanfiller.annotation.annotations.Value;
import io.github.beanfiller.annotation.annotations.Var;
import org.cornutum.tcases.IVarDef;
import org.cornutum.tcases.PropertySet;
import org.cornutum.tcases.VarDef;
import org.cornutum.tcases.VarSet;
import org.cornutum.tcases.VarValueDef;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static io.github.beanfiller.annotation.internal.reader.FieldWrapper.of;
import static io.github.beanfiller.annotation.internal.reader.FieldWrapperMock.stringField;


public class DefaultVarDefReaderTest {

    private static class SampleInvalid {

        @Var(@Value("foo"))
        @io.github.beanfiller.annotation.annotations.VarSet
        public String stringValue;

        public int notVarSet1;

        public Samples.CardinalityZeroToN notVarSet2;
    }

    @Test
    public void testAppliesTo() throws Exception {
        assertThat(new DefaultVarDefReader().appliesTo(stringField("foo"))).isTrue();
        assertThatThrownBy(() -> new DefaultVarDefReader().appliesTo(of(SampleInvalid.class.getDeclaredField("stringValue"))))
                .isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> new DefaultVarDefReader().appliesTo(of(SampleInvalid.class.getDeclaredField("notVarSet1"))))
                .isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> new DefaultVarDefReader().appliesTo(of(SampleInvalid.class.getDeclaredField("notVarSet2"))))
                .isInstanceOf(IllegalStateException.class);
        assertThat(new DefaultVarDefReader().appliesTo(of(Samples.class.getDeclaredField("stringValue")))).isTrue();
        assertThat(new DefaultVarDefReader().appliesTo(of(Samples.class.getDeclaredField("innerSamples")))).isTrue();
    }

    @Test
    public void testReadVarDefs() throws Exception {
        DefaultVarDefReader reader = new DefaultVarDefReader();

        Class<Samples> samplesClass = Samples.class;
        IVarDef stringValue = readVarDef(reader, samplesClass, "stringValue");
        assertThat(stringValue).isInstanceOf(VarDef.class);
        assertThat(stringValue.getName()).isEqualTo("stringValue");
        assertThat(stringValue.getValues()).hasOnlyOneElementSatisfying(vvd -> {
            assertThat(vvd.getName()).isEqualTo("foo");
            assertThat(vvd.getType()).isEqualTo(VarValueDef.Type.ONCE);
            assertThat(vvd.getAnnotation("k12")).isEqualTo("v12");
            assertThat(vvd.getProperties().contains("prop1")).isTrue();
            assertThat(vvd.getCondition().satisfied(new PropertySet("a"))).isFalse();
            assertThat(vvd.getCondition().satisfied(new PropertySet("x"))).isFalse();
            assertThat(vvd.getCondition().satisfied(new PropertySet("c1"))).isTrue();
            assertThat(vvd.getCondition().satisfied(new PropertySet("c1", "x"))).isFalse();
        });
        assertThat(stringValue.getCondition().satisfied(new PropertySet("a"))).isFalse();
        assertThat(stringValue.getCondition().satisfied(new PropertySet("x"))).isFalse();
        assertThat(stringValue.getCondition().satisfied(new PropertySet("c2"))).isTrue();
        assertThat(stringValue.getCondition().satisfied(new PropertySet("c2", "x"))).isFalse();

        assertThat(readVarDef(reader, samplesClass, "enumValue")).isInstanceOf(VarDef.class);

        assertThat(readVarDef(reader, samplesClass, "innerSamples")).isInstanceOf(VarSet.class);
        assertThat(readVarDef(reader, Samples.InnerSamples.class, "innerSamples2")).isInstanceOf(VarSet.class);
    }

    private static IVarDef readVarDef(DefaultVarDefReader reader, Class<?> clazz, String name) throws NoSuchFieldException {
        return reader.readVarDef(FieldWrapper.of(clazz.getDeclaredField(name)));
    }

    private static class Samples {

        @Var(value = @Value(value = "foo", having = "k12:v12", once = true, properties = "prop1", when = "c1", whenNot = "x"),
                having = "k1:v1", tag = "t1", when = "c2", whenNot = "x", nullable = false)
        public String stringValue;

        @Var
        public CardinalityZeroToN enumValue;

        @io.github.beanfiller.annotation.annotations.VarSet(having = "k2:v2")
        public InnerSamples innerSamples;

        public enum CardinalityZeroToN {
            NONE,
            ONE,
            MANY;
        }

        private static class InnerSamples {

            InnerSamples2 innerSamples2;

            private static class InnerSamples2 {

            }
        }
    }
}