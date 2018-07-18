package io.github.beanfiller.annotation.creator;

import io.github.beanfiller.annotation.internal.reader.FieldWrapper;
import org.cornutum.tcases.TestCase;
import org.cornutum.tcases.VarBinding;
import org.cornutum.tcases.VarNaBinding;
import org.junit.Test;

import javax.annotation.Nonnull;
import java.time.LocalDate;

import static io.github.beanfiller.annotation.creator.ReflectionBasedInstanceCreatorTest.Sample.Enum3Sample.A1;
import static io.github.beanfiller.annotation.reader.VarDefReader.INITIALIZE_TESTCASE_VARNAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ReflectionBasedInstanceCreatorTest {

    @Test
    public void createDef() {
        final InstanceCreator creator = ReflectionBasedInstanceCreator.withDefaultMapper();
        final TestCase testCase = new TestCase(1);
        testCase.addVarBinding(new VarBinding(INITIALIZE_TESTCASE_VARNAME, "true"));
        testCase.addVarBinding(new VarBinding("aString", "foo"));
        testCase.addVarBinding(new VarNaBinding("nullString", "nothing"));
        testCase.addVarBinding(new VarBinding("aPrimitiveBoolean", "true"));
        testCase.addVarBinding(new VarBinding("aBoolean", "true"));
        testCase.addVarBinding(new VarBinding("aPrimitiveInt", "42"));
        testCase.addVarBinding(new VarBinding("anInteger", "43"));
        testCase.addVarBinding(new VarBinding("enum3FieldVar", "A1"));
        testCase.addVarBinding(new VarBinding("nested.aNestedString", "bar"));
        // in case of bugs, set nested field but parent is null
        testCase.addVarBinding(new VarNaBinding("nullNested." + INITIALIZE_TESTCASE_VARNAME, "nothing"));
        testCase.addVarBinding(new VarNaBinding("nullNested.aNestedString", "doNotSet"));

        final OutputAnnotationContainer outputAnnotations = new OutputAnnotationContainer();
        final Sample instance = creator.createDef(testCase, Sample.class, outputAnnotations);

        assertThat(instance).isInstanceOf(Sample.class);
        assertThat(instance.aString).isEqualTo("foo");
        assertThat(instance.nullString).isNull();
        assertThat(instance.aPrimitiveBoolean).isEqualTo(true);
        assertThat(instance.aBoolean).isEqualTo(true);
        assertThat(instance.aPrimitiveInt).isEqualTo(42);
        assertThat(instance.anInteger).isEqualTo(43);
        assertThat(instance.enum3FieldVar).isEqualTo(A1);
        assertThat(instance.nested.aNestedString).isEqualTo("bar");
        assertThat(instance.nullNested).isNull();
        // TODO: More tests
    }

    public static class Sample extends AbstractTestInput {
        public String aString;

        public String nullString;

        public boolean aPrimitiveBoolean;

        public Boolean aBoolean;

        public int aPrimitiveInt;

        public Integer anInteger;

        public ReflectionBasedInstanceCreatorTest.Sample.Enum3Sample enum3FieldVar;

        public Nested nested;

        public Nested nullNested;

        public enum Enum3Sample {
            A1, A2, A3
        }

        private static class Nested {
            public String aNestedString;
        }
    }

    @Test
    public void createDefCustomMapper() {

        final VariableToClassValueMapper dateMapper = new DateMapper();
        final InstanceCreator creator = ReflectionBasedInstanceCreator.withMappers(dateMapper);
        final TestCase testCase = new TestCase(1);

        testCase.addVarBinding(new VarBinding("date", "2018-01-01"));
        testCase.addVarBinding(new VarNaBinding("nullDate", "ignore"));

        final OutputAnnotationContainer outputAnnotations = new OutputAnnotationContainer();
        final DateSample instance = creator.createDef(testCase, DateSample.class, outputAnnotations);

        assertThat(instance).isInstanceOf(DateSample.class);
        assertThat(instance.date).isEqualTo(LocalDate.of(2018, 1, 1));
        assertThat(instance.nullDate).isNull();

        final InstanceCreator badCreator = ReflectionBasedInstanceCreator.withDefaultMapper();
        assertThatThrownBy(() -> badCreator.createDef(testCase, DateSample.class, outputAnnotations))
                .isInstanceOf(RuntimeException.class);
    }

    private static class DateSample {
        public LocalDate date;
        public LocalDate nullDate;
    }


    private static class DateMapper implements VariableToClassValueMapper {

        @Override
        public boolean appliesTo(@Nonnull final String varname, @Nonnull final Class<?> parentClass) {
            final FieldWrapper f = getField(parentClass, varname);
            return LocalDate.class.isAssignableFrom(f.getType());
        }

        @Override
        public void setFieldValueAs(@Nonnull final String varname, @Nonnull final Object instance, @Nonnull final VarBinding varBinding) {
            setFieldValue(instance, varname, LocalDate.parse(varBinding.getValue()));
        }
    }

    private static class Foo {
        Foo(String arg) {
        }
    }

    @Test
    public void testInvalidConstructor() {
        final InstanceCreator creator = ReflectionBasedInstanceCreator.withDefaultMapper();
        final TestCase testCase = new TestCase(1);

        assertThatThrownBy(() -> creator.createDef(testCase, Foo.class, new OutputAnnotationContainer()))
                .isInstanceOf(RuntimeException.class)
                .hasCauseInstanceOf(NoSuchMethodException.class);
    }

    @Test
    public void testFillSpecialValues() {
        final InstanceCreator creator = ReflectionBasedInstanceCreator.withDefaultMapper();
        final TestCase testCase = new TestCase(42);
        testCase.setAnnotation("tcaseAnnoKey", "tcaseAnnoValue");
        final VarBinding varBinding = new VarBinding("aString", "foo");
        varBinding.setValueValid(false);
        varBinding.setAnnotation("varAnnoValue", "varAnnoValue");
        testCase.addVarBinding(varBinding);

        final OutputAnnotationContainer outputAnnotations = new OutputAnnotationContainer();
        final Sample instance = creator.createDef(testCase, Sample.class, outputAnnotations);
        assertThat(instance.getTestCaseId()).isEqualTo(42);
        assertThat(instance.isFailure()).isEqualTo(true);
        assertThat(instance.having()).isEqualTo(outputAnnotations);
        assertThat(instance.getTestCaseId()).isEqualTo(42);

        assertThat(outputAnnotations.getTestCaseAnnotation("tcaseAnnoKey")).isEqualTo("tcaseAnnoValue");
        assertThat(outputAnnotations.getVarBindingAnnotation("aString.varAnnoValue")).isEqualTo("varAnnoValue");

    }

    @Test
    public void testInvalidVar() {
        final TestCase testCase = new TestCase(2);
        testCase.addVarBinding(new VarBinding("foo", "bar"));

        final InstanceCreator creator = ReflectionBasedInstanceCreator.withDefaultMapper();
        assertThatThrownBy(() -> creator.createDef(testCase, Sample.class, new OutputAnnotationContainer()))
                .isInstanceOf(IllegalStateException.class);
    }
}