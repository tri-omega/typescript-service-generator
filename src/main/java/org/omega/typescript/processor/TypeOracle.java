package org.omega.typescript.processor;

import org.omega.typescript.processor.builders.TypeDefinitionBuilder;
import org.omega.typescript.processor.builders.TypeInstanceBuilder;
import org.omega.typescript.processor.model.TypeDefinition;
import org.omega.typescript.processor.model.TypeInstanceDefinition;
import org.omega.typescript.processor.utils.TypeUtils;

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

    private ProcessingContext context;

    private TypeMirror collectionType;

    private TypeMirror mapType;
    private TypeElement mapElement;

    // ------------------ Properties --------------------

    // ------------------ Logic      --------------------


    public TypeOracle() {
        PredefinedTypes.registerTypes(this);
    }

    public void initContext(final ProcessingContext context) {
        this.context = context;
        this.typeDefinitionBuilder = new TypeDefinitionBuilder(context);
        this.typeInstanceBuilder = new TypeInstanceBuilder(context);

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
        return Optional.ofNullable(types.get(qualifiedName));
    }

    public Optional<TypeDefinition> getType(final TypeElement typeElement) {
        final String className = TypeUtils.getClassName(typeElement);
        return getType(className);
    }

    /**
     * Correct usages are not really known, let's keep for now and see if we need it
     * @param typeElement type element to build instance form
     * @return new type instance based on the element
     */
    @Deprecated
    public TypeInstanceDefinition buildInstance(final TypeElement typeElement) {
        return typeInstanceBuilder.buildDefinition(typeElement);
    }

    public TypeInstanceDefinition buildInstance(final TypeMirror typeMirror) {
        return typeInstanceBuilder.buildDefinition(typeMirror);
    }

    public void clear() {
        types.clear();
        PredefinedTypes.registerTypes(this);
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

}
