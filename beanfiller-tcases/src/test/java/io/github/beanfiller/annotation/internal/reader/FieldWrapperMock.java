package io.github.beanfiller.annotation.internal.reader;

import javax.annotation.Nonnull;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * mocks access to an underlying field for easier testing
 */
public class FieldWrapperMock extends FieldWrapper {

    private Map<Class<? extends Annotation>, Annotation> annotations;
    private final String name;
    private final Class<?> type;
    private List<FieldWrapper> nestedFields = new ArrayList<>();

    public FieldWrapperMock(String name, Class<?> type, Annotation... annotations) {
        super(null);
        this.name = name;
        this.type = type;
        this.annotations = Arrays.stream(annotations).collect(Collectors.toMap(Annotation::getClass, Function.identity()));
    }

    public static FieldWrapperMock field(String name, Class<?> type, Annotation... annotations) {
        return new FieldWrapperMock(name, type, annotations);
    }

    public static FieldWrapperMock stringField(String name, Annotation... annotations) {
        return field(name, String.class, annotations);
    }

    @Override
    public <T extends Annotation> boolean hasAnnotation(Class<T> clazz) {
        return annotations.containsKey(clazz);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Annotation> T getAnnotation(Class<T> clazz) {
        return (T) annotations.get(clazz);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Class<?> getType() {
        return type;
    }

    @Override
    @Nonnull
    public List<FieldWrapper> getNonStaticNestedFields() {
        return nestedFields;
    }
}
