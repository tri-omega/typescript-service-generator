package org.omega.typescript.processor.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kibork on 1/22/2018.
 */
public class TypeDefinition {

    // ------------------ Constants  --------------------

    // ------------------ Fields     --------------------

    private final DefinitionId definitionId;

    private List<TypeDefinition> superTypes = new ArrayList<>();

    private boolean initialized = false;

    // ------------------ Properties --------------------

    public DefinitionId getDefinitionId() {
        return definitionId;
    }

    public List<TypeDefinition> getSuperTypes() {
        return superTypes;
    }

    public boolean isInitialized() {
        return initialized;
    }


    // ------------------ Logic      --------------------

    public TypeDefinition(DefinitionId definitionId) {
        this.definitionId = definitionId;
    }
}
