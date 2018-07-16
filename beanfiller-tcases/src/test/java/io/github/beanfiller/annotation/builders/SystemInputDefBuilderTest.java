package io.github.beanfiller.annotation.builders;

import org.cornutum.tcases.SystemInputDef;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static io.github.beanfiller.annotation.builders.FunctionInputDefBuilder.function;
import static io.github.beanfiller.annotation.builders.SystemInputDefBuilder.system;
import static io.github.beanfiller.annotation.builders.VarDefBuilder.varDef;

public class SystemInputDefBuilderTest {

    @Test
    public void testBuild() {
        assertThat(system("fooSystem")
                .build().getName())
                .isEqualTo("fooSystem");

        SystemInputDef fooSystem = system("fooSystem",
                function("fooFunction")
                        .addVarDef(varDef("foo").build())
                        .build())
                .build();
        assertThat(fooSystem).isNotNull();
    }
}