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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static io.github.beanfiller.annotation.internal.reader.ValueUtil.createNullVarValueDef;
import static io.github.beanfiller.annotation.internal.reader.ValueUtil.createValidVarValueDef;
import static io.github.beanfiller.annotation.internal.reader.ValueUtil.createVarValueDef;

/**
 * Define values for an enum field annotated with @Var
 */
class EnumFieldReader {

    private EnumFieldReader() {
    }

    /**
     * Creates VarValue for each enum constant, with additional properties if the Var annotation provides any.
     */
    @Nonnull
    static List<VarValueDef> readVarValueDefsForEnumField(
            final Class<? extends Enum<?>> enumClass,
            @Nullable final Var varAnnotation,
            @Nullable final String... conditions) {
        if (enumClass.getFields().length == 0) {
            throw new IllegalStateException("Enum '" + enumClass
                    + "' has no values.");
        }

        final Set<String> excludes = new HashSet<>();
        if (varAnnotation != null) {
            excludes.addAll(Arrays.asList(varAnnotation.exclude()));
            if (!varAnnotation.generator().isEmpty()) {
                throw new IllegalStateException("Enum vars do not support generator");
            }
        }
        final List<VarValueDef> varValueDefs = new ArrayList<>();
        for (final Field enumField : enumClass.getFields()) {
            final String enumFieldName = enumField.getName();
            excludes.remove(enumFieldName);
            if ((varAnnotation != null) && Arrays.asList(varAnnotation.exclude()).contains(enumFieldName)) {
                continue;
            }
            final VarValueDef value = getVarValueDefForEnum(enumClass, varAnnotation, enumFieldName, conditions);

            varValueDefs.add(value);
        }
        if ((varAnnotation != null) && varAnnotation.nullable()) {
            varValueDefs.add(getVarValueDefForEnum(enumClass, varAnnotation, null, conditions));
        }
        if (!excludes.isEmpty()) {
            throw new IllegalStateException("Unknown excluded values " + excludes);
        }
        return varValueDefs;
    }

    @SuppressWarnings("PMD.AvoidDeeplyNestedIfStmts")
    @Nonnull
    private static VarValueDef getVarValueDefForEnum(
            final Class<? extends Enum<?>> enumClass,
            @Nullable final Var varAnnotation,
            @Nullable final String enumFieldName,
            @Nullable final String... conditions) {
        VarValueDef value = null;
        if ((varAnnotation != null) && (varAnnotation.value().length > 0)) {
            final Set<String> values = new HashSet<>();
            for (final Value varValue : varAnnotation.value()) {
                try {
                    enumClass.getField(varValue.value());
                } catch (NoSuchFieldException e) {
                    if (!varValue.isNull()) {
                        throw new IllegalStateException("@Value value '" + varValue.value()
                                + "' not a known key in Enum " + enumClass.getName() + " and not isNull", e);
                    }
                }

                if (enumFieldName == null) {
                    value = createVarValueDef("NA", varValue, conditions, true);
                } else if (enumFieldName.equals(varValue.value())) {
                    value = createVarValueDef(enumFieldName, varValue, conditions, varValue.isNull());
                    if (!values.add(varValue.value())) {
                        throw new IllegalStateException("@Value value '" + varValue.value() + "' duplicate");
                    }
                }
            }
        }
        if (value == null) {
            if (enumFieldName == null) {
                value = createNullVarValueDef(conditions);
            } else {
                value = createValidVarValueDef(enumFieldName, conditions);
            }
        }
        return value;
    }

}
