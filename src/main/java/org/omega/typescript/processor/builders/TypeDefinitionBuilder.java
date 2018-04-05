package org.omega.typescript.processor.builders;

import org.omega.typescript.api.TypeScriptName;
import org.omega.typescript.processor.ProcessingContext;
import org.omega.typescript.processor.model.EnumConstant;
import org.omega.typescript.processor.model.TypeDefinition;
import org.omega.typescript.processor.model.TypeKind;
import org.omega.typescript.processor.utils.StringUtils;
import org.omega.typescript.processor.utils.TypeUtils;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by kibork on 1/23/2018.
 */
public class TypeDefinitionBuilder {

    // ------------------ Constants  --------------------

    // ------------------ Fields     --------------------

    private final ProcessingContext context;

    private final PropertyDefinitionBuilder propertyDefinitionBuilder;

    // ------------------ Properties --------------------

    // ------------------ Logic      --------------------


    public TypeDefinitionBuilder(final ProcessingContext context) {
        this.context = context;
        this.propertyDefinitionBuilder = new PropertyDefinitionBuilder(context);
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

        if (typeDefinition.getTypeKind() == TypeKind.INTERFACE) {
            initializeInterface(typeDefinition, typeElement);
        } else if (typeDefinition.getTypeKind() == TypeKind.ENUM) {
            initializeEnum(typeDefinition, typeElement);
        }
    }

    private void initializeEnum(TypeDefinition typeDefinition, TypeElement typeElement) {
        final List<Element> members = TypeUtils.getMembers(typeElement, ElementKind.ENUM_CONSTANT, context);
        typeDefinition.getEnumConstants().addAll(members.stream()
                .map(e -> new EnumConstant(e.getSimpleName().toString()))
                .collect(Collectors.toList())
        );
    }

    private void initializeInterface(TypeDefinition typeDefinition, TypeElement typeElement) {
        typeDefinition.getProperties()
                .addAll(
                        propertyDefinitionBuilder.buildPropertyGetters(typeElement)
                );

        readSuperclass(typeDefinition, typeElement);

        final List<? extends TypeMirror> interfaces = typeElement.getInterfaces();
        for (final TypeMirror interfaceMirror : interfaces) {
            final TypeElement interfaceElement = (TypeElement) context.getProcessingEnv().getTypeUtils().asElement(interfaceMirror);
            if (!Object.class.getName().equals(interfaceElement.getQualifiedName().toString())) {
                final TypeDefinition interfaceDefinition = context.getTypeOracle().getOrDefineType(interfaceElement);
                typeDefinition.getSuperTypes().add(interfaceDefinition);
            }
        }
    }

    private void readSuperclass(TypeDefinition typeDefinition, TypeElement typeElement) {
        final TypeMirror superclassMirror = typeElement.getSuperclass();
        if (superclassMirror != null) {
            final TypeElement superClass = (TypeElement) context.getProcessingEnv().getTypeUtils().asElement(superclassMirror);
            if ((superClass != null) && (!Object.class.getName().equals(superClass.getQualifiedName().toString()))) {
                final TypeDefinition superClassDefinition = context.getTypeOracle().getOrDefineType(superClass);
                typeDefinition.getSuperTypes().add(superClassDefinition);
            }
        }
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
