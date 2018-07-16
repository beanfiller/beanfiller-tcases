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
import io.github.beanfiller.annotation.annotations.SystemDef;
import io.github.beanfiller.annotation.builders.SystemInputDefBuilder;
import io.github.beanfiller.annotation.internal.reader.MapStringReader;
import org.cornutum.tcases.SystemInputDef;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Read a whole TCases System input definition (set of independent function input definitions)
 */
public class SystemDefReader {

    private final AnnotatedFunctionDefReader functionDefReader;

    public SystemDefReader() {
        this.functionDefReader = AnnotatedFunctionDefReader.withDefaultAnnotations();
    }

    /**
     * A system reader with custom annotation interpreters.
     * For extensibility.
     */
    public SystemDefReader(@Nullable VarDefReader... readers) {
        if (readers == null) {
            throw new NullPointerException("readers must not be null");
        }
        this.functionDefReader = AnnotatedFunctionDefReader.withReaders(readers);
    }

    /**
     * @return A System input definition containing testcases for each given function input definition
     */
    @Nonnull
    public SystemInputDef readSystemDefFromFunctionDefs(@Nullable String systemName,
                                                        @Nullable Class<?>... functionDefClass) {
        SystemInputDefBuilder inputDef = SystemInputDefBuilder.system(systemName);
        addFunctionDefs(inputDef, functionDefReader, functionDefClass);
        return inputDef.build();
    }


    /**
     * @return A System input definition containing testcases for each given function input definition
     */
    @Nonnull
    public SystemInputDef readSystemDef(Class<?> systemDefClass) {
        if (systemDefClass == null) {
            throw new NullPointerException("Cannot define system based on null class");
        }
        SystemDef systemAnnotation = systemDefClass.getAnnotation(SystemDef.class);
        SystemInputDefBuilder builder = SystemInputDefBuilder.system(readSystemDefName(systemDefClass, systemAnnotation));
        if (systemAnnotation != null && systemAnnotation.value().length > 0) {
            addFunctionDefs(builder, functionDefReader, systemAnnotation.value());
        } else {
            addFunctionDefs(builder, functionDefReader, systemDefClass);
        }
        if (systemAnnotation != null) {
            builder.addAnnotations(MapStringReader.parse(systemAnnotation.having()));
        }

        return builder.build();
    }

    private static void addFunctionDefs(@Nonnull SystemInputDefBuilder builder, @Nonnull AnnotatedFunctionDefReader functionDefReader, @Nonnull Class<?>... functionDefClass) {
        for (Class<?> annotatedClass : functionDefClass) {
            builder.addInputDef(functionDefReader.readFunctionInputDef(annotatedClass));
        }
    }

    /**
     * returns the name given with the FunctionDef annotation, else the SimpleClassName.
     */
    @Nonnull
    private static String readSystemDefName(@Nonnull Class<?> annotatedClass, @Nullable SystemDef systemDefAnnotation) {
        String functionName;
        if (systemDefAnnotation == null || StringUtils.isBlank(systemDefAnnotation.name())) {
            functionName = annotatedClass.getSimpleName();
        } else {
            functionName = systemDefAnnotation.name();
        }
        return functionName;
    }
}
