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

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.github.beanfiller.annotation.annotations.Value;
import io.github.beanfiller.annotation.annotations.Var;
import org.apache.commons.collections4.IteratorUtils;
import org.cornutum.tcases.PropertySet;
import org.cornutum.tcases.TestCase;
import org.cornutum.tcases.VarValueDef;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static io.github.beanfiller.annotation.internal.reader.VarValueDefReader.readVarValueDefs;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SuppressFBWarnings("UPM_UNCALLED_PRIVATE_METHOD")
public class VarValueDefReaderTest {

    @Test
    public void testReadVarValueDefsString() throws Exception {
        final Class<StringSamples> fieldSamplesClass = StringSamples.class;



        assertThatThrownBy(() -> getVarValueDefs("invalidNoVar", fieldSamplesClass, (String) null))
                .isInstanceOf(IllegalStateException.class);

        assertThatThrownBy(() -> getVarValueDefs("invalidNoValue", fieldSamplesClass, (String) null))
                .isInstanceOf(IllegalStateException.class);

        assertThat(getVarValueDefs("aStringWithVar1Value", fieldSamplesClass, (String) null))
                .hasSize(2);
        assertThat(getVarValueDefs("aStringWithVar1ValueNullable", fieldSamplesClass, (String) null))
                .hasSize(1);

        final List<VarValueDef> aStringOnceFailure = getVarValueDefs("aStringOnceFailure", fieldSamplesClass, (String) null);
        assertThat(aStringOnceFailure).hasSize(2);
        assertThat(aStringOnceFailure.get(0).getType()).isEqualTo(VarValueDef.Type.FAILURE);

        assertThatThrownBy(() -> getVarValueDefs("invalidDuplicate", fieldSamplesClass, (String) null))
            .isInstanceOf(IllegalStateException.class);

        final List<VarValueDef> aStringWithVar2Value
                = getVarValueDefs("aStringWithVar2Value", fieldSamplesClass, (String) null);
        assertThat(aStringWithVar2Value).hasSize(3);
        final VarValueDef fooValue = aStringWithVar2Value.get(0);
        final VarValueDef barValue = aStringWithVar2Value.get(1);
        final VarValueDef nullValue = aStringWithVar2Value.get(2);

        assertThat(fooValue.getType()).isEqualTo(VarValueDef.Type.ONCE);
        assertThat(barValue.getType()).isEqualTo(VarValueDef.Type.FAILURE);
        assertThat(nullValue.getType()).isEqualTo(VarValueDef.Type.VALID);

        assertTrue(fooValue.getProperties().contains("fooProp"));
        assertFalse(fooValue.getProperties().contains("barProp"));
        assertFalse(barValue.getProperties().contains("fooProp"));
        assertTrue(barValue.getProperties().contains("barProp"));

        final List<String> fooAnnotations = IteratorUtils.toList(fooValue.getAnnotations());
        assertThat(fooAnnotations).isEqualTo(Arrays.asList("fooHasKey"));
        assertThat(fooValue.getAnnotation("fooHasKey")).isEqualTo("fooHasValue");

        final List<String> barAnnotations = IteratorUtils.toList(barValue.getAnnotations());
        assertThat(barAnnotations).isEqualTo(Arrays.asList("barHasKey"));
        assertThat(barValue.getAnnotation("barHasKey")).isEqualTo("barHasValue");

        assertTrue(fooValue.getCondition().satisfied(new PropertySet("fooWhen")));
        assertFalse(fooValue.getCondition().satisfied(new PropertySet()));
        assertFalse(barValue.getCondition().satisfied(new PropertySet("fooWhen")));
        assertTrue(barValue.getCondition().satisfied(new PropertySet()));
    }


    private static class StringSamples {
        public String invalidNoVar;

        @Var
        public String invalidNoValue;

        @Var(@Value("foo"))
        public String aStringWithVar1Value;

