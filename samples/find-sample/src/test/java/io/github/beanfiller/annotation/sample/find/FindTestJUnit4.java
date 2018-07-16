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

package io.github.beanfiller.annotation.sample.find;

import io.github.beanfiller.annotation.creator.FunctionTestsCreator;
import io.github.beanfiller.annotation.creator.OutputAnnotationContainer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(Parameterized.class)
public class FindTestJUnit4 {
    private final TestInputs inputs;

    public FindTestJUnit4(TestInputs function) {
        this.inputs = function;
    }

    @Parameterized.Parameters(name = "{index}: {0}")
    public static Collection<TestInputs> data() {
        return new FunctionTestsCreator<>(FindTestInput.class)
                // to create this file from scratch, use SystemTestDefWriter
                .baseResource(FindTestInput.class, "Find-Test.xml")
                .tupleGenerator(2)
                .createDefs() // the magic happens here
                .stream()
                // map abstract input to concrete values (here to improve test label)
                .map(findTestInput -> new TestInputs(findTestInput))
                .collect(Collectors.toList());
    }

    @Test
    public void runTcasesTests() throws Exception {
        if (inputs.findFunction.isFailure()) {
            testFailure();
        } else {
            testSuccess();
        }
    }

    private void testFailure() throws Exception {
        try {
            Find.find(inputs.filename, inputs.fileContent, inputs.searchFilename, inputs.pattern);
            fail("expected Exception " + inputs);
        } catch (IllegalArgumentException | IllegalStateException e) {
            // TODO: Assert message
        }
    }

    private void testSuccess() throws Exception {
        String result = Find.find(inputs.filename, inputs.fileContent, inputs.searchFilename, inputs.pattern);
        assertEquals(result, inputs.resultContent);
    }

}
