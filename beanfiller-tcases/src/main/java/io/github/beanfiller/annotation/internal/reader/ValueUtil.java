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

package io.github.beanfiller.annotation.internal.reader;

import io.github.beanfiller.annotation.annotations.Value;
import io.github.beanfiller.annotation.builders.VarValueDefBuilder;
import org.cornutum.tcases.TestCase;
import org.cornutum.tcases.VarValueDef;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static io.github.beanfiller.annotation.internal.reader.ConditionReader.getCondition;

class ValueUtil {

    private ValueUtil() {
    }

    @Nonnull
    static VarValueDef createVarValueDef(String name, Value varValue, @Nullable String[] conditions, boolean isNull) {
        final VarValueDefBuilder builder = new VarValueDefBuilder(name, typeOf(varValue, VarValueDef.Type.VALID));
        builder.addAnnotations(MapStringReader.parse(varValue.having()));
        builder.addProperties(varValue.properties());
        builder.condition(getCondition(conditions, varValue.when(), varValue.whenNot()));
        if (isNull) {
            builder.setNull();
        }
        return builder.build();
    }

    @Nonnull
    static VarValueDef createValidVarValueDef(String name, @Nullable String... conditions) {
        return new VarValueDefBuilder(name, VarValueDef.Type.VALID)
                .condition(getCondition(conditions, new String[0], new String[0]))
                .build();
    }

    @Nonnull
    static VarValueDef createNullVarValueDef(@Nullable String... conditions) {
        return new VarValueDefBuilder("NA", VarValueDef.Type.VALID)
                .condition(getCondition(conditions, new String[0], new String[0]))
                .setNull()
                .build();
    }

    /**
     * returns the Tcases Type given attributes of a Value annotation.
     */
    @Nonnull
    static VarValueDef.Type typeOf(Value varValue, VarValueDef.Type defaultType) {
        if (varValue.type() == TestCase.Type.FAILURE) {
            return VarValueDef.Type.FAILURE;
        }
        if (varValue.once()) {
            return VarValueDef.Type.ONCE;
        }
        return defaultType;
    }
}