        @Var(value = @Value("foo"), nullable = false)
        public String aStringWithVar1ValueNullable;

        @Var({
                @Value(value = "foo",
                        properties = {"fooProp"},
                        once = true,
                        when = "fooWhen",
                        having = "fooHasKey:fooHasValue"),
                @Value(value = "bar",
                        properties = {"barProp"},
                        whenNot = "fooWhen",
                        type = TestCase.Type.FAILURE,
                        having = "barHasKey:barHasValue")
        })
        public String aStringWithVar2Value;

        @Var({@Value(value = "fail", once = true, type = TestCase.Type.FAILURE)})
        public String aStringOnceFailure;

        @Var({@Value("fail"), @Value("fail")})
        public String invalidDuplicate;
    }



    @Test
    public void testReadVarValueDefsBoolean() throws Exception {
        final Class<BooleanFieldSamples> fieldSamplesClass = BooleanFieldSamples.class;
        assertThat(getVarValueDefs("aBoolean", fieldSamplesClass, (String) null))
                .hasSize(3);
        assertThat(getVarValueDefs("aPrimitiveBoolean", fieldSamplesClass, (String) null))
                .hasSize(2);
        assertThat(getVarValueDefs("aBooleanWithVar", fieldSamplesClass, (String) null))
                .hasSize(3);
        assertThat(getVarValueDefs("aBooleanWithVar1Value", fieldSamplesClass, (String) null))
                .hasSize(3);
        assertThat(getVarValueDefs("aBooleanWithVar1ValueNotNullable", fieldSamplesClass, (String) null))
                .hasSize(2);
        assertThat(getVarValueDefs("aBooleanWithVar1ValueExclude0", fieldSamplesClass, (String) null))
                .hasSize(2);

        final List<VarValueDef> aBooleanOnceFailure = getVarValueDefs("aBooleanOnceFailure", fieldSamplesClass, (String) null);
        assertThat(aBooleanOnceFailure).hasSize(3);
        assertThat(aBooleanOnceFailure.get(0).getType()).isEqualTo(VarValueDef.Type.FAILURE);

        assertThatThrownBy(() -> getVarValueDefs("invalidDuplicate", fieldSamplesClass, (String) null))
            .isInstanceOf(IllegalStateException.class);

        assertThatThrownBy(() -> getVarValueDefs("invalidUnknown", fieldSamplesClass, (String) null))
            .isInstanceOf(IllegalStateException.class);

        final List<VarValueDef> aBooleanWithVar2Value
                = getVarValueDefs("aBooleanWithVar2Value", fieldSamplesClass, (String) null);
        assertThat(aBooleanWithVar2Value).hasSize(3);
        final VarValueDef trueValue = aBooleanWithVar2Value.get(0);
        final VarValueDef falseValue = aBooleanWithVar2Value.get(1);

        assertThat(trueValue.getType()).isEqualTo(VarValueDef.Type.ONCE);
        assertThat(falseValue.getType()).isEqualTo(VarValueDef.Type.FAILURE);

        assertTrue(trueValue.getProperties().contains("trueProp"));
        assertFalse(trueValue.getProperties().contains("falseProp"));
        assertFalse(falseValue.getProperties().contains("trueProp"));
        assertTrue(falseValue.getProperties().contains("falseProp"));

        final List<String> trueAnnotations = IteratorUtils.toList(trueValue.getAnnotations());
        assertThat(trueAnnotations).isEqualTo(Arrays.asList("trueHasKey"));
        assertThat(trueValue.getAnnotation("trueHasKey")).isEqualTo("trueHasValue");

        final List<String> falseAnnotations = IteratorUtils.toList(falseValue.getAnnotations());
        assertThat(falseAnnotations).isEqualTo(Arrays.asList("falseHasKey"));
        assertThat(falseValue.getAnnotation("falseHasKey")).isEqualTo("falseHasValue");

        assertTrue(trueValue.getCondition().satisfied(new PropertySet("trueWhen")));
        assertFalse(trueValue.getCondition().satisfied(new PropertySet()));
        assertFalse(falseValue.getCondition().satisfied(new PropertySet("trueWhen")));
        assertTrue(falseValue.getCondition().satisfied(new PropertySet()));
    }


