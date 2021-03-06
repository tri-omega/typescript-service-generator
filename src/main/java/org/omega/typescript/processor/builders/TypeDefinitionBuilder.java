/*
 * Copyright (c) 2018 William Frank (info@williamfrank.net)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.omega.typescript.processor.builders;

import org.omega.typescript.api.TypeScriptName;
import org.omega.typescript.processor.builders.properties.PropertyDefinitionBuilder;
import org.omega.typescript.processor.model.EnumConstant;
import org.omega.typescript.processor.model.TypeDefinition;
import org.omega.typescript.processor.model.TypeInstanceDefinition;
import org.omega.typescript.processor.model.TypeKind;
import org.omega.typescript.processor.services.ProcessingContext;
import org.omega.typescript.processor.utils.StringUtils;
import org.omega.typescript.processor.utils.TypeUtils;

import javax.lang.model.element.*;
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
        typeDefinition.setContainer(context.getTypeOracle().buildContainer(typeElement));
    }

    private void initializeEnum(TypeDefinition typeDefinition, TypeElement typeElement) {
        final List<Element> members = TypeUtils.getMembers(typeElement, ElementKind.ENUM_CONSTANT, context);
        typeDefinition.getEnumConstants().addAll(members.stream()
                .map(e -> new EnumConstant(e.getSimpleName().toString()))
                .collect(Collectors.toList())
        );
    }

    private void initializeInterface(TypeDefinition typeDefinition, TypeElement typeElement) {
        initializeTypeParams(typeDefinition, typeElement);

        typeDefinition.getProperties()
                .addAll(
                        propertyDefinitionBuilder.buildProperties(typeElement)
                );

        readSuperclass(typeDefinition, typeElement);

        final List<? extends TypeMirror> interfaces = typeElement.getInterfaces();
        for (final TypeMirror interfaceMirror : interfaces) {
            final QualifiedNameable interfaceElement = (TypeElement) context.getProcessingEnv().getTypeUtils().asElement(interfaceMirror);
            if ((interfaceElement == null) || (!Object.class.getName().equals(interfaceElement.getQualifiedName().toString()))) {
                final TypeInstanceDefinition instance = context.getTypeOracle().buildInstance(interfaceMirror);
                if (!Object.class.getName().equals(instance.getTypeDefinition().getFullName())) {
                    typeDefinition.getSuperTypes().add(instance);
                }
            }
        }
    }

    private void initializeTypeParams(TypeDefinition typeDefinition, TypeElement typeElement) {
        if (!typeElement.getTypeParameters().isEmpty()) {
            typeElement.getTypeParameters().forEach(t ->
                    typeDefinition.getGenericTypeParams().add(buildGenericType(t, typeDefinition))
            );
        }
    }

    private TypeDefinition buildGenericType(final TypeParameterElement typeElement, final TypeDefinition typeDefinition) {
        final String genericName = typeElement.getSimpleName().toString();
        final TypeDefinition newGenericType = new TypeDefinition(TypeUtils.getGenericTypeName(typeDefinition, genericName), genericName);
        newGenericType.setTypeScriptName(genericName);
        newGenericType.setTypeKind(TypeKind.GENERIC_PLACEHOLDER);
        typeElement.getBounds().forEach(t -> {
            //Adding each of the bounds as an super interface to the type
            newGenericType.getSuperTypes().add(context.getTypeOracle().buildInstance(t));
        });
        return newGenericType;
    }

    private void readSuperclass(TypeDefinition typeDefinition, TypeElement typeElement) {
        final TypeMirror superclassMirror = typeElement.getSuperclass();
        if (superclassMirror != null) {
            final QualifiedNameable superClass = (TypeElement) context.getProcessingEnv().getTypeUtils().asElement(superclassMirror);
            if ((superClass != null) && (!Object.class.getName().equals(superClass.getQualifiedName().toString()))) {
                final TypeInstanceDefinition superClassDefinition = context.getTypeOracle().buildInstance(superclassMirror);
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
