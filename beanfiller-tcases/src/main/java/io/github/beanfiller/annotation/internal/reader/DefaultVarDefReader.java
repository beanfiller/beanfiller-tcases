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

import org.apache.commons.lang3.StringUtils;
import io.github.beanfiller.annotation.annotations.Var;
import io.github.beanfiller.annotation.annotations.VarSet;
import io.github.beanfiller.annotation.builders.VarDefBuilder;
import io.github.beanfiller.annotation.builders.VarSetBuilder;
import io.github.beanfiller.annotation.reader.VarDefReader;
import org.cornutum.tcases.IVarDef;
import org.cornutum.tcases.VarDef;
import org.cornutum.tcases.VarValueDef;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;

import static io.github.beanfiller.annotation.internal.reader.ConditionReader.getCondition;
import static io.github.beanfiller.annotation.internal.reader.VarValueDefReader.readVarValueDefs;

/**
 * Given a Java Bean classes annotated with default Tcases annotations, creates an IVarDef
 */
public class DefaultVarDefReader implements VarDefReader {

    @Override
    public boolean appliesTo(@Nonnull FieldWrapper field) {
        boolean hasVar = field.hasAnnotation(Var.class);
        boolean hasVarSet = field.hasAnnotation(VarSet.class);
        if (hasVar && hasVarSet) {
            throw new IllegalStateException("Cannot use @Var and @VarSet on same field");
        }
        if (!hasVar) {
            // TODO: defend against more conrner cases
            Class<?> fieldClass = field.getType();
            if (fieldClass.isEnum()) {
                throw new IllegalStateException("Cannot use Enum as VarSet. Hint: mark the enum field as @Var?");
            }
            if (fieldClass.isPrimitive()) {
                throw new IllegalStateException("Cannot use Primitive as VarSet. Hint: mark the enum field as @Var?");
            }
        }
        return true;
    }

    /**
     * create VarSet of VarDef for a field depending on the annotations.
     */
    @Override
    @Nonnull
    public IVarDef readVarDef(@Nonnull FieldWrapper field) {
        return doReadVarDef(field, null, null);
    }

    /**
     * Recursively usable reading of fields.
     * @param path context of field in current recursion, used for generating conditions
     */
    @Nonnull
    private IVarDef doReadVarDef(@Nonnull FieldWrapper field, String path, String[] conditions) {
        IVarDef varDef;

        if (field.hasAnnotation(Var.class)) {
            varDef = readVarDefFromVarField(field, conditions);
        } else {
            varDef = readVarSet(field, path, conditions);
        }
        return varDef;
    }

    /**
     * Create a VarSet for a field that has no @Var annotation, is not a Primitive or an enum.
     * @param path context of field in current recursion, used for generating conditions
     */
    @Nonnull
    private org.cornutum.tcases.VarSet readVarSet(@Nonnull FieldWrapper field, @Nullable String path, @Nullable String[] conditions) {
        // recursion, TODO: make sure not circular
        VarSetBuilder builder = VarSetBuilder.varSet(field.getName());
        VarSet varSetAnnotation = field.getAnnotation(VarSet.class);
        String[] when;
        String[] whenNot;
        if (varSetAnnotation != null) {
            when = varSetAnnotation.when();
            whenNot = varSetAnnotation.whenNot();
            builder.addAnnotations(MapStringReader.parse(varSetAnnotation.having()));
        } else {
            when = new String[0];
            whenNot = new String[0];
        }
        builder.condition(getCondition(conditions, when, whenNot));

        String[] newConditions = conditions;
        // if null is allowed add a control variable controlling the null testcase
        if (varSetAnnotation != null && varSetAnnotation.nullable()) {
            VarValueDef trueValue = new VarValueDef("true", VarValueDef.Type.VALID);
            String conditionValue = getFieldPath(path, field) + INITIALIZE_TESTCASE_VARNAME;
            trueValue.addProperties(conditionValue);
            trueValue.setCondition(ConditionReader.getCondition(conditions, new String[0], new String[0]));
            VarValueDef falseValue = new VarValueDef("false", VarValueDef.Type.VALID);
            falseValue.setCondition(ConditionReader.getCondition(conditions, new String[0], new String[0]));


            VarDef existsVarDef = new VarDef(INITIALIZE_TESTCASE_VARNAME);
            existsVarDef.addValue(trueValue);
            existsVarDef.addValue(falseValue);

            builder.addMember(existsVarDef);

            // define new conditions for all nested fields, only defined when --init-- true
            if (newConditions == null) {
                newConditions = new String[] {conditionValue};
            } else {
                newConditions = Arrays.copyOf(newConditions, newConditions.length + 1);
                newConditions[newConditions.length - 1] = conditionValue;
            }
        }

        for (FieldWrapper nestedField : field.getNonStaticNestedFields()) {
            builder.addMember(doReadVarDef(nestedField, getFieldPath(path, field), newConditions));
        }
        return builder.build();
    }

    @Nonnull
    private static String getFieldPath(String parentPath, @Nonnull FieldWrapper field) {
        return (StringUtils.isBlank(parentPath) ? "" : parentPath + '-') + field.getName();
    }

    @Nonnull
    private VarDef readVarDefFromVarField(@Nonnull FieldWrapper field, String[] conditions) {
        VarDefBuilder builder = VarDefBuilder.varDef(field.getName());
        Var varAnnotation = field.getAnnotation(Var.class);
        String[] when;
        String[] whenNot;
        if (varAnnotation != null) {
            builder.addAnnotations(MapStringReader.parse(varAnnotation.having()));
            when = varAnnotation.when();
            whenNot = varAnnotation.whenNot();
        } else {
            when = new String[0];
            whenNot = new String[0];
        }
        builder.condition(ConditionReader.getCondition(conditions, when, whenNot));
        readVarValueDefs(field, conditions).forEach(builder::addValue);
        return builder.build();
    }

}
