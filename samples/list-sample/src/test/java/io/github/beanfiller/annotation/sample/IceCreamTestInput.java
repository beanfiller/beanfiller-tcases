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

import io.github.beanfiller.annotation.annotations.Var;
import io.github.beanfiller.annotation.creator.AbstractTestInput;
import io.github.beanfiller.annotation.creator.FunctionTestsCreator;
import io.github.beanfiller.annotation.creator.ReflectionBasedInstanceCreator;
import org.cornutum.tcases.VarValueDef;

import java.util.Arrays;
import java.util.List;

import static io.github.beanfiller.annotation.sample.ListEquivalenceClasses.getVarValueDefs;

public class IceCreamTestInput extends AbstractTestInput {

    private enum Extra {
        CREAM,
        CHOCOLATE_SPRINKLES,
        CHOCOLATE_SAUCE,
        KETCHUP
    }

    private static List<VarValueDef> extras() {
        return getVarValueDefs(
                Arrays.asList(Extra.CHOCOLATE_SPRINKLES, Extra.CHOCOLATE_SAUCE, Extra.CREAM),
                Arrays.asList(Extra.KETCHUP),
                0,
                2,
                Extra::valueOf);
    }

    // TODO:
    // - Allow Collection
    // - Unwrap wrapped in instance Creator
    // - genrator method to annotation
    // - globale condition for other variables

    @Var(generator = "extras", nullable = false)
    public SimpleTestInput<Extra>[] extras;

    private enum Flavour {
        CHOCOLATE,
        VANILLA,
        STRAWBERRY,
        WALNUT,
        MINT,
        PINEAPPLE,
        LEMON,
        CHERRY,
        BEEF,
        CAMENBERT
    }

    private static List<VarValueDef> flavours() {
        return getVarValueDefs(
                Arrays.asList(Flavour.CHOCOLATE, Flavour.PINEAPPLE, Flavour.LEMON, Flavour.CHERRY,
                        Flavour.VANILLA, Flavour.STRAWBERRY, Flavour.WALNUT, Flavour.MINT),
                Arrays.asList(Flavour.BEEF, Flavour.CAMENBERT),
                1,
                4,
                Flavour::valueOf);
    }

    @Var(generator = "flavours", nullable = false)
    public SimpleTestInput<Flavour>[] flavours;


    public static void main(String[] args) {
        int tupleSize = 2;
        List<IceCreamTestInput> testCases = new FunctionTestsCreator<>(IceCreamTestInput.class)
                // Special mapper to extract JSon from String
                .instanceCreator(ReflectionBasedInstanceCreator.withMappers(new ArrayMapper()))
                .tupleGenerator(tupleSize)
                .createDefs();
        testCases.forEach(test -> System.out.println(test.toString()));
    }

    @Override
    public String toString() {
        return (isFailure() ? "FAIL: " : "SUCCEED: ")
                + "flavours=" + Arrays.toString(flavours)
                + ", extras=" + Arrays.toString(extras);
    }
}
