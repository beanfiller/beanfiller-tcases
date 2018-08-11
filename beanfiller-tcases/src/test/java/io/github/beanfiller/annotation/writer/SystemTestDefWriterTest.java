package io.github.beanfiller.annotation.writer;

import io.github.beanfiller.annotation.annotations.FunctionDef;
import io.github.beanfiller.annotation.annotations.Value;
import io.github.beanfiller.annotation.annotations.Var;
import io.github.beanfiller.annotation.creator.SystemTestDefCreator;
import org.cornutum.tcases.SystemTestDef;
import org.cornutum.tcases.io.SystemTestDocWriter;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

public class SystemTestDefWriterTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private final SystemTestDef fooSystem = new SystemTestDefCreator("fooSystem", Sample1.class).create();

    @FunctionDef("functionFooName")
    private static class Sample1 {

        private static Boolean ignoreStatic;

        @Var
        private Boolean fooVar;

        @Var(value = @Value("foo"))
        private String stringVar;

        @Var
        private Locale.FilteringMode enumVar;

    }

    @Test
    public void createSystemDefXML() {
        String xml = new SystemTestDefWriter().createSystemDefXML(fooSystem);
        assertThat(xml)
                .contains("<TestCases system=\"fooSystem\">")
                .contains("<Function name=\"functionFooName\">")
                .contains("<Var name=\"fooVar\" value=\"true\">")
                .contains("<Var name=\"fooVar\" value=\"false\">")
                .contains("<Var NA=\"true\" name=\"fooVar\">")
                .contains("<Var name=\"stringVar\" value=\"foo\">")
                .contains("<Var NA=\"true\" name=\"stringVar\">")
                .contains("<Var name=\"enumVar\" value=\"REJECT_EXTENDED_RANGES\">")
                .contains("<Var name=\"enumVar\" value=\"MAP_EXTENDED_RANGES\">")
                .contains("<Var NA=\"true\" name=\"enumVar\">");

        assertThatThrownBy(() -> new SystemTestDefWriterFailingStub().createSystemDefXML(fooSystem))
                .isInstanceOf(RuntimeException.class)
                .hasCauseExactlyInstanceOf(IOException.class);
    }

    @Test
    public void writeSystemDefToFile() {
        new SystemTestDefWriter().writeSystemDefToFile(fooSystem,
                folder.getRoot().toPath().resolve("Foo.xml"));
        File[] files = folder.getRoot().listFiles();
        if (files == null) { // for spotbugs
            fail("files null");
        }
        assertThat(files).hasSize(1);
        File file = files[0];
        assertThat(file).hasName("Foo.xml");

        assertThatThrownBy(() -> new SystemTestDefWriterFailingStub().writeSystemDefToFile(fooSystem,
                folder.getRoot().toPath().resolve("Foo.xml")))
                .isInstanceOf(RuntimeException.class)
                .hasCauseExactlyInstanceOf(IOException.class);
    }

    private static final class SystemTestDefWriterFailingStub extends SystemTestDefWriter {
        static final SystemTestDocWriter WRITER_MOCK = mock(SystemTestDocWriter.class);
        static {
            doThrow(new RuntimeException("Test stub exception", new IOException("Stub cause"))).when(WRITER_MOCK).flush();
        }
        @Override
        SystemTestDocWriter createSysDocWriter(OutputStream outStream) {
            return WRITER_MOCK;
        }

        @Override
        SystemTestDocWriter createSysDocWriter(StringWriter writer) {
            return WRITER_MOCK;
        }
    }
}