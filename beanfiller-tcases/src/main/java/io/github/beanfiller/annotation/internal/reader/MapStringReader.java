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

package io.github.beanfiller.annotation.internal.reader;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import static java.io.StreamTokenizer.TT_EOF;
import static java.io.StreamTokenizer.TT_WORD;

/**
 * For Strings defining a Map&lt;String, String&gt;, defines a parser to read values
 */
public class MapStringReader {

    @Nonnull
    public static Map<String, String> parse(String... havingStrings) {
        final Map<String, String> flatMap = new HashMap<>();
        for (final String havingString : havingStrings) {
            flatMap.putAll(parseHasValues(havingString));
        }
        return flatMap;
    }

    @Nonnull
    @SuppressWarnings("PMD.PrematureDeclaration")
    static Map<String, String> parseHasValues(String havingString) {
        final Map<String, String> map = new HashMap<>();
        // Stream Tokenizer has confusing API, but seems easiest to parse csv including quote handling
        final StreamTokenizer tokenizer = new StreamTokenizer(new StringReader(havingString));
        // default settings makes tokenizer parser numbers
        tokenizer.resetSyntax();
        tokenizer.wordChars(' ', '9');
        tokenizer.wordChars(';', '~');
        tokenizer.quoteChar('"');
        tokenizer.ordinaryChars(',', ',');
        try {
            int nextToken = tokenizer.nextToken();
            while (nextToken != TT_EOF) {
                final String key = tokenizer.sval;
                if (tokenizer.nextToken() != ':') {
                    throw new IllegalStateException("Having expression must have a colon: '" + havingString + '\'');
                }
                final int wordOrQuote = tokenizer.nextToken();
                if (wordOrQuote != TT_WORD && wordOrQuote != '"') {
                    throw new IllegalStateException("Having expression must have a value after colon: '" + havingString + '\'');
                }
                final String value = tokenizer.sval;
                if (map.put(key, value) != null) {
                    throw new IllegalStateException("Duplicate key: '" + key + '\'');
                }
                nextToken = tokenizer.nextToken();
                if (nextToken != ',' && nextToken != TT_EOF) {
                    throw new IllegalStateException("Having expression must be comma-separated: '" + havingString + '\'');
                }
                nextToken = tokenizer.nextToken();
            }
        } catch (IOException e) {
            throw new RuntimeException("Cannot read arguments from " + havingString, e);
        }
        return map;
    }
}
