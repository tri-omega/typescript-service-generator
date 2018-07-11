/*
 * Copyright (c) 2018 William Frank (info@williamfrank.net)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.omega.typescript.processor;

import org.omega.typescript.processor.model.TypeDefinition;
import org.omega.typescript.processor.model.TypeKind;
import org.omega.typescript.processor.model.TypeOracle;
import org.omega.typescript.processor.utils.TypeUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Map;

/**
 * Created by kibork on 3/12/2018.
 */
public class PredefinedTypes {

    // ------------------ Constants  --------------------

    // ------------------ Fields     --------------------

    // ------------------ Properties --------------------

    // ------------------ Logic      --------------------

    public static void registerTypes(final TypeOracle typeOracle) {
        addPrimitive(typeOracle, Object.class, "any");

        addPrimitive(typeOracle, String.class, "string");

        addPrimitive(typeOracle, Long.class, "number");
        addPrimitive(typeOracle, Integer.class, "number");
        addPrimitive(typeOracle, Short.class, "number");
        addPrimitive(typeOracle, Byte.class, "number");

        addPrimitive(typeOracle, "long", "long", "number");
        addPrimitive(typeOracle, "int", "int", "number");
        addPrimitive(typeOracle, "short", "short", "number");
        addPrimitive(typeOracle, "byte", "byte", "number");
        addPrimitive(typeOracle, "double", "double", "number");
        addPrimitive(typeOracle, "float", "double", "number");

        addPrimitive(typeOracle, "boolean", "boolean", "boolean");
        addPrimitive(typeOracle, Boolean.class, "boolean");

        addPrimitive(typeOracle, BigDecimal.class,"number");
        addPrimitive(typeOracle, BigInteger.class,"number");

        typeOracle.addType(
                new TypeDefinition(Map.class.getName(), Map.class.getSimpleName())
                    .setTypeKind(TypeKind.MAP)
                    .setPredefined(true)
                    .setTypeScriptName("Map")
        );

        typeOracle.addType(
                new TypeDefinition(TypeUtils.ARRAY_TYPE_NAME, TypeUtils.ARRAY_TYPE_NAME)
                    .setTypeKind(TypeKind.COLLECTION)
                    .setPredefined(true)
                    .setTypeScriptName(TypeUtils.ARRAY_TYPE_NAME)
        );

        final GenConfig config = typeOracle.getContext().getGenConfig();
        if (config.isEnableJavaTimeIntegration()) {
            addPrimitive(typeOracle, ZonedDateTime.class, config.getZonedDateTimeType());
            addPrimitive(typeOracle, LocalDateTime.class, config.getLocalDateTimeType());
            addPrimitive(typeOracle, LocalTime.class, config.getTimeType());
            addPrimitive(typeOracle, LocalDate.class, config.getDateType());
        }
    }

    private static void addPrimitive(final TypeOracle typeOracle, final Class<?> clazz, String tsTypeName) {
        addPrimitive(typeOracle, clazz.getName(), clazz.getSimpleName(), tsTypeName);
    }

    private static void addPrimitive(final TypeOracle typeOracle, final String fullTypeName, String shortTypeName, String tsTypeName) {
        typeOracle.addType(
                new TypeDefinition(fullTypeName, shortTypeName)
                        .setTypeKind(TypeKind.PRIMITIVE)
                        .setPredefined(true)
                        .setTypeScriptName(tsTypeName)
        );
    }

}
