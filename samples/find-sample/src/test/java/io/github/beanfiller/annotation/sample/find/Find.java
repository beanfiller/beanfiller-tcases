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

package io.github.beanfiller.annotation.sample.find;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

public class Find {

    /**
     * returns a String containing all lines of given file that contain given pattern at least once.
     */
    public static String find(String filename, String filecontent, String searchFilename, String pattern) throws IOException {
        if (searchFilename == null) {
            throw new IllegalArgumentException("filename must be given");
        }
        if (StringUtils.isEmpty(pattern)) {
            throw new IllegalArgumentException("pattern must be given");
        }
        if (!searchFilename.equals(filename)) {
            throw new IllegalArgumentException("File not found: " + searchFilename);
        }

        int maxLineLength = 0;
        StringBuilder resultContent = new StringBuilder();
        BufferedReader bufReader = new BufferedReader(new StringReader(filecontent));
        String line;
        while ((line = bufReader.readLine()) != null) {
            maxLineLength = Math.max(maxLineLength, line.length());
            if (line.contains(pattern)) {
                resultContent.append(line).append('\n');
            }
        }
        if (pattern.length() > maxLineLength) {
            throw new IllegalArgumentException("Pattern must not be longer than maximum length line in file");
        }

        validatePattern(pattern);

        return resultContent.toString();
    }

    private static void validatePattern(String pattern) {
        if (pattern.length() == 0) {
            return;
        }
        char[] chars = pattern.toCharArray();
        boolean quoted = chars[0] == '"';
        if (quoted && (chars.length == 1 || chars[chars.length - 1] != '"')) {
            throw new IllegalArgumentException("unterminated quotes");
        }
        for (int i = 1; i < chars.length; i++) {
            // validate quotes
            if (!quoted && (chars[i] == ' ')) {
                throw new IllegalArgumentException("blanks in pattern must be used with quotes");
            }
            // TODO validate quote escaping
        }
    }
}
