package io.github.beanfiller.annotation.reader;

import io.github.beanfiller.annotation.annotations.FunctionDef;
import io.github.beanfiller.annotation.annotations.SystemDef;
import io.github.beanfiller.annotation.annotations.Var;
import org.cornutum.tcases.SystemInputDef;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SystemDefReaderTest {

    @FunctionDef("functionFooName")
    private static class FooSample {

        @Var
        private Boolean fooVar;

    }

    @FunctionDef("functionBarName")
    private static class BarSample {

        @Var
        private Boolean barVar;

    }


    private static class EmptySystem {

    }

    @SystemDef(name = "secondEmptySystem")
    private static class EmptySystem2 {

    }

    @SystemDef
    @FunctionDef("functionName")
    private static class SystemWithFunction {
    }

    @SystemDef({FooSample.class, BarSample.class})
    private static class FooBarSystem {
    }

    @SystemDef({FooSample.class, BarSample.class, FooBarPlusSystem.class})
    @FunctionDef("rootFunctionName")
    private static class FooBarPlusSystem {
    }

    @Test
    public void readSystemDef() {
        SystemInputDef systemDef = new SystemDefReader().readSystemDefFromFunctionDefs("fooSystem", FooSample.class, BarSample.class);
        assertThat(systemDef.getName()).isEqualTo("fooSystem");
        assertThat(systemDef.getFunctionInputDefs()).hasSize(2);
        assertThat(systemDef.getFunctionInputDef("functionFooName"))
                .isNotNull();
        assertThat(systemDef.getFunctionInputDef("functionBarName"))
                .isNotNull();

        systemDef = new SystemDefReader().readSystemDef(FooSample.class);
        assertThat(systemDef.getName()).isEqualTo("FooSample");
        assertThat(systemDef.getFunctionInputDefs()).hasSize(1);
        assertThat(systemDef.getFunctionInputDef("functionFooName"))
                .isNotNull();

        systemDef = new SystemDefReader().readSystemDef(EmptySystem.class);
        assertThat(systemDef.getName()).isEqualTo("EmptySystem");
        assertThat(systemDef.getFunctionInputDefs()).hasSize(1);
        assertThat(systemDef.getFunctionInputDef("EmptySystem"))
                .isNotNull();

        systemDef = new SystemDefReader().readSystemDef(EmptySystem2.class);
        assertThat(systemDef.getName()).isEqualTo("secondEmptySystem");
        assertThat(systemDef.getFunctionInputDefs()).hasSize(1);
        assertThat(systemDef.getFunctionInputDef("EmptySystem2"))
                .isNotNull();

        systemDef = new SystemDefReader().readSystemDef(SystemWithFunction.class);
        assertThat(systemDef.getName()).isEqualTo("SystemWithFunction");
        assertThat(systemDef.getFunctionInputDefs()).hasSize(1);
        assertThat(systemDef.getFunctionInputDef("functionName"))
                .isNotNull();

        systemDef = new SystemDefReader().readSystemDef(FooBarSystem.class);
        assertThat(systemDef.getName()).isEqualTo("FooBarSystem");
        assertThat(systemDef.getFunctionInputDefs()).hasSize(2);
        assertThat(systemDef.getFunctionInputDef("functionFooName"))
                .isNotNull();
        assertThat(systemDef.getFunctionInputDef("functionBarName"))
                .isNotNull();

        systemDef = new SystemDefReader().readSystemDef(FooBarPlusSystem.class);
        assertThat(systemDef.getName()).isEqualTo("FooBarPlusSystem");
        assertThat(systemDef.getFunctionInputDefs()).hasSize(3);
        assertThat(systemDef.getFunctionInputDef("functionFooName"))
                .isNotNull();
        assertThat(systemDef.getFunctionInputDef("functionBarName"))
                .isNotNull();
        assertThat(systemDef.getFunctionInputDef("rootFunctionName"))
                .isNotNull();
    }

}