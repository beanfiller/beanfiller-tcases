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

import org.apache.commons.collections4.IteratorUtils;
import io.github.beanfiller.annotation.annotations.Value;
import io.github.beanfiller.annotation.annotations.Var;
import org.cornutum.tcases.PropertySet;
import org.cornutum.tcases.TestCase;
import org.cornutum.tcases.VarValueDef;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static io.github.beanfiller.annotation.internal.reader.VarValueDefReader.readVarValueDefs;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class VarValueDefReaderTest {

    @Test
    public void testReadVarValueDefsString() throws Exception {
        Class<StringSamples> fieldSamplesClass = StringSamples.class;

        try {
            getVarValueDefs("invalidNoVar", fieldSamplesClass, null);
            fail("expected IllegalStateException");
        } catch (IllegalStateException e) {
            // pass
        }

        try {
            getVarValueDefs("invalidNoValue", fieldSamplesClass, null);
            fail("expected IllegalStateException");
        } catch (IllegalStateException e) {
            // pass
        }

        assertThat(getVarValueDefs("aStringWithVar1Value", fieldSamplesClass, null),
                hasSize(2));
        assertThat(getVarValueDefs("aStringWithVar1ValueNullable", fieldSamplesClass, null),
                hasSize(1));

        List<VarValueDef> aStringOnceFailure = getVarValueDefs("aStringOnceFailure", fieldSamplesClass, null);
        assertThat(aStringOnceFailure, hasSize(2));
        assertThat(aStringOnceFailure.get(0).getType(), equalTo(VarValueDef.Type.FAILURE));

        try {
            getVarValueDefs("invalidDuplicate", fieldSamplesClass, null);
            fail("expected IllegalStateException");
        } catch (IllegalStateException e) {
            // pass
        }

        List<VarValueDef> aStringWithVar2Value
                = getVarValueDefs("aStringWithVar2Value", fieldSamplesClass, null);
        assertThat(aStringWithVar2Value, hasSize(3));
        VarValueDef fooValue = aStringWithVar2Value.get(0);
        VarValueDef barValue = aStringWithVar2Value.get(1);
        VarValueDef nullValue = aStringWithVar2Value.get(2);

        assertThat(fooValue.getType(), equalTo(VarValueDef.Type.ONCE));
        assertThat(barValue.getType(), equalTo(VarValueDef.Type.FAILURE));
        assertThat(nullValue.getType(), equalTo(VarValueDef.Type.VALID));

        assertTrue(fooValue.getProperties().contains("fooProp"));
        assertFalse(fooValue.getProperties().contains("barProp"));
        assertFalse(barValue.getProperties().contains("fooProp"));
        assertTrue(barValue.getProperties().contains("barProp"));

        List<String> fooAnnotations = IteratorUtils.toList(fooValue.getAnnotations());
        assertThat(fooAnnotations, equalTo(Arrays.asList("fooHasKey")));
        assertThat(fooValue.getAnnotation("fooHasKey"), equalTo("fooHasValue"));

        List<String> barAnnotations = IteratorUtils.toList(barValue.getAnnotations());
        assertThat(barAnnotations, equalTo(Arrays.asList("barHasKey")));
        assertThat(barValue.getAnnotation("barHasKey"), equalTo("barHasValue"));

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
        Class<BooleanFieldSamples> fieldSamplesClass = BooleanFieldSamples.class;
        assertThat(getVarValueDefs("aBoolean", fieldSamplesClass, null),
                hasSize(3));
        assertThat(getVarValueDefs("aPrimitiveBoolean", fieldSamplesClass, null),
                hasSize(2));
        assertThat(getVarValueDefs("aBooleanWithVar", fieldSamplesClass, null),
                hasSize(3));
        assertThat(getVarValueDefs("aBooleanWithVar1Value", fieldSamplesClass, null),
                hasSize(3));
        assertThat(getVarValueDefs("aBooleanWithVar1ValueNotNullable", fieldSamplesClass, null),
                hasSize(2));
        assertThat(getVarValueDefs("aBooleanWithVar1ValueExclude0", fieldSamplesClass, null),
                hasSize(2));

        List<VarValueDef> aBooleanOnceFailure = getVarValueDefs("aBooleanOnceFailure", fieldSamplesClass, null);
        assertThat(aBooleanOnceFailure, hasSize(3));
        assertThat(aBooleanOnceFailure.get(0).getType(), equalTo(VarValueDef.Type.FAILURE));

        try {
            getVarValueDefs("invalidDuplicate", fieldSamplesClass, null);
            fail("expected IllegalStateException");
        } catch (IllegalStateException e) {
            // pass
        }

        try {
            getVarValueDefs("invalidUnknown", fieldSamplesClass, null);
            fail("expected IllegalStateException");
        } catch (IllegalStateException e) {
            // pass
        }

        List<VarValueDef> aBooleanWithVar2Value
                = getVarValueDefs("aBooleanWithVar2Value", fieldSamplesClass, null);
        assertThat(aBooleanWithVar2Value, hasSize(3));
        VarValueDef trueValue = aBooleanWithVar2Value.get(0);
        VarValueDef falseValue = aBooleanWithVar2Value.get(1);

        assertThat(trueValue.getType(), equalTo(VarValueDef.Type.ONCE));
        assertThat(falseValue.getType(), equalTo(VarValueDef.Type.FAILURE));

        assertTrue(trueValue.getProperties().contains("trueProp"));
        assertFalse(trueValue.getProperties().contains("falseProp"));
        assertFalse(falseValue.getProperties().contains("trueProp"));
        assertTrue(falseValue.getProperties().contains("falseProp"));

        List<String> trueAnnotations = IteratorUtils.toList(trueValue.getAnnotations());
        assertThat(trueAnnotations, equalTo(Arrays.asList("trueHasKey")));
        assertThat(trueValue.getAnnotation("trueHasKey"), equalTo("trueHasValue"));

        List<String> falseAnnotations = IteratorUtils.toList(falseValue.getAnnotations());
        assertThat(falseAnnotations, equalTo(Arrays.asList("falseHasKey")));
        assertThat(falseValue.getAnnotation("falseHasKey"), equalTo("falseHasValue"));

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
        Class<IntegerFieldSamples> fieldSamplesClass = IntegerFieldSamples.class;
        assertThat(getVarValueDefs("anInteger", fieldSamplesClass, null),
                hasSize(3));
        assertThat(getVarValueDefs("aPrimitiveInteger", fieldSamplesClass, null),
                hasSize(2));
        assertThat(getVarValueDefs("anIntegerWithVar", fieldSamplesClass, null),
                hasSize(3));
        assertThat(getVarValueDefs("anIntegerWithVar1Value", fieldSamplesClass, null),
                hasSize(3));
        assertThat(getVarValueDefs("anIntegerWithVar1ValueNotNullable", fieldSamplesClass, null),
                hasSize(2));

        List<VarValueDef> anIntegerOnceFailure = getVarValueDefs("anIntegerOnceFailure", fieldSamplesClass, null);
        assertThat(anIntegerOnceFailure, hasSize(3));
        assertThat(anIntegerOnceFailure.get(0).getType(), equalTo(VarValueDef.Type.FAILURE));

        try {
            getVarValueDefs("invalidDuplicate", fieldSamplesClass, null);
            fail("expected IllegalStateException");
        } catch (IllegalStateException e) {
            // pass
        }

        try {
            getVarValueDefs("invalidDuplicateNull", fieldSamplesClass, null);
            fail("expected IllegalStateException");
        } catch (IllegalStateException e) {
            // pass
        }

        List<VarValueDef> anIntegerWithVar2Value
                = getVarValueDefs("anIntegerWithVar2Value", fieldSamplesClass, null);
        assertThat(anIntegerWithVar2Value, hasSize(3));
        VarValueDef trueValue = anIntegerWithVar2Value.get(0);
        VarValueDef falseValue = anIntegerWithVar2Value.get(1);

        assertThat(trueValue.getType(), equalTo(VarValueDef.Type.ONCE));
        assertThat(falseValue.getType(), equalTo(VarValueDef.Type.FAILURE));

        assertTrue(trueValue.getProperties().contains("trueProp"));
        assertFalse(trueValue.getProperties().contains("falseProp"));
        assertFalse(falseValue.getProperties().contains("trueProp"));
        assertTrue(falseValue.getProperties().contains("falseProp"));

        List<String> trueAnnotations = IteratorUtils.toList(trueValue.getAnnotations());
        assertThat(trueAnnotations, equalTo(Arrays.asList("trueHasKey")));
        assertThat(trueValue.getAnnotation("trueHasKey"), equalTo("trueHasValue"));

        List<String> falseAnnotations = IteratorUtils.toList(falseValue.getAnnotations());
        assertThat(falseAnnotations, equalTo(Arrays.asList("falseHasKey")));
        assertThat(falseValue.getAnnotation("falseHasKey"), equalTo("falseHasValue"));

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
        Class<EnumFieldSamples> fieldSamplesClass = EnumFieldSamples.class;

        try {
            getVarValueDefs("enum0Field", fieldSamplesClass, null);
            fail("expected IllegalStateException");
        } catch (IllegalStateException e) {
            // pass
        }

        assertThat(getVarValueDefs("enum1Field", fieldSamplesClass, null),
                hasSize(1));
        assertThat(getVarValueDefs("enum3Field", fieldSamplesClass, null),
                hasSize(3));
        assertThat(getVarValueDefs("enum3FieldVar", fieldSamplesClass, null),
                hasSize(4));
        assertThat(getVarValueDefs("enum3FieldVarWithNull", fieldSamplesClass, null),
                hasSize(4));
        assertThat(getVarValueDefs("enum3FieldVarNotNullable", fieldSamplesClass, null),
                hasSize(3));

        List<VarValueDef> aEnumOnceFailure = getVarValueDefs("anEnumOnceFailure", fieldSamplesClass, null);
        assertThat(aEnumOnceFailure, hasSize(2));
        assertThat(aEnumOnceFailure.get(0).getType(), equalTo(VarValueDef.Type.FAILURE));


        try {
            getVarValueDefs("invalidDuplicate", fieldSamplesClass, null);
            fail("expected IllegalStateException");
        } catch (IllegalStateException e) {
            // pass
        }

        try {
            getVarValueDefs("invalidUnknown", fieldSamplesClass, null);
            fail("expected IllegalStateException");
        } catch (IllegalStateException e) {
            // pass
            assert e != null;
        }

        List<VarValueDef> aEnumWithVar2Value
                = getVarValueDefs("enum3FieldVarFull", fieldSamplesClass, null);
        assertThat(aEnumWithVar2Value, hasSize(4));
        VarValueDef a1Value = aEnumWithVar2Value.get(0);
        VarValueDef a2Value = aEnumWithVar2Value.get(1);
        VarValueDef a3Value = aEnumWithVar2Value.get(2);
        // TODO: assert a4Value NA

        assertThat(a1Value.getType(), equalTo(VarValueDef.Type.ONCE));
        assertThat(a2Value.getType(), equalTo(VarValueDef.Type.FAILURE));

        assertTrue(a1Value.getProperties().contains("a1Prop"));
        assertFalse(a1Value.getProperties().contains("a2Prop"));
        assertFalse(a2Value.getProperties().contains("a1Prop"));
        assertTrue(a2Value.getProperties().contains("a2Prop"));
        assertFalse(a3Value.getProperties().contains("a2Prop"));
        assertFalse(a3Value.getProperties().contains("a1Prop"));

        List<String> a1Annotations = IteratorUtils.toList(a1Value.getAnnotations());
        assertThat(a1Annotations, equalTo(Arrays.asList("a1HasKey")));
        assertThat(a1Value.getAnnotation("a1HasKey"), equalTo("a1HasValue"));

        List<String> a2Annotations = IteratorUtils.toList(a2Value.getAnnotations());
        assertThat(a2Annotations, equalTo(Arrays.asList("a2HasKey")));
        assertThat(a2Value.getAnnotation("a2HasKey"), equalTo("a2HasValue"));

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

    private List<VarValueDef> getVarValueDefs(String name, Class<?> fieldSamplesClass1, String[] conditions) throws NoSuchFieldException {
        return readVarValueDefs(FieldWrapper.of(fieldSamplesClass1.getDeclaredField(name)), conditions);
    }
}