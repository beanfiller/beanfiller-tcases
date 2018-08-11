package io.github.beanfiller.annotation.sample;

import io.github.beanfiller.annotation.creator.VariableToClassValueMapper;
import org.cornutum.tcases.VarBinding;

import javax.annotation.Nonnull;
import java.util.function.Function;

public class WrappedTestInputMapper<E extends Enum<?>> implements VariableToClassValueMapper {

    private final Function<String, E> stringToValue;

    public WrappedTestInputMapper(Function<String, E> stringToValue) {
        this.stringToValue = stringToValue;
    }

    @Override
    public boolean appliesTo(@Nonnull final String varname, @Nonnull final Class<?> parentClass) {
        if (SimpleTestInput.class.isAssignableFrom(parentClass)) {
            return true;
        }
        return false;
    }

    @Override
    public void setFieldValueAs(@Nonnull final String varname, @Nonnull final Object instance, @Nonnull final VarBinding varBinding) {
        setFieldValue(instance, varname, stringToValue.apply(varBinding.getValue().toString()));
    }

}