    private static class BooleanFieldSamples {

        public boolean aPrimitiveBoolean;

        public Boolean aBoolean;

        @Var
        public Boolean aBooleanWithVar;

        @Var(@Value("true"))
        public Boolean aBooleanWithVar1Value;

        @Var(value = @Value("true"), nullable = false)
        public Boolean aBooleanWithVar1ValueNotNullable;

        @Var(nullable = false)
        public Boolean aBooleanWithVar1ValueExclude0;

        @Var({
                @Value(value = "true",
                        properties = {"trueProp"},
                        once = true,
                        when = "trueWhen",
                        having = "trueHasKey:trueHasValue"),
                @Value(value = "false",
                        properties = {"falseProp"},
                        whenNot = "trueWhen",
                        type = TestCase.Type.FAILURE,
                        having = "falseHasKey:falseHasValue")
        })
        public Boolean aBooleanWithVar2Value;

        @Var({@Value(value = "true", once = true, type = TestCase.Type.FAILURE)})
        public Boolean aBooleanOnceFailure;

        @Var({@Value("true"), @Value("true")})
        public Boolean invalidDuplicate;

        @Var({@Value("unknown")})
        public Boolean invalidUnknown;
    }

    @Test
    public void testReadVarValueDefsInteger() throws Exception {
        final Class<IntegerFieldSamples> fieldSamplesClass = IntegerFieldSamples.class;
        assertThat(getVarValueDefs("anInteger", fieldSamplesClass, (String) null))
                .hasSize(3);
        assertThat(getVarValueDefs("aPrimitiveInteger", fieldSamplesClass, (String) null))
                .hasSize(2);
        assertThat(getVarValueDefs("anIntegerWithVar", fieldSamplesClass, (String) null))
                .hasSize(3);
        assertThat(getVarValueDefs("anIntegerWithVar1Value", fieldSamplesClass, (String) null))
                .hasSize(3);
        assertThat(getVarValueDefs("anIntegerWithVar1ValueNotNullable", fieldSamplesClass, (String) null))
                .hasSize(2);

        final List<VarValueDef> anIntegerOnceFailure = getVarValueDefs("anIntegerOnceFailure", fieldSamplesClass, (String) null);
        assertThat(anIntegerOnceFailure).hasSize(3);
        assertThat(anIntegerOnceFailure.get(0).getType()).isEqualTo(VarValueDef.Type.FAILURE);

        assertThatThrownBy(() -> getVarValueDefs("invalidDuplicate", fieldSamplesClass, (String) null))
            .isInstanceOf(IllegalStateException.class);

        assertThatThrownBy(() -> getVarValueDefs("invalidDuplicateNull", fieldSamplesClass, (String) null))
            .isInstanceOf(IllegalStateException.class);

        final List<VarValueDef> anIntegerWithVar2Value
                = getVarValueDefs("anIntegerWithVar2Value", fieldSamplesClass, (String) null);
        assertThat(anIntegerWithVar2Value).hasSize(3);
        final VarValueDef trueValue = anIntegerWithVar2Value.get(0);
        final VarValueDef falseValue = anIntegerWithVar2Value.get(1);

        assertThat(trueValue.getType()).isEqualTo(VarValueDef.Type.ONCE);
        assertThat(falseValue.getType()).isEqualTo(VarValueDef.Type.FAILURE);

        assertTrue(trueValue.getProperties().contains("trueProp"));
        assertFalse(trueValue.getProperties().contains("falseProp"));
        assertFalse(falseValue.getProperties().contains("trueProp"));
        assertTrue(falseValue.getProperties().contains("falseProp"));

        final List<String> trueAnnotations = IteratorUtils.toList(trueValue.getAnnotations());
        assertThat(trueAnnotations).isEqualTo(Arrays.asList("trueHasKey"));
        assertThat(trueValue.getAnnotation("trueHasKey")).isEqualTo("trueHasValue");

        final List<String> falseAnnotations = IteratorUtils.toList(falseValue.getAnnotations());
        assertThat(falseAnnotations).isEqualTo(Arrays.asList("falseHasKey"));
        assertThat(falseValue.getAnnotation("falseHasKey")).isEqualTo("falseHasValue");

        assertTrue(trueValue.getCondition().satisfied(new PropertySet("trueWhen")));
        assertFalse(trueValue.getCondition().satisfied(new PropertySet()));
        assertFalse(falseValue.getCondition().satisfied(new PropertySet("trueWhen")));
        assertTrue(falseValue.getCondition().satisfied(new PropertySet()));
    }


