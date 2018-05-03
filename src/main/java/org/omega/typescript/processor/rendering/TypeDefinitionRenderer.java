package org.omega.typescript.processor.rendering;

import org.omega.typescript.processor.model.TypeDefinition;
import org.omega.typescript.processor.model.TypeKind;

/**
 * Created by kibork on 5/2/2018.
 */
public interface TypeDefinitionRenderer {
    void render(TypeDefinition definition);

    TypeKind getSupportedDefinitionType();
}
