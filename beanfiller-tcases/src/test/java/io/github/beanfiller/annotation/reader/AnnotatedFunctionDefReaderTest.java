package io.github.beanfiller.annotation.reader;

import org.apache.commons.collections4.IteratorUtils;
import io.github.beanfiller.annotation.annotations.FunctionDef;
import io.github.beanfiller.annotation.annotations.Var;
import org.cornutum.tcases.FunctionInputDef;
import org.cornutum.tcases.IVarDef;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.fail;

public class AnnotatedFunctionDefReaderTest {

    @FunctionDef(value = "altName", having = {
            "foo:fooV",
            "bar:barV"
    })
    private static class Sample1 {

        private static Boolean ignoreStatic;

        @Var
        private Boolean fooVar;

    }

    @Test
    public void testDefaults() {
        FunctionInputDef inputDef = AnnotatedFunctionDefReader.withDefaultAnnotations()
                .readFunctionInputDef(Sample1.class);
        assertThat(inputDef.getName()).isEqualTo("altName");
        assertThat(inputDef.getVarDefs()).hasSize(1);
    }

    @Test
    public void testWithReaders() {
        assertThatThrownBy(() -> AnnotatedFunctionDefReader.withReaders())
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> AnnotatedFunctionDefReader.withReaders((VarDefReader[]) null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    public void testReadFunctionInputDef() {

        AnnotatedFunctionDefReader reader = AnnotatedFunctionDefReader.withDefaultAnnotations();

        FunctionInputDef funDef = reader.readFunctionInputDef(EmptyClass.class);
        assertThat(funDef).isNotNull();
        assertThat(funDef.getName()).isEqualTo("EmptyClass");
        assertThat(funDef.getVarDefs().hasNext()).isFalse();

        funDef = reader.readFunctionInputDef(EmptyClass2.class);
        assertThat(funDef).isNotNull();
        assertThat(funDef.getName()).isEqualTo("EmptyClass2");
        assertThat(funDef.getVarDefs().hasNext()).isFalse();

        try {
            reader.readFunctionInputDef(InvalidAnnotation.class);
            fail("Excpected IllegalStateException");
        } catch (IllegalStateException e) {
            // pass
        }

        funDef = reader.readFunctionInputDef(Sample1.class);
        assertThat(funDef.getName()).isEqualTo("altName");
        assertThat(funDef.getAnnotation("foo")).isEqualTo("fooV");
        assertThat(funDef.getAnnotation("bar")).isEqualTo("barV");
        List<IVarDef> varDefs = IteratorUtils.toList(funDef.getVarDefs());
        assertThat(varDefs).hasSize(1);
        assertThat(varDefs.get(0).getName()).isEqualTo("fooVar");
    }

    private static class EmptyClass {
    }

    @FunctionDef(" ")
    private static class EmptyClass2 {
    }

    private static class InvalidAnnotation {
        @Var
        public static Boolean staticField;
    }

}