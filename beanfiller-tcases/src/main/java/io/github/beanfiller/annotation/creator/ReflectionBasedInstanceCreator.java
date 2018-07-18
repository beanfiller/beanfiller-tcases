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

import org.apache.commons.collections4.IteratorUtils;
import org.cornutum.tcases.TestCase;
import org.cornutum.tcases.VarBinding;

import javax.annotation.Nonnull;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static io.github.beanfiller.annotation.reader.VarDefReader.INITIALIZE_TESTCASE_VARNAME;

public class ReflectionBasedInstanceCreator implements InstanceCreator {

    /**
     * registry for finding a VariableToClassValueMapper for a given target class
     */
    private final List<VariableToClassValueMapper> stringToValueList = new LinkedList<>();

    @Nonnull
    public static ReflectionBasedInstanceCreator withDefaultMapper() {
        final ReflectionBasedInstanceCreator creator = new ReflectionBasedInstanceCreator();
        creator.stringToValueList.add(new DefaultVariableToClassValueMapper());
        return creator;
    }

    @Nonnull
    public static ReflectionBasedInstanceCreator withMappers(VariableToClassValueMapper... mappers) {
        final ReflectionBasedInstanceCreator creator = new ReflectionBasedInstanceCreator();
        creator.stringToValueList.addAll(Arrays.asList(mappers));
        return creator;
    }

    /**
     *
     */
    @Override
    @Nonnull
    public <T> T createDef(TestCase testCase,
                           Class<T> typeClass,
                           OutputAnnotationContainer outputAnnotations) {
        final T instance;
        try {
            final Constructor<T> declaredConstructor = typeClass.getDeclaredConstructor();
            declaredConstructor.setAccessible(true);
            instance = declaredConstructor.newInstance();
            outputAnnotations.addTestCaseAnnotations(testCase);
            fillValues(0,
                    instance,
                    IteratorUtils.toList(testCase.getVarBindings()),
                    outputAnnotations);
            fillSpecialValues(instance, testCase, outputAnnotations);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Error creating type " + typeClass, e);
        }
        return instance;
    }

    /**
     * Fill other fields with meta-information, if given annotation is present.
     */
    private void fillSpecialValues(
            Object instance,
            TestCase testCase,
            OutputAnnotationContainer outputAnnotations) {
        if (TestMetadataAware.class.isAssignableFrom(instance.getClass())) {
            final TestMetadataAware testMetadataAware = (TestMetadataAware) instance;
            testMetadataAware.setTestMetadata(
                    testCase.getId(),
                    testCase.getType() == TestCase.Type.FAILURE,
                    outputAnnotations);
        }
    }

    /**
     * recursively create and fill instance from varbinding values
     *
     * @param prefixLength      the initial varbinding key part to discard because of nesting depth
     * @param outputAnnotations Container to collect Vardef output annotations
     */
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    private <C> void fillValues(int prefixLength, final C instance,
                                Collection<VarBinding> varBindings,
                                OutputAnnotationContainer outputAnnotations) {
        // each Varbinding is either for a single data field of this instance, or a nested field
        final Map<String, List<VarBinding>> nestedFieldBindings = new HashMap<>();
        for (final VarBinding varBinding : varBindings) {
            final String name = varBinding.getVar().substring(prefixLength);
            if (INITIALIZE_TESTCASE_VARNAME.equals(name)) {
                continue;
            }
            outputAnnotations.addVarBindingAnnotations(name, varBinding);
            final int firstDotPos = name.indexOf('.');
            if (firstDotPos >= 0) {
                final String mapKey = name.substring(0, firstDotPos);
                final List<VarBinding> bindingList = nestedFieldBindings.computeIfAbsent(mapKey, k -> new ArrayList<>());
                bindingList.add(varBinding);
            } else {
                final VariableToClassValueMapper mapper = getMapper(name, instance.getClass());
                if (!varBinding.isValueNA()) {
                    mapper.setFieldValueAs(name, instance, varBinding);
                }
            }
        }
        nestedFieldBindings.forEach((key, value) -> {
            for (final VarBinding binding : value) {
                // if parent is null, no need to fill children
                if (binding.getVar().substring(prefixLength + key.length() + 1).equals(INITIALIZE_TESTCASE_VARNAME)
                        && (binding.isValueNA() || binding.getValue().equals("false"))) {
                    return;
                }
            }
            try {
                final Field f = instance.getClass().getDeclaredField(key);
                f.setAccessible(true);
                Object fieldInstance = f.get(instance);
                if (fieldInstance == null) {
                    final Constructor<?> declaredConstructor = f.getType().getDeclaredConstructor();
                    declaredConstructor.setAccessible(true);
                    fieldInstance = declaredConstructor.newInstance();
                    f.set(instance, fieldInstance);
                }
                fillValues(prefixLength + key.length() + 1, fieldInstance, value, outputAnnotations);
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException("Error setting field " + key, e);
            }
        });
    }

    @Nonnull
    private VariableToClassValueMapper getMapper(String varname, Class<?> parentClass) {
        for (final VariableToClassValueMapper mapper : stringToValueList) {
            if (mapper.appliesTo(varname, parentClass)) {
                return mapper;
            }
        }
        throw new IllegalStateException("No mapper for variable " + varname);
    }


}
