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

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.github.beanfiller.annotation.internal.reader.FieldWrapper;
import org.cornutum.tcases.VarBinding;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * Interface for classes creating values of fields based on String variable values
 */
public interface VariableToClassValueMapper {

    /**
     * @param varname a tcases variable with a string value to drive instantiation
     * @param parentClass the class which should be filled based on the variable value
     * @return true if the implementing class is willing to be responsible to provide a value in getFieldValueAs.
     */
    boolean appliesTo(String varname, Class<?> parentClass);

    /**
     * sets an Instance suitable to be set as this fields value, may use field class or annotations.
     */
    void setFieldValueAs(String varname, Object instance, VarBinding varBinding);

    @Nonnull
    default FieldWrapper getField(Class<?> clazz, String name) {
        final Field field = getFieldAnywhere(clazz, name);

        if (field == null) {
            throw new IllegalStateException("Field not found " + name
                    + " on '" + clazz);
        }

        return FieldWrapper.of(field);
    }

    default void setFieldValue(final Object instance, final String name, final Object valueOrNull) {
        final Field f;
        try {
            f = getFieldAnywhere(instance.getClass(), name);
            if (f == null) {
                throw new NoSuchMethodException(name);
            }
            AccessController.doPrivileged((PrivilegedAction<Object>) () -> {
                f.setAccessible(true);
                return null;
            });
            f.set(instance, valueOrNull);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Error setting " + name
                    + " to '" + valueOrNull + '\'', e);

        }
    }

    /**
     * getField only works for public field, getDeclaredField only works on current class.
     * Need to recurse to get non-public fields inherited from parent class
     */
    @CheckForNull
    @SuppressWarnings({
            "PMD.EmptyCatchBlock",
            "PMD.AvoidCatchingGenericException",
            "checkstyle:EmptyCatchBlock"
    })
    @SuppressFBWarnings("DE_MIGHT_IGNORE")
    default Field getFieldAnywhere(Class<?> clazz, String name) {
        Field field = null;
        Class<?> loopClazz = clazz;
        while ((loopClazz != null) && (field == null)) {
            try {
                field = loopClazz.getDeclaredField(name);
            } catch (NoSuchFieldException | RuntimeException e) {
                // pass
            }
            loopClazz = loopClazz.getSuperclass();
        }
        return field;
    }
}
