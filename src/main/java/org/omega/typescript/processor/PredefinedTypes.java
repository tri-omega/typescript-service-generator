package org.omega.typescript.processor;

import org.omega.typescript.processor.model.TypeDefinition;
import org.omega.typescript.processor.model.TypeKind;
import org.omega.typescript.processor.utils.TypeUtils;

import java.math.BigDecimal;
import java.math.BigInteger;

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
                new TypeDefinition(TypeUtils.ARRAY_TYPE_NAME, TypeUtils.ARRAY_TYPE_NAME)
                    .setTypeKind(TypeKind.COLLECTION)
                    .setPredefined(true)
                    .setTypeScriptName(TypeUtils.ARRAY_TYPE_NAME)
        );
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
