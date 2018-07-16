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

package io.github.beanfiller.annotation.reader;

import io.github.beanfiller.annotation.internal.reader.FieldWrapper;
import org.cornutum.tcases.IVarDef;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Interface for classes defining Tcases values based on a java field.
 */
public interface VarDefReader {

    /**
     * A variable named this indicates two variants, one where the child variables get initialized, one where not
     */
    String INITIALIZE_TESTCASE_VARNAME = "__init";

    /**
     * @return true if method readVarDef can handle the field
     */
    boolean appliesTo(@Nonnull FieldWrapper field);

    /**
     * @return valid TCases IVarDef or null
     */
    @Nullable
    IVarDef readVarDef(@Nonnull FieldWrapper field);
}
