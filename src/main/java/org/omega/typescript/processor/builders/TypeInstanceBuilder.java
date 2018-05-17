package org.omega.typescript.processor.builders;

import org.omega.typescript.processor.model.TypeDefinition;
import org.omega.typescript.processor.model.TypeInstanceDefinition;
import org.omega.typescript.processor.services.ProcessingContext;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.*;
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
            return createFromGenericTypeParameter(typeMirror);
        } else if (typeMirror.getKind() == TypeKind.WILDCARD) {
            return createWildcardInstance((WildcardType) typeMirror);
        } else if (typeMirror.getKind() == TypeKind.ARRAY) {
            return createArray((ArrayType)typeMirror);
        } else {
            //If this is a simple case
            final TypeInstanceDefinition typeInstance = new TypeInstanceDefinition(context.getTypeOracle().getOrDefineType(typeMirror));
            if ((typeInstance.getTypeKind().hasTypeParams())) {
                checkTypeParameters(typeInstance, typeMirror);
            }
            return typeInstance;
        }
    }

    private TypeInstanceDefinition createArray(final ArrayType type) {
        final TypeInstanceDefinition typeInstance = new TypeInstanceDefinition(context.getTypeOracle().getOrDefineType(type));
        typeInstance.getGenericTypeArguments().add(buildDefinition(type.getComponentType()));
        return typeInstance;
    }

    private TypeInstanceDefinition createFromGenericTypeParameter(TypeMirror typeMirror) {
        final TypeParameterElement element = (TypeParameterElement)context.getProcessingEnv().getTypeUtils().asElement(typeMirror);
        final String typeParamName = element.getSimpleName().toString();
        final Element parentElement = element.getGenericElement();
        if (parentElement instanceof TypeElement) {
            //Get the class that contains the generic type definition from the Oracle
            final TypeDefinition parentType = context.getTypeOracle().getOrDefineType((TypeElement) parentElement);
            final TypeDefinition typeDefinition = parentType.getGenericTypeParams().stream()
                    .filter(f -> f.getShortName().equals(typeParamName))
                    .findAny().orElseThrow(() -> new IllegalStateException("Unknown type template with name " + typeParamName + " in " + parentType));
            final TypeInstanceDefinition definition = new TypeInstanceDefinition(typeDefinition);
            checkTypeParameters(definition, typeMirror);
            return definition;
        } else if (parentElement instanceof ExecutableElement) {
            //For methods: we drop the generic type to either the bound if there's a single one or to 'any' otherwise
            //It is possible to synthesise an interface for the return type if there are multiple bounds
            //but it gets complicated when there are multiple template types within the same method aka:
            // public <T1, T2, T3> T1<T2, List<T3>> getField();
            final ExecutableElement executableElement = (ExecutableElement)parentElement;
            if (element.getBounds().size() == 1) {
                return buildDefinition(element.getBounds().get(0));
            } else {
                return buildAny();
            }
        } else {
            throw new UnsupportedOperationException("Unable to get type references to generics of " + parentElement.getClass());
        }
    }

    private TypeInstanceDefinition createWildcardInstance(WildcardType wildcardType) {
        //With TypeScript duct typing the extends semantics are naturally available from the base class
        if (wildcardType.getExtendsBound() != null) {
            return buildDefinition(wildcardType.getExtendsBound());
        } else {
            return buildAny();
        }
        //There is no semantics in typescript that could represent a super bound
//            else if (wildcardType.getSuperBound() != null) {
//                typeInstance.getTypeDefinition().getSuperTypes().add(buildDefinition(wildcardType.getSuperBound()));
//            }
    }

    public TypeInstanceDefinition buildAny() {
        return new TypeInstanceDefinition(context.getTypeOracle().getAny());
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
