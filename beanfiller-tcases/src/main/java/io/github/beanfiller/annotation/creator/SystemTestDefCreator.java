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

import io.github.beanfiller.annotation.reader.AnnotatedFunctionDefReader;
import io.github.beanfiller.annotation.writer.SystemTestDefWriter;
import org.cornutum.tcases.FunctionInputDef;
import org.cornutum.tcases.SystemInputDef;
import org.cornutum.tcases.SystemTestDef;
import org.cornutum.tcases.Tcases;
import org.cornutum.tcases.generator.GeneratorOptions;
import org.cornutum.tcases.generator.IGeneratorSet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Given the output of Tcases, creates an instance of a JavaBean
 * with fields filled according to VarBindings.
 */
public class SystemTestDefCreator extends AbstractTestCaseCreator<SystemTestDefCreator> {

    private final String systemName;

    private final List<Class<?>> functionDefinitions = new ArrayList<>();

    public SystemTestDefCreator(@Nullable SystemTestDef baseDef, @Nullable String systemName, @Nullable Class<?>... functionDefinitions) {
        super(baseDef);
        if (systemName == null) {
            this.systemName = baseDef.getName();
        } else {
            this.systemName = systemName;
        }
        this.functionDefinitions.addAll(Arrays.asList(functionDefinitions));
    }

    public SystemTestDefCreator(@Nullable String systemName, @Nullable Class<?>... functionDefinitions) {
        this((SystemTestDef) null, systemName, functionDefinitions);
    }

    public SystemTestDefCreator(@Nullable String baseDefFilePath,
                                @Nullable String systemName,
                                @Nullable Class<?>... functionDefinitions) {
        this(getSystemTestDef(baseDefFilePath), systemName, functionDefinitions);
        if (baseDefFilePath != null) {
            base(new File(baseDefFilePath).toPath());
        }
    }

    @Nonnull
    private static SystemTestDef createSystemTestDef(
            AnnotatedFunctionDefReader reader,
            String systemName,
            @Nullable SystemTestDef baseDef,
            @Nullable IGeneratorSet generatorSet,
            @Nullable GeneratorOptions options,
            Class<?>... functionDefinitions) {
        final SystemInputDef systemInputDef = new SystemInputDef(systemName);
        for (final FunctionInputDef functionDef : reader.readFunctionDefs(functionDefinitions)) {
            systemInputDef.addFunctionInputDef(functionDef);
        }
        return Tcases.getTests(
                systemInputDef,
                generatorSet,
                baseDef,
                options);
    }

    @Nonnull
    public SystemTestDef create() {
        final SystemTestDef systemTestDef = createSystemTestDef(getReader(),
                systemName,
                getBaseDef(),
                getGeneratorSet(),
                getOptions(),
                functionDefinitions.toArray(new Class<?>[0]));
        final Path baseFile = getBaseFile();
        if (baseFile != null) {
            new SystemTestDefWriter().writeSystemDefToFile(systemTestDef, baseFile);
        }
        return systemTestDef;
    }

    @Override
    @Nonnull
    protected SystemTestDefCreator getInstance() {
        return this;
    }
}
