package org.omega.typescript.processor.rendering;

import org.omega.typescript.processor.model.TypeInstanceDefinition;
import org.omega.typescript.processor.model.TypeKind;

/**
 * Created by kibork on 5/2/2018.
 */
public class TypeInstanceRenderer {

    // ------------------ Constants  --------------------

    // ------------------ Fields     --------------------

    private final RenderingContext context;

    // ------------------ Properties --------------------

    // ------------------ Logic      --------------------

    public TypeInstanceRenderer(final RenderingContext context) {
        this.context = context;
    }

    public String renderTypeInstance(final TypeInstanceDefinition instanceDefinition) {
        if (instanceDefinition.getTypeKind() == TypeKind.COLLECTION) {
            final String baseTypeName = getCollectionBaseType(instanceDefinition, 0);
            return baseTypeName + instanceDefinition.getTypeScriptName();
        } else if (instanceDefinition.getTypeKind() == TypeKind.MAP) {
            return getMapInstance(instanceDefinition);
        }
        return instanceDefinition.getTypeScriptName();
    }

    private String getMapInstance(TypeInstanceDefinition instanceDefinition) {
        if (!instanceDefinition.getGenericTypeArguments().isEmpty()) {
            return "{ [index: " + getCollectionBaseType(instanceDefinition, 0) + "]: " + getCollectionBaseType(instanceDefinition, 1) + " }";
        } else {
            return "{ }";
        }
    }

    private String getCollectionBaseType(final TypeInstanceDefinition instanceDefinition, final int index) {
        final String baseTypeName;
        if (instanceDefinition.getGenericTypeArguments().size() > index) {
            baseTypeName = renderTypeInstance(instanceDefinition.getGenericTypeArguments().get(index));
        } else {
            baseTypeName = context.getProcessingContext().getTypeOracle().getAny().getTypeScriptName();
        }
        return baseTypeName;
    }
}
