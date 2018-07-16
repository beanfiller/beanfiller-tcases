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

package io.github.beanfiller.annotation.writer;

import org.cornutum.tcases.SystemTestDef;
import org.cornutum.tcases.io.SystemTestDocWriter;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Utils wrapper class around SystemTestDocWriter
 */
public class SystemTestDefWriter {

    /**
     * writes XML to debug log of
     */
    @Nonnull
    public String createSystemDefXML(SystemTestDef systemTestDef) {
        StringWriter writer = new StringWriter();
        try (SystemTestDocWriter xmlwriter = createSysDocWriter(writer)) {
            xmlwriter.write(systemTestDef);
            xmlwriter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return writer.toString();
    }


    public void writeSystemDefToFile(SystemTestDef systemTestDef, Path baseFile) {
        try (OutputStream outStream = Files.newOutputStream(baseFile);
             SystemTestDocWriter writer = createSysDocWriter(outStream)
        ) {
            writer.write(systemTestDef);
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // VisibleForTesting
    SystemTestDocWriter createSysDocWriter(OutputStream outStream) {
        return new SystemTestDocWriter(outStream);
    }

    // VisibleForTesting
    SystemTestDocWriter createSysDocWriter(StringWriter writer) {
        return new SystemTestDocWriter(writer);
    }
}
