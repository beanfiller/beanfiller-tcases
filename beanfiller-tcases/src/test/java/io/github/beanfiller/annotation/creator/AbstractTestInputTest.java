package io.github.beanfiller.annotation.creator;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AbstractTestInputTest {

    @Test
    public void setTestMetadata() {
        AbstractTestInput input = new AbstractTestInput() {
        };
        OutputAnnotationContainer container = new OutputAnnotationContainer();
        input.setTestMetadata(123, true, container);

        assertThat(input.getTestCaseId()).isEqualTo(123);
        assertThat(input.isFailure()).isTrue();
        assertThat(input.having()).isSameAs(container);
    }
}