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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collection;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class FindTestJUnit5 {


    public static Collection<TestInputs> data() {
        return new FunctionTestsCreator<>(FindTestInput.class)
                .tupleGenerator(2)
                .createDefs() // the magic happens here
                .stream()
                // map abstract input to concrete values (here to improve test label)
                .map(findTestCase -> new TestInputs(findTestCase))
                .collect(Collectors.toList());
    }

    @ParameterizedTest
    @MethodSource("data")
    public void runTcasesTests(TestInputs inputs) throws Exception {
        if (inputs.findFunction.isFailure()) {
            testFailure(inputs);
        } else {
            testSuccess(inputs);
        }
    }

    private void testFailure(TestInputs inputs) throws Exception {
        try {
            Find.find(inputs.filename, inputs.fileContent, inputs.searchFilename, inputs.pattern);
            fail("expected Exception " + inputs);
        } catch (IllegalArgumentException | IllegalStateException e) {
            // TODO: Assert message
        }
    }

    private void testSuccess(TestInputs inputs) throws Exception {
        String result = Find.find(inputs.filename, inputs.fileContent, inputs.searchFilename, inputs.pattern);
        assertEquals(result, inputs.resultContent);
    }

}
