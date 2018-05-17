package org.omega.typescript.processor.emitters;

import org.omega.typescript.processor.model.TypeInstanceDefinition;
import org.omega.typescript.processor.model.TypeKind;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by kibork on 5/2/2018.
 */
public class TypeInstanceEmitter {

    // ------------------ Constants  --------------------

    // ------------------ Fields     --------------------

    private final EmitContext context;

    // ------------------ Properties --------------------

    // ------------------ Logic      --------------------

    public TypeInstanceEmitter(final EmitContext context) {
        this.context = context;
    }

    public String renderTypeInstance(final TypeInstanceDefinition instanceDefinition) {
        if (instanceDefinition.getTypeKind() == TypeKind.COLLECTION) {
            final String baseTypeName = getCollectionBaseType(instanceDefinition, 0);
            return baseTypeName + instanceDefinition.getTypeScriptName();
        } else if (instanceDefinition.getTypeKind() == TypeKind.MAP) {
            return getMapInstance(instanceDefinition);
        }
        return instanceDefinition.getTypeScriptName() + genericArguments(instanceDefinition.getGenericTypeArguments());
    }

    private String genericArguments(final List<TypeInstanceDefinition> genericTypeArguments) {
        if (genericTypeArguments.isEmpty()) {
            return "";
        }
        final String body = genericTypeArguments.stream()
                .map(this::renderTypeInstance)
                .collect(Collectors.joining(", "))
        ;

        return "<" + body + ">";
    }

    private String getMapInstance(final TypeInstanceDefinition instanceDefinition) {
        if (!instanceDefinition.getGenericTypeArguments().isEmpty()) {
            final String collectionIndexType = getCollectionBaseType(instanceDefinition, 0);
            if ((!"number".equals(collectionIndexType)) && (!"string".equals(collectionIndexType))) {
                context.warning("Unable to use " + collectionIndexType + "as map index: TypeScript at this point prohibits maps of non indexable types");
                return "{}";
            }

            return "{ [index: " + collectionIndexType + "]: " + getCollectionBaseType(instanceDefinition, 1) + " }";
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
