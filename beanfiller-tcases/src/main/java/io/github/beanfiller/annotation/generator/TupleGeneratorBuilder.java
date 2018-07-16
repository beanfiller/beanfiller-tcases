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

import org.cornutum.tcases.generator.TupleCombiner;
import org.cornutum.tcases.generator.TupleGenerator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Fluent API builder for the TupleGenerator
 */
public class TupleGeneratorBuilder {

    private int tuples = 1;
    private Long myRandomSeed;
    private final List<TupleCombiner> combiners = new ArrayList<>();

    public TupleGeneratorBuilder() {
        this(1);
    }

    public TupleGeneratorBuilder(int tuples) {
        this.tuples = tuples;
    }

    @Nonnull
    public TupleGeneratorBuilder randomSeed(@Nullable Long randomSeed) {
        this.myRandomSeed = randomSeed;
        return this;
    }

    @Nonnull
    public TupleGeneratorBuilder combiner(TupleCombiner combiner) {
        if (combiner == null) {
            throw new NullPointerException("must not add null combiner");
        }
        this.combiners.add(combiner);
        return this;
    }

    @Nonnull
    public TupleGenerator build() {
        TupleGenerator tupleGenerator = new TupleGenerator(tuples);
        tupleGenerator.setCombiners(combiners);
        if (myRandomSeed != null) {
            tupleGenerator.setRandomSeed(myRandomSeed);
        }
        return tupleGenerator;
    }
}
