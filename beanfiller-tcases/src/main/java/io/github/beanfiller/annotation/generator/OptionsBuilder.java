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

import org.cornutum.tcases.generator.GeneratorOptions;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class OptionsBuilder {

    private final Long randomSeed;
    private final Integer defaultTupleSize;

    public OptionsBuilder() {
        this(null, null);
    }

    public OptionsBuilder(@Nullable Long randomSeed, @Nullable Integer defaultTupleSize) {
        this.randomSeed = randomSeed;
        this.defaultTupleSize = defaultTupleSize;
    }

    @Nonnull
    public GeneratorOptions build() {
        GeneratorOptions options = new GeneratorOptions();
        options.setRandomSeed(randomSeed);
        options.setDefaultTupleSize(defaultTupleSize);
        return options;
    }
}
