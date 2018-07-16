package io.github.beanfiller.annotation.creator;

import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import io.github.beanfiller.annotation.annotations.FunctionDef;
import io.github.beanfiller.annotation.annotations.Var;
import io.github.beanfiller.annotation.generator.GeneratorSetBuilder;
import io.github.beanfiller.annotation.reader.AnnotatedFunctionDefReader;
import org.cornutum.tcases.generator.GeneratorOptions;
import org.cornutum.tcases.generator.IGeneratorSet;
import org.cornutum.tcases.generator.ITestCaseGenerator;
import org.cornutum.tcases.generator.TupleGenerator;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class FunctionTestsCreatorTest {

    @FunctionDef("functionFooName")
    private static class Sample1 {

        @Var
        private Boolean fooVar;

        @Override
        public String toString() {
            return ReflectionToStringBuilder.toString(this);
        }
    }

    @Test
    public void createDefsDefaults() {
        FunctionTestsCreator<Sample1> functionTestsCreator = new FunctionTestsCreator<>(Sample1.class);
        assertThat(functionTestsCreator.getOptions()).isNotNull();
        assertThat(functionTestsCreator.getInstanceCreator()).isNotNull();
        assertThat(functionTestsCreator.getReader()).isNotNull();
        assertThat(functionTestsCreator.getGeneratorSet()).isNotNull();

        functionTestsCreator.tupleGenerator(4);
        List<ITestCaseGenerator> generators = IteratorUtils.toList(functionTestsCreator.getGeneratorSet().getGenerators());
        assertThat(generators).hasSize(1);
        assertThat(((TupleGenerator) generators.get(0)).getDefaultTupleSize()).isEqualTo(4);

    }

    @Test
    public void createDefs() {
        GeneratorOptions options = new GeneratorOptions();
        InstanceCreator creator = ReflectionBasedInstanceCreator.withDefaultMapper();
        AnnotatedFunctionDefReader reader = AnnotatedFunctionDefReader.withDefaultAnnotations();
        IGeneratorSet generatorSet = GeneratorSetBuilder.basicGeneratorSet().build();

        FunctionTestsCreator<Sample1> functionTestsCreator = new FunctionTestsCreator<>(Sample1.class)
                .generatorOptions(options)
                .instanceCreator(creator)
                .generatorSet(generatorSet)
                .reader(reader);

        assertThat(functionTestsCreator.getOptions()).isSameAs(options);
        assertThat(functionTestsCreator.getInstanceCreator()).isSameAs(creator);
        assertThat(functionTestsCreator.getReader()).isSameAs(reader);
        assertThat(functionTestsCreator.getGeneratorSet()).isSameAs(generatorSet);

        List<Sample1> funDefs = functionTestsCreator
                .createDefs();
        assertThat(funDefs).hasSize(3);
    }
}