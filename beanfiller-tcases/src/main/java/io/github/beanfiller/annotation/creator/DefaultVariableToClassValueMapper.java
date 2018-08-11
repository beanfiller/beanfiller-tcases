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

import io.github.beanfiller.annotation.internal.reader.FieldWrapper;
import org.cornutum.tcases.VarBinding;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class DefaultVariableToClassValueMapper implements VariableToClassValueMapper {

    private static final Set<Class<?>> APPLICABLE_CLASSES;

    static {
        APPLICABLE_CLASSES = new HashSet<>(Arrays.asList(
                Boolean.class,
                boolean.class,
                Integer.class,
                int.class,
                Long.class,
                long.class,
                Double.class,
                double.class,
                Float.class,
                float.class,
                Short.class,
                short.class,
                Byte.class,
                byte.class,
                Character.class,
                char.class,
                String.class,
                BigInteger.class,
                BigDecimal.class));
    }


    @Override
    public boolean appliesTo(final String varname, final Class<?> parentClass) {
        final FieldWrapper f = getField(parentClass, varname);
        return (f.getType().isEnum() || APPLICABLE_CLASSES.contains(f.getType()));
    }

    @Override
    public void setFieldValueAs(final String varname, final Object instance, final VarBinding varBinding) {
        final FieldWrapper targetField = getField(instance.getClass(), varname);
        final Object rawValue = varBinding.getValue();
        if (rawValue instanceof String) {
            final Object value = getClassValueAs((String) rawValue, targetField.getType());
            if (value != null) {
                setFieldValue(instance, varname, value);
            }
        } else if (rawValue != null) {
            setFieldValue(instance, varname, rawValue);
        }
    }

    /**
     * Util method to set a bean field based on a String Value.
     */
    @CheckForNull
    @SuppressWarnings({"unchecked", "PMD.CyclomaticComplexity"})
    <C> C getClassValueAs(@Nullable String valueString, Class<C> targetType) {
        if (valueString == null) {
            return null;
        }
        final C result;
        // TODO: Find better way to handle types, also primitive types
        if (targetType == Boolean.class || targetType == boolean.class) {
            result = (C) Boolean.valueOf(valueString);
        } else if (targetType == Integer.class || targetType == int.class) {
            result = (C) Integer.valueOf(valueString);
        } else if (targetType == Long.class || targetType == long.class) {
            result = (C) Long.valueOf(valueString);
        } else if (targetType == Double.class || targetType == double.class) {
            result = (C) Double.valueOf(valueString);
        } else if (targetType == Float.class || targetType == float.class) {
            result = (C) Float.valueOf(valueString);
        } else if (targetType == Short.class || targetType == short.class) {
            result = (C) Short.valueOf(valueString);
        } else if (targetType == Byte.class || targetType == byte.class) {
            result = (C) Byte.valueOf(valueString);
        } else if (targetType == Character.class || targetType == char.class) {
            if (valueString.length() != 1) {
                throw new IllegalStateException("Cannot assign String '" + valueString
                        + "' of length " + valueString.length() + " to character field");
            }
            result = (C) Character.valueOf(valueString.charAt(0));
        } else if (targetType == BigInteger.class) {
            result = (C) new BigInteger(valueString);
        } else if (targetType == BigDecimal.class) {
            result = (C) new BigDecimal(valueString);
        } else if (targetType.isEnum()) {
            result = (C) Enum.valueOf((Class<Enum>) targetType, valueString);
        } else {
            result = (C) valueString;
        }
        return result;
    }

}
