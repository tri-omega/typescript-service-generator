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

package org.omega.typescript.processor.model;

import org.omega.typescript.processor.PredefinedTypes;
import org.omega.typescript.processor.builders.TypeContainerBuilder;
import org.omega.typescript.processor.builders.TypeDefinitionBuilder;
import org.omega.typescript.processor.builders.TypeInstanceBuilder;
import org.omega.typescript.processor.services.ProcessingContext;
import org.omega.typescript.processor.utils.TypeUtils;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by kibork on 1/22/2018.
 */
public class TypeOracle {

    // ------------------ Constants  --------------------

    // ------------------ Fields     --------------------

    private final Map<String, TypeDefinition> types = new ConcurrentHashMap<>();

    private TypeDefinitionBuilder typeDefinitionBuilder;

    private TypeInstanceBuilder typeInstanceBuilder;

    private TypeContainerBuilder typeContainerBuilder;

    private ProcessingContext context;

    private TypeMirror collectionType;

    private TypeMirror mapType;

    private TypeElement mapElement;

    private boolean initializedPredefinedTypes = false;

    // ------------------ Properties --------------------

    public Collection<TypeDefinition> getKnownTypes() {
        return types.values();
    }

    public ProcessingContext getContext() {
        return context;
    }

    // ------------------ Logic      --------------------


    public TypeOracle() {
    }

    public void initContext(final ProcessingContext context) {
        this.context = context;
        this.typeDefinitionBuilder = new TypeDefinitionBuilder(context);
        this.typeInstanceBuilder = new TypeInstanceBuilder(context);
        this.typeContainerBuilder = new TypeContainerBuilder(context);

        if (!initializedPredefinedTypes) {
            PredefinedTypes.registerTypes(this);
            initializedPredefinedTypes = true;
        }

        this.collectionType = context.getProcessingEnv().getTypeUtils().erasure(
                context.getProcessingEnv().getElementUtils().getTypeElement(Collection.class.getName())
                .asType()
        );

        mapElement = context.getProcessingEnv().getElementUtils().getTypeElement(Map.class.getName());
        this.mapType = context.getProcessingEnv().getTypeUtils().erasure(
                mapElement.asType()
        );
    }

    public Optional<TypeDefinition> getType(final String qualifiedName) {
        final boolean isExcluded =
                context.getGenConfig().getExcludedClasses()
                        .values().stream()
                        .anyMatch(p -> p.matcher(qualifiedName).matches());
        if (isExcluded) {
            return Optional.of(getAny());
        }
        return Optional.ofNullable(types.get(qualifiedName));
    }

    public Optional<TypeDefinition> getType(final TypeElement typeElement) {
        final String className = TypeUtils.getClassName(typeElement);
        return getType(className);
    }

    public TypeInstanceDefinition buildInstance(final TypeMirror typeMirror) {
        return typeInstanceBuilder.buildDefinition(typeMirror);
    }

    public void clear() {
        types.clear();
        initializedPredefinedTypes = false;
    }

    public void addType(final TypeDefinition typeDefinition) {
        final String className = typeDefinition.getFullName();
        types.putIfAbsent(className, typeDefinition);
    }

    public TypeDefinition getAny() {
        return context.getTypeOracle().getType(Object.class.getName()).orElse(null);
    }

    public TypeInstanceDefinition getAnyInstance() {
        return typeInstanceBuilder.buildAny();
    }

    public TypeDefinition getOrDefineType(final TypeMirror typeMirror) {
        final String className = TypeUtils.getClassName(typeMirror, context);
        final Optional<TypeDefinition> definition = getType(className);
        return definition.orElseGet(() -> getOrDefineType((TypeElement) context.getProcessingEnv().getTypeUtils().asElement(typeMirror)));
    }

    public TypeDefinition getOrDefineType(final TypeElement typeElement) {
        final Optional<TypeDefinition> typeDefinition = getType(typeElement);
        return typeDefinition
                .orElseGet(() -> buildNewType(typeElement));
    }

    private TypeDefinition buildNewType(final TypeElement typeElement) {
        final Types types = context.getProcessingEnv().getTypeUtils();
        final TypeMirror erasedType = types.erasure(typeElement.asType());

        //Place to be extended to support user level type overrides
        if (types.isAssignable(erasedType, collectionType)) {
            final Optional<TypeDefinition> arrayType = getType(TypeUtils.ARRAY_TYPE_NAME);
            if (arrayType.isPresent()) {
                return arrayType.get();
            }
        } else if (types.isAssignable(erasedType, mapType)) {
            final Optional<TypeDefinition> arrayType = getType(mapElement);
            if (arrayType.isPresent()) {
                return arrayType.get();
            }
        }

        return typeDefinitionBuilder.buildClassDefinition(typeElement);
    }

    public TypeContainer buildContainer(final Element element) {
        return typeContainerBuilder.buildContainer(element);
    }

}
