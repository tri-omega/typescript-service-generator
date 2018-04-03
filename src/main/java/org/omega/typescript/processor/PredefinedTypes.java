package org.omega.typescript.processor;

import org.omega.typescript.processor.model.TypeDefinition;
import org.omega.typescript.processor.model.TypeKind;

/**
 * Created by kibork on 3/12/2018.
 */
public class PredefinedTypes {

    // ------------------ Constants  --------------------

    // ------------------ Fields     --------------------

    // ------------------ Properties --------------------

    // ------------------ Logic      --------------------

    public static void registerTypes(final TypeOracle typeOracle) {
        typeOracle.addType(
                new TypeDefinition("java.lang.String", "String")
                    .setTypeKind(TypeKind.PRIMITIVE)
                    .setPredefined(true)
                    .setTypeScriptName("string")
        );

        typeOracle.addType(
                new TypeDefinition("java.lang.Long", "Long")
                    .setTypeKind(TypeKind.PRIMITIVE)
                    .setPredefined(true)
                    .setTypeScriptName("number")
        );

        typeOracle.addType(
                new TypeDefinition("long", "long")
                        .setTypeKind(TypeKind.PRIMITIVE)
                        .setPredefined(true)
                        .setTypeScriptName("number")
        );
    }

}
