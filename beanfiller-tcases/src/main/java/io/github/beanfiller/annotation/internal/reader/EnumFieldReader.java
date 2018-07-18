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
import io.github.beanfiller.annotation.annotations.VarValueTemplate;
import io.github.beanfiller.annotation.builders.VarValueDefBuilder;
import org.cornutum.tcases.TestCase;
import org.cornutum.tcases.VarValueDef;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static io.github.beanfiller.annotation.internal.reader.ConditionReader.getCondition;
import static io.github.beanfiller.annotation.internal.reader.ValueUtil.createNullVarValueDef;
import static io.github.beanfiller.annotation.internal.reader.ValueUtil.createValidVarValueDef;
import static io.github.beanfiller.annotation.internal.reader.ValueUtil.createVarValueDef;
import static io.github.beanfiller.annotation.internal.reader.ValueUtil.typeOf;

/**
 * Define values for an enum field annotated with @Var
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
class EnumFieldReader {

    private EnumFieldReader() {
    }

    /**
     * Creates VarValue for each enum constant, with additional properties if the Var annotation provides any.
     */
    @Nonnull
    static List<VarValueDef> readVarValueDefsForEnumFields(
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
            validateVarAnnotation(enumClass, varAnnotation);
        }
        final List<VarValueDef> varValueDefs = new ArrayList<>();
        for (final Field enumField : enumClass.getFields()) {
            final String enumFieldName = enumField.getName();
            excludes.remove(enumFieldName);
            if ((varAnnotation != null) && Arrays.asList(varAnnotation.exclude()).contains(enumFieldName)) {
                continue;
            }
            final VarValueDef value = getVarValueDefForEnumField(varAnnotation, enumField, conditions);

            varValueDefs.add(value);
        }
        if ((varAnnotation != null) && varAnnotation.nullable()) {
            varValueDefs.add(getNullVarValueDefForEnum(varAnnotation, conditions));
        }
        if (!excludes.isEmpty()) {
            throw new IllegalStateException("Unknown excluded values " + excludes);
        }
        return varValueDefs;
    }

    /**
     * @throws IllegalStateException on Validation issues
     */
    private static void validateVarAnnotation(Class<? extends Enum<?>> enumClass, Var varAnnotation) {
        // validate varValue annotations based on enum (must be an enum field, must not be duplicate)
        if (!varAnnotation.generator().isEmpty()) {
            throw new IllegalStateException("Enum vars do not support generator");
        }
        final Set<String> values = new HashSet<>();
        boolean nullValueFound = false;
        for (final Value varValue : varAnnotation.value()) {
            try {
                enumClass.getField(varValue.value());
                if (varValue.isNull()) {
                    throw new IllegalStateException("@Value value '" + varValue.value()
                            + "' is a known key in Enum " + enumClass.getName() + " but also isNull");
                }
            } catch (NoSuchFieldException e) {
                if (!varValue.isNull()) {
                    throw new IllegalStateException("@Value value '" + varValue.value()
                            + "' not a known key in Enum " + enumClass.getName() + " and not isNull", e);
                }
                if (nullValueFound) {
                    throw new IllegalStateException("@Value value '" + varValue.value()
                            + "' defines second null value for enum " + enumClass.getName(), e);
                }
                nullValueFound = true;
            }
            if (!values.add(varValue.value())) {
                throw new IllegalStateException("@Value value '" + varValue.value() + "' duplicate");
            }
        }
    }

    /**
     * Create a VarValue Definition based on 2 input sources, the Var annotation, and the enum definition.
     * Example:
     *
     * class Foo {
     *     @Var(value = {
     *                 @Value(value = "A",..),
     *         }
     *     FooEnum x;
     *
     *     private static enum FooEnum {
     *         @VarValueTemplate(...)
     *         A,
     *         B,
     *         C
     *     }
     * }
     */
    @SuppressWarnings("PMD.AvoidDeeplyNestedIfStmts")
    @Nonnull
    private static VarValueDef getVarValueDefForEnumField(
            @Nullable final Var varAnnotation,
            final Field enumField,
            @Nullable final String... conditions) {
        VarValueDef value = null;
        final String enumFieldName = enumField.getName();
        // First try to create a value based on a field annotation with same name as enum field
        if ((varAnnotation != null) && (varAnnotation.value().length > 0)) {
            for (final Value varValue : varAnnotation.value()) {
                if (enumFieldName.equals(varValue.value())) {
                    value = createTemplatedVarValueDef(enumField, varValue, conditions);
                    // assume only one varValue has same name, based on prior validation
                    break;
                }
            }
        }
        // if no variable field annotation matched given enum field create based on enum field only
        if (value == null) {
            value = createTemplatedVarValueDef(enumField, null, conditions);
        }
        return value;
    }

    @Nonnull
    @SuppressWarnings("PMD.UseVarargs")
    private static VarValueDef createTemplatedVarValueDef(Field enumField, @Nullable Value varValue, @Nullable String[] conditions) {
        final @Nullable VarValueTemplate templateAnnotation = enumField.getAnnotation(VarValueTemplate.class);
        final String name = enumField.getName();
        if (templateAnnotation == null) {
            if (varValue == null) {
                return createValidVarValueDef(name, conditions);
            } else {
                return createVarValueDef(name, varValue, conditions, false);
            }
        }
        return createTemplatedVarValueDef(templateAnnotation, name, varValue, conditions);
    }

    @SuppressWarnings("PMD.UseVarargs")
    private static VarValueDef createTemplatedVarValueDef(VarValueTemplate templateAnnotation, String name, @Nullable Value varValue, @Nullable String[] conditions) {
        // take defaults from enum annotation, override with VarValue settings
        final VarValueDef.Type templateType;
        if (templateAnnotation.type() == TestCase.Type.FAILURE) {
            templateType = VarValueDef.Type.FAILURE;
        } else if (templateAnnotation.once()) {
            templateType = VarValueDef.Type.ONCE;
        } else {
            templateType = VarValueDef.Type.VALID;
        }
        final VarValueDefBuilder builder = new VarValueDefBuilder(name, varValue == null ? templateType : typeOf(varValue, templateType));
        builder.addAnnotations(MapStringReader.parse(templateAnnotation.having()));
        builder.addProperties(templateAnnotation.properties());
        String[] when = null;
        String[] whenNot = null;
        if (varValue != null) {
            when = varValue.when();
            whenNot = varValue.whenNot();
            builder.addAnnotations(MapStringReader.parse(varValue.having()));
            builder.addProperties(varValue.properties());
        }

        if (when == null || when.length == 0) {
            when = templateAnnotation.when();
        }
        if (whenNot == null || whenNot.length == 0) {
            whenNot = templateAnnotation.whenNot();
        }
        builder.condition(getCondition(conditions, when, whenNot));
        return builder.build();
    }

    @Nonnull
    private static VarValueDef getNullVarValueDefForEnum(
            @Nullable final Var varAnnotation,
            @Nullable final String... conditions) {
        VarValueDef value = null;
        // First try to create a value based on a field annotation with same name as enum field
        if ((varAnnotation != null) && (varAnnotation.value().length > 0)) {
            for (final Value varValue : varAnnotation.value()) {
                // assume only one varValue is Null, based on prior validation
                if (varValue.isNull()) {
                    value = createVarValueDef("NA", varValue, conditions, true);
                    break;
                }
            }
        }
        // if no variable field annotation matched given enum field create based on enum field only
        if (value == null) {
            value = createNullVarValueDef(conditions);
        }
        return value;
    }

}
