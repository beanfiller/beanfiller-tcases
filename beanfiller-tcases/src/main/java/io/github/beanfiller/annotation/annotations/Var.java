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

package io.github.beanfiller.annotation.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker Annotation for Testcase field that has values.
 * Must either contain @Value annotations, or have implicit values (Enum or Boolean)
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Var {

    Value[] value() default {}; // For Enum / Boolean, the values add properties to the value Defs.

    String tag() default "arg"; // Input type from docs, TODO: not sure what to do with it

    String[] when() default {};

    String[] whenNot() default {};

    String[] having() default {};

    String[] exclude() default {}; // Values from underlying type not to use (for Boolean, Enum)

    /**
     * on a nullable field, do not generate null testcase. Short for exclude = {"NA"}
     */
    boolean nullable() default true;
}
