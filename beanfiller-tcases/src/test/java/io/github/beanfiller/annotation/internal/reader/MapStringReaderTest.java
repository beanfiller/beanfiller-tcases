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

import org.assertj.core.util.Maps;
import org.junit.Test;

import java.util.AbstractMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static io.github.beanfiller.annotation.internal.reader.MapStringReader.parseHasValues;


public class MapStringReaderTest {

    @Test
    public void testParse() {
        assertThat(MapStringReader.parse(new String[]{})).isEmpty();
        assertThat(MapStringReader.parse(new String[]{"foo:bar"})).containsEntry("foo", "bar");
    }

    @Test
    public void testParseHasValues() {
        assertThat(parseHasValues("")).isEmpty();
        assertThat(parseHasValues("foo:bar")).isEqualTo(Maps.newHashMap("foo", "bar"));
        assertThat(parseHasValues("a1:b1,a2:b2")).isEqualTo(
                Stream.of(
                        new AbstractMap.SimpleEntry<>("a1", "b1"),
                        new AbstractMap.SimpleEntry<>("a2", "b2"))
                        .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue)));

        assertThatThrownBy(() -> parseHasValues("a,")).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> parseHasValues("a:1,a:2")).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> parseHasValues("a:1,a:2:3")).isInstanceOf(IllegalStateException.class);
    }
}