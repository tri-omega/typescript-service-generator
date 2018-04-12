package org.omega.typescript.processor.builders;

import org.omega.typescript.processor.ProcessingContext;
import org.omega.typescript.processor.model.TypeDefinition;
import org.omega.typescript.processor.model.TypeInstanceDefinition;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.WildcardType;
import java.util.List;

/**
 * Created by kibork on 4/9/2018.
 */
public class TypeInstanceBuilder {

    // ------------------ Constants  --------------------

    // ------------------ Fields     --------------------

    private final ProcessingContext context;

    // ------------------ Properties --------------------

    // ------------------ Logic      --------------------

    public TypeInstanceBuilder(ProcessingContext context) {
        this.context = context;
    }

    public TypeInstanceDefinition buildDefinition(final TypeElement typeElement) {
        return new TypeInstanceDefinition(context.getTypeOracle().getOrDefineType(typeElement));
    }

    public TypeInstanceDefinition buildDefinition(final TypeMirror typeMirror) {
        if (typeMirror.getKind() == TypeKind.TYPEVAR) {
            final TypeParameterElement element = (TypeParameterElement)context.getProcessingEnv().getTypeUtils().asElement(typeMirror);
            final String typeParamName = element.getSimpleName().toString();
            final Element parentElement = element.getGenericElement();
            if (parentElement instanceof TypeElement) {
                final TypeDefinition parentType = context.getTypeOracle().getOrDefineType((TypeElement) parentElement);
                final TypeDefinition typeDefinition = parentType.getGenericTypeParams().stream()
                        .filter(f -> f.getShortName().equals(typeParamName))
                        .findAny().orElseThrow(() -> new IllegalStateException("Unknown type template with name " + typeParamName + " in " + parentType));
                final TypeInstanceDefinition definition = new TypeInstanceDefinition(typeDefinition);
                checkTypeParameters(definition, typeMirror);
                return definition;
            } else {
                throw new UnsupportedOperationException("Unable to get type references to generics of " + parentElement.getClass());
            }
        } else if (typeMirror.getKind() == TypeKind.WILDCARD) {
            return createWildcardInstance((WildcardType) typeMirror);
        } else {
            //If this is a simple case
            final TypeInstanceDefinition typeInstance = new TypeInstanceDefinition(context.getTypeOracle().getOrDefineType(typeMirror));
            if (typeInstance.getTypeKind() == org.omega.typescript.processor.model.TypeKind.INTERFACE) {
                //final TypeElement typeElement = (TypeElement)context.getProcessingEnv().getTypeUtils().asElement(typeMirror);
                checkTypeParameters(typeInstance, typeMirror);
            }
            return typeInstance;
        }
    }

    private TypeInstanceDefinition createWildcardInstance(WildcardType wildcardType) {
        //With TypeScript duct typing the extends semantics are naturally available from the base class
        if (wildcardType.getExtendsBound() != null) {
            return buildDefinition(wildcardType.getExtendsBound());
        } else {
            return new TypeInstanceDefinition(context.getTypeOracle().getType(Object.class.getName()).orElse(null));
        }
        //There is no semantics in typescript that could represent a super bound
//            else if (wildcardType.getSuperBound() != null) {
//                typeInstance.getTypeDefinition().getSuperTypes().add(buildDefinition(wildcardType.getSuperBound()));
//            }
    }

    private void checkTypeParameters(final TypeInstanceDefinition typeInstance, final TypeMirror typeMirror) {
//        final TypeMirror captureMirror = context.getProcessingEnv().getTypeUtils().capture(typeMirror);
        if (typeMirror instanceof DeclaredType) {
            final DeclaredType declaredType = (DeclaredType)typeMirror;
            final List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();
            for (final TypeMirror typeArgument : typeArguments) {
                typeInstance.getGenericTypeArguments().add(buildDefinition(typeArgument));
            }
        }
    }

}
