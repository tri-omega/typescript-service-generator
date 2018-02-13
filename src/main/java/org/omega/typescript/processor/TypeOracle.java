package org.omega.typescript.processor;

import org.omega.typescript.processor.model.DefinitionId;
import org.omega.typescript.processor.model.TypeDefinition;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
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

    // ------------------ Properties --------------------

    // ------------------ Logic      --------------------

    public Optional<TypeDefinition> getType(final String qualifiedName) {
        return Optional.of(types.get(qualifiedName));
    }

    public Optional<TypeDefinition> getType(final TypeElement typeElement) {
        final String className = typeElement.getQualifiedName().toString();
        return getType(className);
    }

    public TypeDefinition create(final TypeElement type, final ProcessingContext context) {
        return new TypeDefinition(new DefinitionId(type,context));
    }

    public void clear() {
        types.clear();
    }
}
