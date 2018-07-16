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

import org.apache.commons.lang3.StringUtils;
import io.github.beanfiller.annotation.annotations.FunctionDef;
import io.github.beanfiller.annotation.annotations.Var;
import io.github.beanfiller.annotation.annotations.VarSet;
import io.github.beanfiller.annotation.builders.FunctionInputDefBuilder;
import io.github.beanfiller.annotation.internal.reader.DefaultVarDefReader;
import io.github.beanfiller.annotation.internal.reader.FieldWrapper;
import io.github.beanfiller.annotation.internal.reader.MapStringReader;
import org.cornutum.tcases.FunctionInputDef;
import org.cornutum.tcases.IVarDef;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Given a Java Bean classes annotated with Tcases annotations, created a SystemInputDef
 */
public class AnnotatedFunctionDefReader {

    private final VarDefReader[] varDefReaders;

    private AnnotatedFunctionDefReader(@Nonnull VarDefReader... readers) {
        this.varDefReaders = Arrays.copyOf(readers, readers.length);
    }

    @Nonnull
    public static AnnotatedFunctionDefReader withDefaultAnnotations() {
        return new AnnotatedFunctionDefReader(new DefaultVarDefReader());
    }

    /**
     * A function input reader with custom annotation interpreters.
     * For extensibility.
     */
    @Nonnull
    public static AnnotatedFunctionDefReader withReaders(@Nullable VarDefReader... readers) {
        if (readers == null || readers.length == 0) {
            throw new NullPointerException("readers argument must not be null");
        }
        return new AnnotatedFunctionDefReader(readers);
    }

    /**
     * returns the name given with the FunctionDef annotation, else the SimpleClassName.
     */
    @Nonnull
    private static String readFunctionDefName(@Nonnull Class<?> annotatedClass, @Nullable FunctionDef functionAnnotation) {
        String functionName;
        if (functionAnnotation == null || StringUtils.isBlank(functionAnnotation.value())) {
            functionName = annotatedClass.getSimpleName();
        } else {
            functionName = functionAnnotation.value();
        }
        return functionName;
    }

    /**
     * create System Def from given classes as FunctionDefs with given name.
     */
    @Nonnull
    public List<FunctionInputDef> readFunctionDefs(@Nonnull Class<?>... functionDefClass) {
        List<FunctionInputDef> result = new ArrayList<>();
        for (Class<?> annotatedClass : functionDefClass) {
            result.add(readFunctionInputDef(annotatedClass));
        }
        return result;
    }

    /**
     * create FunctionInputDef from given annotated class.
     */
    @Nonnull
    public FunctionInputDef readFunctionInputDef(@Nonnull Class<?> annotatedClass) {
        FunctionDef functionAnnotation = annotatedClass.getAnnotation(FunctionDef.class);
        FunctionInputDefBuilder builder = FunctionInputDefBuilder.function(readFunctionDefName(annotatedClass, functionAnnotation));
        if (functionAnnotation != null) {
            builder.addAnnotations(MapStringReader.parse(functionAnnotation.having()));
        }

        for (Field field : annotatedClass.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers())) {
                if (field.getAnnotation(Var.class) != null
                        || field.getAnnotation(VarSet.class) != null) {
                    throw new IllegalStateException("Annotation not valid on static field");
                }
            } else {
                FieldWrapper fieldWrapper = FieldWrapper.of(field);
                IVarDef varDef = readVarDef(fieldWrapper);
                if (varDef != null) {
                    builder.addVarDef(varDef);
                }
            }
        }
        return builder.build();
    }

    @Nullable
    private IVarDef readVarDef(@Nonnull FieldWrapper field) {
        for (VarDefReader reader : varDefReaders) {
            if (reader.appliesTo(field)) {
                return reader.readVarDef(field);
            }
        }
        throw new IllegalStateException("No suitable reader defined for field " + field);
    }
}
