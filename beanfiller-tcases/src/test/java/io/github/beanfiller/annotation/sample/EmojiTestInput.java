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

import io.github.beanfiller.annotation.annotations.Value;
import io.github.beanfiller.annotation.annotations.Var;
import io.github.beanfiller.annotation.creator.FunctionTestsCreator;

import java.util.List;

public class EmojiTestInput {

    @Var(value = {@Value("^"), @Value("°"), @Value("ಠ"), @Value("≖"), @Value("•"), @Value("ˇ"),
            @Value("˘"), @Value("ᴗ"), @Value("\""), @Value("<"), @Value("╥")})
    char eye;

    @Var(value = {@Value("-"), @Value("_"), @Value("‿"), @Value("∇"), @Value("◡"), @Value("³"),
            @Value("ᴗ"), @Value("﹏"), @Value(".")})
    char snoot;

    @Var(value = {@Value("\\/"), @Value("ᕙᕗ"), @Value("ᕦᕤ"), @Value("┌ʃ")})
    String arms;

    private String getFace() {
        return  (arms == null ? "" : arms.substring(0, 1))
                + '(' + eye + snoot + eye + ')'
                + (arms == null ? "" : arms.substring(1, 2));
    }

    public static void main(String[] args) {
        int tupleSize = 1;
        List<EmojiTestInput> testCases = new FunctionTestsCreator<>(EmojiTestInput.class)
                .tupleGenerator(tupleSize)
                .createDefs();
        testCases.forEach(test -> System.out.println(test.getFace()));

        System.out.println("\n" + testCases.size() + " faces generated with independent " + tupleSize + "-tuples or properties");
    }
}
