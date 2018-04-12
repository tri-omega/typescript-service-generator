package org.omega.typescript.processor;

import org.omega.typescript.processor.builders.TypeDefinitionBuilder;
import org.omega.typescript.processor.builders.TypeInstanceBuilder;
import org.omega.typescript.processor.model.TypeDefinition;
import org.omega.typescript.processor.model.TypeInstanceDefinition;
import org.omega.typescript.processor.utils.TypeUtils;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
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

    private ProcessingContext context;

    // ------------------ Properties --------------------

    // ------------------ Logic      --------------------


    public TypeOracle() {
        PredefinedTypes.registerTypes(this);
    }

    public void initContext(final ProcessingContext context) {
        this.context = context;
        this.typeDefinitionBuilder = new TypeDefinitionBuilder(context);
        this.typeInstanceBuilder = new TypeInstanceBuilder(context);
    }

    public Optional<TypeDefinition> getType(final String qualifiedName) {
        return Optional.ofNullable(types.get(qualifiedName));
    }

    public Optional<TypeDefinition> getType(final TypeElement typeElement) {
        final String className = TypeUtils.getClassName(typeElement);
        return getType(className);
    }

    public TypeInstanceDefinition buildInstance(final TypeElement typeElement) {
        return typeInstanceBuilder.buildDefinition(typeElement);
    }

    public TypeInstanceDefinition buildInstance(final TypeMirror typeMirror) {
        return typeInstanceBuilder.buildDefinition(typeMirror);
    }

    public TypeDefinition getOrDefineType(final TypeElement typeElement) {
        final Optional<TypeDefinition> typeDefinition = getType(typeElement);
        return typeDefinition
                .orElseGet(() -> typeDefinitionBuilder.buildClassDefinition(typeElement));
    }

    public TypeDefinition getOrDefineType(final TypeMirror typeMirror) {
        final String className = TypeUtils.getClassName(typeMirror, context);
        final Optional<TypeDefinition> definition = getType(className);
        return definition.orElseGet(() -> getOrDefineType((TypeElement) context.getProcessingEnv().getTypeUtils().asElement(typeMirror)));
    }

    public void clear() {
        types.clear();
        PredefinedTypes.registerTypes(this);
    }

    public void addType(final TypeDefinition typeDefinition) {
        final String className = typeDefinition.getFullName();
        types.putIfAbsent(className, typeDefinition);
    }
}
