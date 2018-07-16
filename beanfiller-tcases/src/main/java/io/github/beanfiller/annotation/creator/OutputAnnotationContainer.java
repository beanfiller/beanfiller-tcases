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

import org.apache.commons.lang3.StringUtils;
import org.cornutum.tcases.Annotated;
import org.cornutum.tcases.VarBinding;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * To contain the collected output annotations from a systemTestDef.
 */
public class OutputAnnotationContainer {
    /**
     * testCase annotations include default Values from the system and function level, unless they have been overridden
     */
    private final Map<String, String> testCaseAnnotations = new HashMap<>();

    /**
     * variable binding annotations with the variable path
     */
    private final Map<String, String> varBindingAnnotations = new HashMap<>();

    private static void addAll(@Nonnull Map<String, String> map, @Nonnull Annotated annotated) {
        annotated.getAnnotations().forEachRemaining(key -> map.put(key, annotated.getAnnotation(key)));
    }

    public void addTestCaseAnnotations(Annotated systemTestDef) {
        addAll(testCaseAnnotations, systemTestDef);
    }

    public void addVarBindingAnnotations(String path, VarBinding annotated) {
        annotated.getAnnotations().forEachRemaining(key -> {
            String prefixPath = StringUtils.isBlank(path) ? key : path + '.' + key;
            varBindingAnnotations.put(prefixPath, annotated.getAnnotation(key));
        });
    }

    @Nonnull
    public Iterator<String> getTestCaseAnnotationKeys() {
        return testCaseAnnotations.keySet().iterator();
    }

    @Nullable
    public String getTestCaseAnnotation(String key) {
        return testCaseAnnotations.get(key);
    }

    @Nonnull
    public Iterator<String> getVarBindingAnnotationKeys() {
        return varBindingAnnotations.keySet().iterator();
    }

    @Nullable
    public String getVarBindingAnnotation(String key) {
        return varBindingAnnotations.get(key);
    }

}
