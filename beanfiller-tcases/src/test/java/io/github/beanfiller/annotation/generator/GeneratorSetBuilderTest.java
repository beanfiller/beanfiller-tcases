package io.github.beanfiller.annotation.generator;

import org.cornutum.tcases.generator.IGeneratorSet;
import org.cornutum.tcases.generator.ITestCaseGenerator;
import org.cornutum.tcases.generator.TupleGenerator;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

public class GeneratorSetBuilderTest {

    @Test
    public void build() {
        IGeneratorSet genSet = GeneratorSetBuilder.basicGeneratorSet().build();
        assertThat(genSet.getGenerators()).hasOnlyOneElementSatisfying(gen -> {
            assertThat(gen).isInstanceOf(TupleGenerator.class);
            assertThat(((TupleGenerator) gen).getDefaultTupleSize()).isEqualTo(1);
        });

        genSet = new GeneratorSetBuilder().build();
        assertThat(genSet.getGenerators()).hasOnlyOneElementSatisfying(gen -> {
            assertThat(gen).isInstanceOf(TupleGenerator.class);
            assertThat(((TupleGenerator) gen).getDefaultTupleSize()).isEqualTo(1);
        });

        genSet = new GeneratorSetBuilder()
                .tupleGenerator("fooFun", 3)
                .build();
        assertThat(genSet.getGenerators()).hasOnlyOneElementSatisfying(gen -> {
            assertThat(gen).isInstanceOf(TupleGenerator.class);
            assertThat(((TupleGenerator) gen).getDefaultTupleSize()).isEqualTo(3);
        });

        ITestCaseGenerator genMock = mock(ITestCaseGenerator.class);
        genSet = new GeneratorSetBuilder()
                .generator("fooFun", genMock)
                .build();
        assertThat(genSet.getGenerators()).hasOnlyOneElementSatisfying(gen -> {
            assertThat(gen).isSameAs(genMock);
        });

        assertThatThrownBy(() -> new GeneratorSetBuilder()
                .generator(null, genMock)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new GeneratorSetBuilder()
                .generator("", genMock)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new GeneratorSetBuilder()
                .generator("a b", genMock)).isInstanceOf(IllegalArgumentException.class);
    }
}