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

package io.github.beanfiller.annotation.creator;

import org.cornutum.tcases.TestCase;

import javax.annotation.Nonnull;

/**
 * Clients may implement this to create custom strategy of writing values based on TCases test case
 */
public interface InstanceCreator {
    /**
     *
     * @param testCase definitions of values for all significant fields for one test case.
     * @param typeClass target type to create
     * @param outputAnnotations a prefilled container with annotations of the system (if any)
     * @param <T> Any Java type
     * @return an Object of type T, with all fields of T filled according to testCase, recursively
     */
    @Nonnull
    <T> T createDef(@Nonnull TestCase testCase, @Nonnull Class<T> typeClass, @Nonnull OutputAnnotationContainer outputAnnotations);
}
