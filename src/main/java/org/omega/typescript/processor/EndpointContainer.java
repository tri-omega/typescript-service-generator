package org.omega.typescript.processor;

import org.omega.typescript.processor.model.Endpoint;

import javax.lang.model.element.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by kibork on 2/2/2018.
 */
public class EndpointContainer {

    // ------------------ Constants  --------------------

    // ------------------ Fields     --------------------

    private final Map<String, Endpoint> endpointMap = new ConcurrentHashMap<>();

    // ------------------ Properties --------------------

    public Map<String, Endpoint> getEndpointMap() {
        return Collections.unmodifiableMap(endpointMap);
    }


    // ------------------ Logic      --------------------

    public Optional<Endpoint> getEndpoint(final String controllerClassName) {
        return Optional.ofNullable(endpointMap.get(controllerClassName.intern()));
    }

    public boolean hasEndpoint(final String controllerClassName) {
        return endpointMap.containsKey(controllerClassName);
    }

    public Endpoint buildEndpoint(final TypeElement type, final ProcessingContext context) {
        final String controllerClassName = type.getQualifiedName().toString().intern();
        return endpointMap.computeIfAbsent(controllerClassName, (className) -> EndpointDefinitionCreator.buildEndpoint(className, type, context));
    }


    public void clear() {
        endpointMap.clear();
    }
}
