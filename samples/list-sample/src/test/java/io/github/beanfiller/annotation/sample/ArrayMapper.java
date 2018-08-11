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

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.beanfiller.annotation.creator.VariableToClassValueMapper;
import io.github.beanfiller.annotation.internal.reader.FieldWrapper;
import org.cornutum.tcases.VarBinding;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Collection;

public class ArrayMapper implements VariableToClassValueMapper {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public boolean appliesTo(@Nonnull final String varname, @Nonnull final Class<?> parentClass) {
        final FieldWrapper field = getField(parentClass, varname);
        if (Collection.class.isAssignableFrom(field.getType())) {
            return true;
        }
        if (field.getType().isArray()) {
            return true;
        }
        return false;
    }

    @Override
    public void setFieldValueAs(@Nonnull final String varname, @Nonnull final Object instance, @Nonnull final VarBinding varBinding) {
        final FieldWrapper field = getField(instance.getClass(), varname);
        final Object value;
        try {
            value = OBJECT_MAPPER.readValue(varBinding.getValue().toString(), field.getType());
        } catch (IOException e) {
            throw new IllegalStateException("Cannot deserialize field " + varname, e);
        }
        setFieldValue(instance, varname, value);
    }

    public static <T> String encodeToString(Collection<T> list) {
        try {
            return OBJECT_MAPPER.writeValueAsString(list);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
