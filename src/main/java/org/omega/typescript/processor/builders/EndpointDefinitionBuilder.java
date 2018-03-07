package org.omega.typescript.processor.builders;

import org.omega.typescript.api.TypeScriptEndpoint;
import org.omega.typescript.processor.ProcessingContext;
import org.omega.typescript.processor.model.Endpoint;
import org.omega.typescript.processor.utils.StringUtils;
import org.omega.typescript.processor.utils.TypeUtils;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by kibork on 1/22/2018.
 */
public class EndpointDefinitionBuilder {

    // ------------------ Constants  --------------------

    // ------------------ Fields     --------------------

    private final ProcessingContext context;

    private final MappingDefinitionBuilder mappingDefinitionBuilder;

    private final MethodDefinitionBuilder methodDefinitionBuilder;

    // ------------------ Properties --------------------

    // ------------------ Logic      --------------------

    public EndpointDefinitionBuilder(final ProcessingContext context) {
        this.context = context;
        this.mappingDefinitionBuilder = new MappingDefinitionBuilder(context);
        this.methodDefinitionBuilder = new MethodDefinitionBuilder(context, mappingDefinitionBuilder);
    }

    public Endpoint buildEndpoint(final TypeElement type) {
        final String controllerClassName = type.getQualifiedName().toString();
        final Endpoint endpoint = new Endpoint(controllerClassName);

        endpoint.setMappingDefinition(mappingDefinitionBuilder.build(type));

        Optional.ofNullable(type.getAnnotation(TypeScriptEndpoint.class))
                .ifPresent(annotation -> processMetadata(context, annotation, endpoint, type));

        readMethodDefinitions(type, endpoint);

        return endpoint;
    }

    private void readMethodDefinitions(TypeElement type, Endpoint endpoint) {
        final List<ExecutableElement> methods = TypeUtils.getMethods(type, context);
        endpoint.setEndpointMethods(methods.stream()
                .map(m -> methodDefinitionBuilder.build(endpoint, m))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList())
        );
    }

    private void processMetadata(final ProcessingContext context, final TypeScriptEndpoint annotation, final Endpoint endpoint, final Element type) {
        final String userName = annotation.name();
        if (StringUtils.hasText(userName)) {
            endpoint.setControllerName(userName);
        } else {
            endpoint.setControllerName(type.getSimpleName().toString());
        }
    }

}