    private static class IntegerFieldSamples {

        @Var({@Value("1"), @Value("2")})
        public int aPrimitiveInteger;

        @Var({@Value("1"), @Value("2")})
        public Integer anInteger;

        @Var({@Value("1"), @Value("2")})
        public Integer anIntegerWithVar;

        @Var({@Value("1"), @Value("2")})
        public Integer anIntegerWithVar1Value;

        @Var(value = {@Value("1"), @Value("2")}, nullable = false)
        public Integer anIntegerWithVar1ValueNotNullable;

        @Var({
                @Value(value = "1",
                        properties = {"trueProp"},
                        once = true,
                        when = "trueWhen",
                        having = "trueHasKey:trueHasValue"),
                @Value(value = "2",
                        properties = {"falseProp"},
                        whenNot = "trueWhen",
                        type = TestCase.Type.FAILURE,
                        having = "falseHasKey:falseHasValue")
        })
        public Integer anIntegerWithVar2Value;

        @Var({@Value(value = "1", once = true, type = TestCase.Type.FAILURE), @Value("2")})
        public Integer anIntegerOnceFailure;

        @Var({@Value("1"), @Value("1")})
        public Integer invalidDuplicate;

        @Var({@Value(value = "unknown", isNull = true), @Value(value = "unknown2", isNull = true)})
        public Integer invalidDuplicateNull;
    }

