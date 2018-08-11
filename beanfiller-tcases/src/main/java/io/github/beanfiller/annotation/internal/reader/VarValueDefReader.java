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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static io.github.beanfiller.annotation.internal.reader.BooleanFieldReader.readVarValueDefsForBoolean;
import static io.github.beanfiller.annotation.internal.reader.EnumFieldReader.readVarValueDefsForEnumField;
import static io.github.beanfiller.annotation.internal.reader.ValueUtil.createNullVarValueDef;
import static io.github.beanfiller.annotation.internal.reader.ValueUtil.createVarValueDef;

/**
 * Given a Java Bean classes annotated with Tcases annotations, created an IVarDef
 */
class VarValueDefReader {

    private VarValueDefReader() {
    }

    /**
     * Creates values for a var field depending on the type and annotations.
     */
    @SuppressWarnings("unchecked")
    @Nonnull
    static List<VarValueDef> readVarValueDefs(final FieldWrapper field,
                                              @Nullable final String... conditions) {
        final List<VarValueDef> varValueDefs;
        final Var varAnnotation = field.getAnnotation(Var.class);
        try {
            if ((field.getType() == Boolean.class) || (field.getType() == boolean.class)) {
                varValueDefs = readVarValueDefsForBoolean(varAnnotation, (field.getType() == Boolean.class) && ((varAnnotation == null) || varAnnotation.nullable()), conditions);
            } else if (field.getType().isEnum()) {
                varValueDefs = readVarValueDefsForEnumField((Class<? extends Enum<?>>) field.getType(), varAnnotation, conditions);
            } else {
                if (field.getType().isPrimitive()) {
                    varValueDefs = getVarValuesNumbersStringPrimitive(varAnnotation, conditions, false);
                } else {
                    varValueDefs = getVarValuesNumbersStringPrimitive(varAnnotation, conditions, ((varAnnotation == null) || varAnnotation.nullable()));
                }
            }
        } catch (IllegalStateException e) {
            // provide more context
            throw new IllegalStateException("Failed to read definitions on field " + field.getName(), e);
        }
        return varValueDefs;
    }

    @Nonnull
    private static List<VarValueDef> getVarValuesNumbersStringPrimitive(
            @Nullable final Var varAnnotation,
            @Nullable final String[] conditions,
            final boolean includeNull) {
        if ((varAnnotation != null) && (varAnnotation.exclude().length > 0)) {
            // TODO: When allowing generators, allow exclusions
            throw new IllegalStateException("Only Boolean and Enum type Vars can exclude values");
        }
        if ((varAnnotation == null) || (varAnnotation.value().length <= 0)) {
            throw new IllegalStateException("Fields must be enum, boolean or define values using @Var(value=...)");
        }

        final Set<Object> collectedValues = new HashSet<>();
        final List<VarValueDef> result = new ArrayList<>();
        for (final VarValueDef varValueDef : getVarValueDefsFromAnnotation(varAnnotation, conditions, includeNull)) {
            result.add(varValueDef);
            if (!collectedValues.add(varValueDef.getName())) {
                throw new IllegalStateException("@Value value '" + varValueDef.getName() + "' duplicate");
            }
        }


        return result;
    }


    private static List<VarValueDef> getVarValueDefsFromAnnotation(
            final Var varAnnotation,
            @Nullable final String[] conditions,
            final boolean includeNull) {
        final List<VarValueDef> varValueDefs = new ArrayList<>();
        boolean foundNull = false;
        for (final Value varValue : varAnnotation.value()) {
            if (varValue.isNull()) {
                if (foundNull) {
                    throw new IllegalStateException("Duplicate definition with isNull");
                }
                foundNull = true;
            } else {
                varValueDefs.add(createVarValueDef(varValue.value(), varValue, conditions, varValue.isNull()));
            }
        }
        if (includeNull && !foundNull) {
            varValueDefs.add(createNullVarValueDef(conditions));
        }
        return varValueDefs;
    }
}
