package org.omega.typescript.processor.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kibork on 1/22/2018.
 */
@Data
public class TypeDefinition {

    // ------------------ Constants  --------------------

    // ------------------ Fields     --------------------

    private final String fullName;

    private final String shortName;

    private String typeScriptName;

    private TypeKind typeKind;

    private List<TypeDefinition> superTypes = new ArrayList<>();

    private boolean initialized = false;

    private boolean predefined = false;

    private List<PropertyDefinition> properties = new ArrayList<>();

    // ------------------ Properties --------------------

    // ------------------ Logic      --------------------


    public TypeDefinition(String fullName, String shortName) {
        this.fullName = fullName;
        this.shortName = shortName;
    }

    public TypeDefinition setPredefined(boolean predefined) {
        this.predefined = predefined;
        if (predefined) {
            this.initialized = true;
        }
        return this;
    }
}
