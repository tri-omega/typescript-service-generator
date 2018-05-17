package org.omega.typescript.processor.emitters;

import org.omega.typescript.processor.model.TypeDefinition;
import org.omega.typescript.processor.model.TypeKind;

/**
 * Created by kibork on 5/2/2018.
 */
public interface TypeDefinitionEmitter {
    void render(TypeDefinition definition);

    TypeKind getSupportedDefinitionType();
}
