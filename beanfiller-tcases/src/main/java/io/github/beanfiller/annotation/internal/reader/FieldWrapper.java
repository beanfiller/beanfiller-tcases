package io.github.beanfiller.annotation.internal.reader;

import javax.annotation.Nonnull;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper class mostly to allow easy testing
 */
public class FieldWrapper {

    private final Field field;

    FieldWrapper(Field field) {
        this.field = field;
    }

    public static FieldWrapper of(@Nonnull Field field) {
        return new FieldWrapper(field);
    }

    public <T extends Annotation> boolean hasAnnotation(Class<T> clazz) {
        return field.getAnnotation(clazz) != null;
    }

    public <T extends Annotation> T getAnnotation(Class<T> clazz) {
        return field.getAnnotation(clazz);
    }

    public String getName() {
        return field.getName();
    }

    public Class<?> getType() {
        return field.getType();
    }

    @Nonnull
    public List<FieldWrapper> getNonStaticNestedFields() {
        return getNonStaticFields(field.getType());
    }

    @Nonnull
    private static List<FieldWrapper> getNonStaticFields(Class<?> clazz) {
        List<FieldWrapper> result = new ArrayList<>();
        for (Field nestedField : clazz.getDeclaredFields()) {
            if (!Modifier.isStatic(nestedField.getModifiers())) {
                result.add(of(nestedField));
            }
        }
        return result;
    }
}
