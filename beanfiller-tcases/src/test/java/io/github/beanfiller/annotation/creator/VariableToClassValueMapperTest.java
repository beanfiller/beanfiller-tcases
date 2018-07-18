package io.github.beanfiller.annotation.creator;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.cornutum.tcases.VarBinding;
import org.junit.Test;

import javax.annotation.Nonnull;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SuppressFBWarnings("UWF_NULL_FIELD")
public class VariableToClassValueMapperTest {

    StubMapper mapper = new StubMapper();

    @Test
    public void testGetField() {
        assertThatThrownBy(() -> mapper.getField(SimpleBean.class, "parentValue2"))
                .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("Field not found")
        .hasMessageContaining("parentValue2");

        assertThat(mapper.getField(SimpleBean.class, "parentValue")).isNotNull();

        assertThatThrownBy(() -> mapper.getField(SimpleBean.class, "value2"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Field not found")
                .hasMessageContaining("value2");

        assertThat(mapper.getField(SimpleBean.class, "value")).isNotNull();
    }

    @Test
    public void testSetFieldValue() {
        SimpleBean bean = new SimpleBean();
        mapper.setFieldValue(bean, "parentValue", "v0");
        mapper.setFieldValue(bean, "value", "v1");
        assertThat(bean.parentValue).isEqualTo("v0");
        assertThat(bean.value).isEqualTo("v1");

        assertThatThrownBy(() -> mapper.setFieldValue(bean, "parentValue2", "v2"))
                .isInstanceOf(RuntimeException.class)
                .hasRootCauseInstanceOf(NoSuchMethodException.class);
    }

    private static class ParentBean {
        public final String parentValue = null;
    }

    private static class SimpleBean extends ParentBean {
        private final String value = null;
    }

    private static class StubMapper implements VariableToClassValueMapper {

        @Override
        public boolean appliesTo(@Nonnull String varname, @Nonnull Class<?> parentClass) {
            return false;
        }

        @Override
        public void setFieldValueAs(@Nonnull String varname, @Nonnull Object instance, @Nonnull VarBinding varBinding) {

        }
    }
}