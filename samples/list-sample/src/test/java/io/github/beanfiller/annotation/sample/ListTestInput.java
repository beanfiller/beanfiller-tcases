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

import io.github.beanfiller.annotation.creator.OutputAnnotationContainer;
import io.github.beanfiller.annotation.creator.TestMetadataAware;

import javax.annotation.Nonnull;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;


public class ListTestInput<T> extends AbstractList<T> implements TestMetadataAware {

    private final List<T> wrapped;
    private int testCaseId;

    private boolean failure;

    private OutputAnnotationContainer outputAnnotations;

    @Override
    public void setTestMetadata(int id, boolean isFailure, @Nonnull OutputAnnotationContainer outputAnnotationContainer) {
        this.testCaseId = id;
        this.failure = isFailure;
        this.outputAnnotations = outputAnnotationContainer;
    }

    public int getTestCaseId() {
        return testCaseId;
    }

    public OutputAnnotationContainer having() {
        return outputAnnotations;
    }

    public boolean isFailure() {
        return failure;
    }

    public ListTestInput() {
        this(new ArrayList<>());
    }

    public ListTestInput(List<T> wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public T get(final int index) {
        return wrapped.get(index);
    }

    @Override
    public int size() {
        return wrapped.size();
    }

    @Override
    public boolean add(final T t) {
        return wrapped.add(t);
    }
}
