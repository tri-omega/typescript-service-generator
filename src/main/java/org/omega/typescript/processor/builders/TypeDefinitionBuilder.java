package org.omega.typescript.processor.builders;

import org.omega.typescript.api.TypeScriptName;
import org.omega.typescript.processor.ProcessingContext;
import org.omega.typescript.processor.model.TypeDefinition;
import org.omega.typescript.processor.model.TypeKind;
import org.omega.typescript.processor.utils.StringUtils;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import java.util.Optional;

/**
 * Created by kibork on 1/23/2018.
 */
public class TypeDefinitionBuilder {

    // ------------------ Constants  --------------------

    // ------------------ Fields     --------------------

    private final ProcessingContext context;

    // ------------------ Properties --------------------

    // ------------------ Logic      --------------------


    public TypeDefinitionBuilder(final ProcessingContext context) {
        this.context = context;
    }

    public TypeDefinition buildClassDefinition(final TypeElement type) {
        final String className = type.getQualifiedName().toString().intern();
        synchronized (className) {
            final Optional<TypeDefinition> optionalDefinition = context.getTypeOracle().getType(className);
            if (optionalDefinition.isPresent()) {
                return optionalDefinition.get();
            }

            final TypeDefinition typeDefinition = new TypeDefinition(type.getQualifiedName().toString(), type.getSimpleName().toString());
            context.getTypeOracle().addType(typeDefinition);
            initializeTypeDefinition(typeDefinition, type);
            return typeDefinition;
        }
    }

    private void initializeTypeDefinition(final TypeDefinition typeDefinition, final TypeElement typeElement) {
        typeDefinition.setTypeKind(fromElementKind(typeElement.getKind(), typeElement));
        typeDefinition.setTypeScriptName(getTypeScriptName(typeDefinition, typeElement));
    }

    private String getTypeScriptName(TypeDefinition typeDefinition, TypeElement element) {
        String targetName = typeDefinition.getShortName();
        final TypeScriptName nameAnnotation = element.getAnnotation(TypeScriptName.class);
        if ((nameAnnotation != null) && (StringUtils.hasText(nameAnnotation.value()))) {
            targetName = nameAnnotation.value();
        }
        return targetName;
    }

    private TypeKind fromElementKind(final ElementKind kind, final TypeElement element) {
        if (element.asType().getKind().isPrimitive()) {
            return TypeKind.PRIMITIVE;
        }
        switch (kind) {
            case INTERFACE:
            case CLASS: return TypeKind.INTERFACE;
            case ENUM: return TypeKind.ENUM;
        }
        return TypeKind.UNKNOWN;
    }


}
