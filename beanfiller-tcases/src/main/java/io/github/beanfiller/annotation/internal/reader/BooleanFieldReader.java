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

package io.github.beanfiller.annotation.internal.reader;

import io.github.beanfiller.annotation.annotations.Value;
import io.github.beanfiller.annotation.annotations.Var;
import org.cornutum.tcases.VarValueDef;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static io.github.beanfiller.annotation.internal.reader.ValueUtil.createNullVarValueDef;
import static io.github.beanfiller.annotation.internal.reader.ValueUtil.createValidVarValueDef;
import static io.github.beanfiller.annotation.internal.reader.ValueUtil.createVarValueDef;

/**
 * Define boolean values for a Value annotation on a boolean field
 */
class BooleanFieldReader {

    private BooleanFieldReader() {
    }

    /**
     * Creates a true and a false VarValue, with additional properties if the Var annotation provides any.
     */
    @Nonnull
    static List<VarValueDef> readVarValueDefsForBoolean(
            @Nullable final Var varAnnotation,
            final boolean includeNull,
            @Nullable final String... conditions) {
        final Set<String> excludes = new HashSet<>();
        if (varAnnotation != null) {
            excludes.addAll(Arrays.asList(varAnnotation.exclude()));
            if (!varAnnotation.generator().isEmpty()) {
                throw new IllegalStateException("Boolean vars do not support generator");
            }
        }

        final List<VarValueDef> varValues = new ArrayList<>();
        final List<String> valueStrings;
        if (includeNull) {
            valueStrings = Arrays.asList("true", "false", "null");
        } else {
            valueStrings = Arrays.asList("true", "false");
        }
        for (final String boolname : valueStrings) {
            excludes.remove(boolname);
            final VarValueDef value = getVarValueDefBoolean(varAnnotation, boolname, conditions);
            if (value == null) {
                continue;
            }

            varValues.add(value);

        }
        if (!excludes.isEmpty()) {
            throw new IllegalStateException("Unknown excluded values " + excludes);
        }
        return varValues;
    }

    /**
     * create true, false or NA value with properties from annotations
     */
    @CheckForNull
    private static VarValueDef getVarValueDefBoolean(
            @Nullable final Var varAnnotation,
            final String boolname,
            @Nullable final String... conditions) {
        if ((varAnnotation != null) && Arrays.asList(varAnnotation.exclude()).contains(boolname)) {
            return null;
        }
        VarValueDef value = null;
        if ((varAnnotation != null) && (varAnnotation.value().length > 0)) {
            final Set<String> values = new HashSet<>();
            for (final Value varValue : varAnnotation.value()) {
                final String actualValueString = varValue.value();
                if (!"true".equalsIgnoreCase(actualValueString)
                        && !"null".equalsIgnoreCase(actualValueString)
                        && !"false".equalsIgnoreCase(actualValueString)) {
                    throw new IllegalStateException("@Value value '" + actualValueString
                            + "' not a valid Boolean value");
                }
                if (boolname.equalsIgnoreCase(actualValueString)) {
                    value = createVarValueDef(boolname, varValue, conditions, varValue.isNull());
                    if (!values.add(actualValueString)) {
                        throw new IllegalStateException("@Value value '" + actualValueString + "' duplicate");
                    }
                }
            }
        }
        if (value == null) {
            if ("null".equals(boolname)) {
                value = createNullVarValueDef(conditions);
            } else {
                value = createValidVarValueDef(boolname, conditions);
            }
        }
        return value;
    }


}
