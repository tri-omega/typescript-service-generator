package org.omega.typescript.processor.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kibork on 4/9/2018.
 */
@Data
public class TypeInstanceDefinition {

    // ------------------ Constants  --------------------

    // ------------------ Fields     --------------------

    private TypeDefinition typeDefinition;

    private List<TypeInstanceDefinition> genericTypeArguments = new ArrayList<>();

    // ------------------ Properties --------------------

    public String getFullName() {
        return typeDefinition.getFullName();
    }

    public String getShortName() {
        return typeDefinition.getShortName();
    }

    public boolean isInitialized() {
        return typeDefinition.isInitialized();
    }

    public boolean isPredefined() {
        return typeDefinition.isPredefined();
    }

    public List<PropertyDefinition> getProperties() {
        return typeDefinition.getProperties();
    }

    public List<EnumConstant> getEnumConstants() {
        return typeDefinition.getEnumConstants();
    }

    public TypeKind getTypeKind() {
        return typeDefinition.getTypeKind();
    }

    public String getTypeScriptName() {
        return typeDefinition.getTypeScriptName();
    }

    public List<TypeInstanceDefinition> getSuperTypes() {
        return typeDefinition.getSuperTypes();
    }


    // ------------------ Logic      --------------------


    public TypeInstanceDefinition() {
    }

    public TypeInstanceDefinition(final TypeDefinition typeDefinition) {
        this.typeDefinition = typeDefinition;
    }
}
