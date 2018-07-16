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

import javax.annotation.Nonnull;

/**
 * Default abstract implementation of TestMetadataAware
 */
public abstract class AbstractTestInput implements TestMetadataAware {

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
}
