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

package io.github.beanfiller.annotation.generator;

import org.cornutum.tcases.generator.GeneratorSet;
import org.cornutum.tcases.generator.IGeneratorSet;
import org.cornutum.tcases.generator.ITestCaseGenerator;
import org.cornutum.tcases.generator.TupleGenerator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * Fluent API builder for GeneratorSets
 */
public class GeneratorSetBuilder {

    private final Map<String, ITestCaseGenerator> definitions;
    private ITestCaseGenerator myDefaultGenerator;

    public GeneratorSetBuilder() {
        definitions = new HashMap<>();
    }

    /**
     * preconfigured with 1-tuple TupleGenerator as fallback generator
     *
     * @return
     */
    public static GeneratorSetBuilder basicGeneratorSet() {
        return basicGeneratorSet(1);
    }

    /**
     * preconfigured with 1-tuple TupleGenerator as fallback generator
     *
     * @return
     */
    @Nonnull
    public static GeneratorSetBuilder basicGeneratorSet(int tuples) {
        final GeneratorSetBuilder generatorSetBuilder = new GeneratorSetBuilder();
        generatorSetBuilder.defaultGenerator(new TupleGenerator(tuples));
        return generatorSetBuilder;
    }

    /**
     * The generator to use when no function-specific generator was declared.
     */
    @Nonnull
    public GeneratorSetBuilder defaultGenerator(@Nullable ITestCaseGenerator testCaseGenerator) {
        myDefaultGenerator = testCaseGenerator;
        return this;
    }

    /**
     * Add any TCases Generator
     * @param functionName name of function to apply to. Must not be null
     */
    @Nonnull
    public GeneratorSetBuilder generator(@Nullable String functionName, ITestCaseGenerator testCaseGenerator) {
        if (functionName == null || !functionName.matches("^\\w+$")) {
            throw new IllegalArgumentException("Invalid function name " + functionName);
        }
        definitions.put(functionName, testCaseGenerator);
        return this;
    }

    /**
     * specific convenience generator for TCases tupleGenerator
     * @param functionName name of function to apply to. Must not be null
     */
    @Nonnull
    public GeneratorSetBuilder tupleGenerator(@Nullable String functionName, int tuples) {
        return generator(functionName, new TupleGenerator(tuples));
    }

    @Nonnull
    public IGeneratorSet build() {
        final GeneratorSet generatorSet;
        if (definitions.isEmpty() && myDefaultGenerator == null) {
            generatorSet = GeneratorSet.basicGenerator();
        } else {
            generatorSet = new GeneratorSet();
            definitions.forEach(generatorSet::setGenerator);
            if (myDefaultGenerator != null) {
                generatorSet.addGenerator(GeneratorSet.ALL, myDefaultGenerator);
            }
        }
        return generatorSet;
    }
}
