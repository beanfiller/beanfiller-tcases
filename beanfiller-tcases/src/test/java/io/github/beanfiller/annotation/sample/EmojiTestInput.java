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

package io.github.beanfiller.annotation.sample;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.github.beanfiller.annotation.annotations.Value;
import io.github.beanfiller.annotation.annotations.Var;
import io.github.beanfiller.annotation.creator.FunctionTestsCreator;
import org.cornutum.tcases.VarValueDef;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressFBWarnings("UPM_UNCALLED_PRIVATE_METHOD")
@Combiners{tuples = 1,
        value = @Combiner("EYEMOUTH", tuples = 2)}
public class EmojiTestInput {

    @SuppressFBWarnings("RE_POSSIBLE_UNINTENDED_PATTERN")
    private static List<VarValueDef> eyeValues() {
        return Arrays.stream("^°ಠ≖•ˇ˘ᴗ\"<╥".split("|"))
                .map(c -> new VarValueDef(c, VarValueDef.Type.VALID))
                .collect(Collectors.toList());
    }

    @Var(generator = "eyeValues")

    @Combination("EYEMOUTH")
    char eye;

    @Var(value = {@Value("-"), @Value("_"), @Value("‿"), @Value("∇"), @Value("◡"), @Value("³"),
            @Value("ᴗ"), @Value("﹏"), @Value(".")})
    @Combination("EYEMOUTH")
    char snoot;

    @Var(value = {@Value("\\/"), @Value("ᕙᕗ"), @Value("ᕦᕤ"), @Value("┌ʃ")})
    String arms;

    private String getFace() {
        return  (arms == null ? "" : arms.substring(0, 1))
                + '(' + eye + snoot + eye + ')'
                + (arms == null ? "" : arms.substring(1, 2));
    }

    @Test
    public void testTupleSize() {
        final int tupleSize = 2;
        final List<EmojiTestInput> testCases = new FunctionTestsCreator<>(EmojiTestInput.class)
                .tupleGenerator(tupleSize)
                .createDefs();
        assertThat(testCases.size()).isGreaterThan(100);
        testCases.forEach(test -> System.out.println(test.getFace()));

        System.out.println("\n" + testCases.size() + " faces generated with independent " + tupleSize + "-tuples or properties");
    }
}
