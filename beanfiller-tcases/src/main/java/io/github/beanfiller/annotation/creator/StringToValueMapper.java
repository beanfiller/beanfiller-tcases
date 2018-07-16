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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;

/**
 * Interface for classes emitting values of C based on String variable values
 */
public interface StringToValueMapper {

    /**
     * @return true if the implementig class is willing to be responsible to provide a value in getFieldValueAs.
     */
    boolean appliesTo(@Nonnull Field f);

    /**
     * @return an Instance suitable to be set as this fields value, may use field class or annotations.
     */
    @Nullable
    Object getFieldValueAs(@Nullable String valueString, @Nonnull Field targetField);

}
