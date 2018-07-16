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

package io.github.beanfiller.annotation.util;

import org.apache.commons.lang3.builder.RecursiveToStringStyle;

/**
 * helper for reflection-based toString
 * TODO: Replace with proper toString once Model became stable
 */
public class CustomToStringStyle extends RecursiveToStringStyle {

    private static final long serialVersionUID = 996242944891615052L;

    public static final CustomToStringStyle INSTANCE = new CustomToStringStyle();

    // for Json-like toString
    public CustomToStringStyle() {
        super();
        super.setUseClassName(false);
        super.setUseIdentityHashCode(false);
        super.setContentStart("{");
        super.setContentEnd("}");
        super.setArrayStart("[");
        super.setArrayEnd("]");
        super.setFieldSeparator(", ");
        super.setFieldNameValueSeparator(":");
        super.setNullText("null");
        super.setSummaryObjectStartText("\"<");
        super.setSummaryObjectEndText(">\"");
        super.setSizeStartText("\"<size=");
        super.setSizeEndText(">\"");
    }
}