    @Test
    public void testReadVarValueDefsEnum() throws Exception {
        final Class<EnumFieldSamples> fieldSamplesClass = EnumFieldSamples.class;

        assertThatThrownBy(() -> getVarValueDefs("enum0Field", fieldSamplesClass, (String) null))
            .isInstanceOf(IllegalStateException.class);

        assertThat(getVarValueDefs("enum1Field", fieldSamplesClass, (String) null))
                .hasSize(1);
        assertThat(getVarValueDefs("enum3Field", fieldSamplesClass, (String) null))
                .hasSize(3);
        assertThat(getVarValueDefs("enum3FieldVar", fieldSamplesClass, (String) null))
                .hasSize(4);
        assertThat(getVarValueDefs("enum3FieldVarWithNull", fieldSamplesClass, (String) null))
                .hasSize(4);
        assertThat(getVarValueDefs("enum3FieldVarNotNullable", fieldSamplesClass, (String) null))
                .hasSize(3);

        final List<VarValueDef> aEnumOnceFailure = getVarValueDefs("anEnumOnceFailure", fieldSamplesClass, (String) null);
        assertThat(aEnumOnceFailure).hasSize(2);
        assertThat(aEnumOnceFailure.get(0).getType()).isEqualTo(VarValueDef.Type.FAILURE);


        assertThatThrownBy(() -> getVarValueDefs("invalidDuplicate", fieldSamplesClass, (String) null))
            .isInstanceOf(IllegalStateException.class);

        assertThatThrownBy(() -> getVarValueDefs("invalidUnknown", fieldSamplesClass, (String) null))
            .isInstanceOf(IllegalStateException.class);

        final List<VarValueDef> aEnumWithVar2Value
                = getVarValueDefs("enum3FieldVarFull", fieldSamplesClass, (String) null);
        assertThat(aEnumWithVar2Value).hasSize(4);
        final VarValueDef a1Value = aEnumWithVar2Value.get(0);
        final VarValueDef a2Value = aEnumWithVar2Value.get(1);
        final VarValueDef a3Value = aEnumWithVar2Value.get(2);
        // TODO: assert a4Value NA

        assertThat(a1Value.getType()).isEqualTo(VarValueDef.Type.ONCE);
        assertThat(a2Value.getType()).isEqualTo(VarValueDef.Type.FAILURE);

        assertTrue(a1Value.getProperties().contains("a1Prop"));
        assertFalse(a1Value.getProperties().contains("a2Prop"));
        assertFalse(a2Value.getProperties().contains("a1Prop"));
        assertTrue(a2Value.getProperties().contains("a2Prop"));
        assertFalse(a3Value.getProperties().contains("a2Prop"));
        assertFalse(a3Value.getProperties().contains("a1Prop"));

        final List<String> a1Annotations = IteratorUtils.toList(a1Value.getAnnotations());
        assertThat(a1Annotations).isEqualTo(Arrays.asList("a1HasKey"));
        assertThat(a1Value.getAnnotation("a1HasKey")).isEqualTo("a1HasValue");

        final List<String> a2Annotations = IteratorUtils.toList(a2Value.getAnnotations());
        assertThat(a2Annotations).isEqualTo(Arrays.asList("a2HasKey"));
        assertThat(a2Value.getAnnotation("a2HasKey")).isEqualTo("a2HasValue");

        assertTrue(a1Value.getCondition().satisfied(new PropertySet("aWhen")));
        assertFalse(a1Value.getCondition().satisfied(new PropertySet()));
        assertFalse(a2Value.getCondition().satisfied(new PropertySet("aWhen")));
        assertTrue(a2Value.getCondition().satisfied(new PropertySet()));
        assertTrue(a3Value.getCondition().satisfied(new PropertySet()));
        assertTrue(a3Value.getCondition().satisfied(new PropertySet("aWhen")));
    }

    private static class EnumFieldSamples {

        public Enum0Sample enum0Field;
        public Enum1Sample enum1Field;
        public Enum3Sample enum3Field;

        @Var
        public Enum3Sample enum3FieldVar;

        @Var(@Value(value = "NA", isNull = true))
        public Enum3Sample enum3FieldVarWithNull;

        @Var(nullable = false)
        public Enum3Sample enum3FieldVarNotNullable;

        @Var({
                @Value(value = "A1",
                        properties = {"a1Prop"},
                        once = true,
                        when = "aWhen",
                        having = "a1HasKey:a1HasValue"),
                @Value(value = "A2",
                        properties = {"a2Prop"},
                        type = TestCase.Type.FAILURE,
                        whenNot = "aWhen",
                        having = "a2HasKey:a2HasValue")}
        )
        public Enum3Sample enum3FieldVarFull;
        @Var({@Value(value = "A1", once = true, type = TestCase.Type.FAILURE)})
        public Enum1Sample anEnumOnceFailure;
        @Var({@Value("A1"), @Value("A1")})
        public Enum3Sample invalidDuplicate;
        @Var({@Value("A4")})
        public Enum3Sample invalidUnknown;

        public enum Enum0Sample {
        }

        public enum Enum1Sample {
            A1
        }

        public enum Enum3Sample {
            A1, A2, A3
        }
    }


    private List<VarValueDef> getVarValueDefs(String name, Class<?> fieldSamplesClass1, String... conditions) throws NoSuchFieldException {
        return readVarValueDefs(FieldWrapper.of(fieldSamplesClass1.getDeclaredField(name)), conditions);
    }
}