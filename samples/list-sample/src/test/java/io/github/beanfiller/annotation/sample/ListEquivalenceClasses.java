package io.github.beanfiller.annotation.sample;

import io.github.beanfiller.annotation.builders.VarDefBuilder;
import io.github.beanfiller.annotation.builders.VarValueDefBuilder;
import io.github.beanfiller.annotation.creator.FunctionTestsCreator;
import io.github.beanfiller.annotation.creator.ReflectionBasedInstanceCreator;
import io.github.beanfiller.annotation.creator.TestMetadataAware;
import org.cornutum.tcases.FunctionInputDef;
import org.cornutum.tcases.VarValueDef;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static io.github.beanfiller.annotation.builders.FunctionInputDefBuilder.function;

public class ListEquivalenceClasses {
    /**
     * @return a list of arrays to test
     */
    public static <T extends TestMetadataAware> List<ListTestInput<T>> combine(
            final List<T> testcases,
            final int min,
            final int max,
            final boolean includeNull) {
        final List<ListTestInput<T>> result = new ArrayList<>();
        if (includeNull) {
            result.add(null);
        }
        final int actualMin = Math.max(0, min);

        if (actualMin > max) {
            throw new IllegalStateException("Min > max " + min + " > " + max);
        }

        final CircleIterator<T> validsIterator = new CircleIterator<>(testcases.stream()
                .filter(testcase -> !testcase.isFailure()).collect(Collectors.toList()));

        // create 1 minimal valid instance (boundary)
        final ListTestInput<T> minResult = new ListTestInput<>();
        for (int i = 0; i < actualMin; i++) {
            minResult.add(validsIterator.next());
        }
        result.add(minResult);

        if (actualMin > 0) {
            // create one minimal invalid sample
            final ListTestInput<T> tooSmallResult = new ListTestInput<>();
            tooSmallResult.setTestMetadata(0, true, null);
            for (int i = 0; i < (actualMin - 1); i++) {
                tooSmallResult.add(validsIterator.next());
            }
            result.add(tooSmallResult);
        }

        // create 1 or more maximal instances
        // keep consuming valids until exhausted, then start from beginning
        while (!validsIterator.isExhausted()) {
            final ListTestInput<T> maxResult = new ListTestInput<>();
            for (int i = 0; i < max; i++) {
                if (validsIterator.isExhausted() && (i >= min)) {
                    break;
                }
                maxResult.add(validsIterator.next());
            }
            result.add(maxResult);
        }

        // create a max + 1 instance
        final ListTestInput<T> tooBigResult = new ListTestInput<>();
        for (int i = 0; i < (max + 1); i++) {
            tooBigResult.add(validsIterator.next());
        }
        tooBigResult.setTestMetadata(0, true, null);
        result.add(tooBigResult);

        // create invalid instances
        final Iterator<T> invalidsIterator = testcases.stream()
                .filter(TestMetadataAware::isFailure).collect(Collectors.toList()).iterator();

        while (invalidsIterator.hasNext()) {
            final ListTestInput<T> invalidResult = new ListTestInput<>();
            invalidResult.setTestMetadata(0, true, null);
            invalidResult.add(invalidsIterator.next()); // the rotten apple
            for (int i = 1; i < min; i++) {
                invalidResult.add(validsIterator.next()); // good filler values
            }
            result.add(invalidResult);
        }

        return result;
    }

    @SuppressWarnings("rawtypes")
    public static <T extends Enum<?>> List<VarValueDef> getVarValueDefs(
            List<T> valid,
            List<T> invalid,
            int min, int max,
            final Function<String, T> stringToValueMapper) {
        final VarDefBuilder varDefBuilder = VarDefBuilder.varDef("wrapped");
        for (final T extra: valid) {
            varDefBuilder.addValue(new VarValueDefBuilder(extra.name()).build());
        }
        for (final T extra: invalid) {
            varDefBuilder.addValue(new VarValueDefBuilder(extra.name(), VarValueDef.Type.FAILURE).build());
        }

        final FunctionInputDef funInputDef = function("allExtras").addVarDef(varDefBuilder.build()).build();
        /* generate testcases */

        final List<SimpleTestInput> list = new FunctionTestsCreator<>(funInputDef, SimpleTestInput.class)
                .instanceCreator(ReflectionBasedInstanceCreator.withMappers(new WrappedTestInputMapper<>(stringToValueMapper)))
                .createDefs();

        return combine(list, min, max, false)
                .stream()
                .map((ListTestInput<SimpleTestInput> i) -> new VarValueDef(
                        ArrayMapper.encodeToString(i),
                        i.isFailure() ? VarValueDef.Type.FAILURE : VarValueDef.Type.VALID))
                .collect(Collectors.toList());
    }
}
