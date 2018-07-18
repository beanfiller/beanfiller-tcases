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

import org.cornutum.tcases.FunctionInputDef;
import org.cornutum.tcases.FunctionTestDef;
import org.cornutum.tcases.Tcases;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * Given the output of Tcases, creates an instance of a JavaBean
 * with fields filled according to VarBindings.
 */
public class FunctionTestsCreator<T> extends AbstractTestCaseCreator<FunctionTestsCreator<T>> {

    private final Class<T> functionDefClass;
    private final FunctionInputDef inputDef;

    public FunctionTestsCreator(Class<T> functionDefClass) {
        super();
        this.functionDefClass = functionDefClass;
        inputDef = getReader().readFunctionInputDef(functionDefClass);
    }

    public FunctionTestsCreator(FunctionInputDef inputDef, Class<T> functionDefClass) {
        super();
        this.functionDefClass = functionDefClass;
        this.inputDef = inputDef;
    }

    @Nonnull
    public List<T> createDefs() {
        final FunctionTestDef funTestDef = Tcases.getTests(inputDef,
                getGeneratorSet(),
                getBaseDef(),
                getOptions());
        final List<T> result = new ArrayList<>();
        funTestDef.getTestCases().forEachRemaining(testCase -> {
            result.add(getInstanceCreator().createDef(testCase, functionDefClass, new OutputAnnotationContainer()));
        });
        return result;
    }

    @Override
    @Nonnull
    protected FunctionTestsCreator<T> getInstance() {
        return this;
    }

}
