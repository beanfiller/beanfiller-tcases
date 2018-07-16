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

package io.github.beanfiller.annotation.creator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class DefaultStringToValueMapperTest {

    private final String valueString;
    private final Class<?> typeClass;
    private final Object value;

    public DefaultStringToValueMapperTest(String valueString, Class<?> typeClass, Object value) {
        this.valueString = valueString;
        this.typeClass = typeClass;
        this.value = value;
    }

    @Parameterized.Parameters(name = "{index} {0} as {1} = {2}")
    public static Collection<Object[]> parameters() {
        return Arrays.asList(new Object[][]{
                {null, Boolean.class, null},
                {null, boolean.class, null},
                {null, Integer.class, null},
                {null, int.class, null},
                {null, Double.class, null},
                {null, double.class, null},
                {null, Float.class, null},
                {null, float.class, null},
                {null, Long.class, null},
                {null, long.class, null},
                {null, Short.class, null},
                {null, short.class, null},
                {null, Byte.class, null},
                {null, byte.class, null},
                {null, Character.class, null},
                {null, char.class, null},
                {null, BigDecimal.class, null},
                {null, BigInteger.class, null},
                {null, String.class, null},
                {null, SAMPLE.class, null},

                {"true", Boolean.class, Boolean.TRUE},
                {"true", boolean.class, Boolean.TRUE},

                {"-123456", Integer.class, -123456},
                {Integer.toString(Integer.MIN_VALUE), Integer.class, Integer.MIN_VALUE},
                {Integer.toString(Integer.MAX_VALUE), Integer.class, Integer.MAX_VALUE},
                {"-123456", int.class, -123456},
                {Integer.toString(Integer.MIN_VALUE), int.class, Integer.MIN_VALUE},
                {Integer.toString(Integer.MAX_VALUE), int.class, Integer.MAX_VALUE},

                {"-123456", Double.class, -123456.0},
                {"-123456.12345", Double.class, -123456.12345},
                {Double.toString(Double.MIN_VALUE), Double.class, Double.MIN_VALUE},
                {Double.toString(Double.MAX_VALUE), Double.class, Double.MAX_VALUE},
                {"-123456", double.class, -123456.0},
                {"-123456.12345", double.class, -123456.12345},
                {Double.toString(Double.MIN_VALUE), double.class, Double.MIN_VALUE},
                {Double.toString(Double.MAX_VALUE), double.class, Double.MAX_VALUE},

                {"-123456", Float.class, -123456.0f},
                {"-123456.12345", Float.class, -123456.12345f},
                {Float.toString(Float.MIN_VALUE), Float.class, Float.MIN_VALUE},
                {Float.toString(Float.MAX_VALUE), Float.class, Float.MAX_VALUE},
                {"-123456", float.class, -123456.0f},
                {"-123456.12345", float.class, -123456.12345f},
                {Float.toString(Float.MIN_VALUE), float.class, Float.MIN_VALUE},
                {Float.toString(Float.MAX_VALUE), float.class, Float.MAX_VALUE},

                {"-123456", Long.class, -123456L},
                {Long.toString(Long.MIN_VALUE), Long.class, Long.MIN_VALUE},
                {Long.toString(Long.MAX_VALUE), Long.class, Long.MAX_VALUE},
                {"-123456", long.class, -123456L},
                {Long.toString(Long.MIN_VALUE), long.class, Long.MIN_VALUE},
                {Long.toString(Long.MAX_VALUE), long.class, Long.MAX_VALUE},

                {"-12345", Short.class, (short) -12345},
                {Short.toString(Short.MIN_VALUE), Short.class, Short.MIN_VALUE},
                {Short.toString(Short.MAX_VALUE), Short.class, Short.MAX_VALUE},
                {"-12345", short.class, (short) -12345},
                {Short.toString(Short.MIN_VALUE), short.class, Short.MIN_VALUE},
                {Short.toString(Short.MAX_VALUE), short.class, Short.MAX_VALUE},

                {"-123", Byte.class, (byte) -123},
                {Byte.toString(Byte.MIN_VALUE), Byte.class, Byte.MIN_VALUE},
                {Byte.toString(Byte.MAX_VALUE), Byte.class, Byte.MAX_VALUE},
                {"-123", byte.class, (byte) -123},
                {Byte.toString(Byte.MIN_VALUE), byte.class, Byte.MIN_VALUE},
                {Byte.toString(Byte.MAX_VALUE), byte.class, Byte.MAX_VALUE},

                {"x", Character.class, 'x'},
                {"x", char.class, 'x'},
                {"-123456.12345", BigDecimal.class, new BigDecimal("-123456.12345")},
                {"-123456", BigInteger.class, new BigInteger("-123456")},

                {"foo", String.class, "foo"},

                {"V1", SAMPLE.class, SAMPLE.V1},
                {"V_2", SAMPLE.class, SAMPLE.V_2}
        });
    }


    @Test
    public void testGetValueAs() {
        assertThat(new DefaultStringToValueMapper().getClassValueAs(valueString, typeClass)).isEqualTo(value);
    }

    private enum SAMPLE {
        V1,
        V_2;
    }
}