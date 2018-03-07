package org.omega.typescript.processor.builders;

import org.omega.typescript.api.TypeScriptEndpoint;
import org.omega.typescript.processor.ProcessingContext;
import org.omega.typescript.processor.model.Endpoint;
import org.omega.typescript.processor.model.RequestMethod;
import org.omega.typescript.processor.utils.StringUtils;
import org.omega.typescript.processor.utils.TypeUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.lang.model.element.*;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Created by kibork on 1/22/2018.
 */
public class EndpointDefinitionBuilder {

    // ------------------ Constants  --------------------

    // ------------------ Fields     --------------------

    private final ProcessingContext context;

    private final MappingDefinitionBuilder mappingDefinitionBuilder;

    // ------------------ Properties --------------------

    // ------------------ Logic      --------------------

    public EndpointDefinitionBuilder(final ProcessingContext context) {
        this.context = context;
        this.mappingDefinitionBuilder = new MappingDefinitionBuilder(context);
    }

    public Endpoint buildEndpoint(final TypeElement type) {
        final String controllerClassName = type.getQualifiedName().toString();
        final Endpoint endpoint = new Endpoint(controllerClassName);

        endpoint.setMappingDefinition(mappingDefinitionBuilder.build(type));

        Optional.ofNullable(type.getAnnotation(TypeScriptEndpoint.class))
                .ifPresent(annotation -> processMetadata(context, annotation, endpoint, type));

        final List<ExecutableElement> propertyGetters = TypeUtils.getPropertyGetters(type, context);

        return endpoint;
    }

    private void processMetadata(final ProcessingContext context, final TypeScriptEndpoint annotation, final Endpoint endpoint, final Element type) {
        final String userName = annotation.name();
        if (StringUtils.hasText(userName)) {
            endpoint.setControllerName(userName);
        } else {
            endpoint.setControllerName(type.getSimpleName().toString());
        }
    }

    private void processRequestMapping(final String controllerClass, final ProcessingContext context, final Endpoint endpoint, final RequestMapping containerDefaults) {
        final String[] paths = containerDefaults.path().length > 0 ? containerDefaults.path() : containerDefaults.value();
        if (paths.length > 0) {
            if (paths.length > 1) {
                context.warning("Multiple paths are mapped for endpoint " + controllerClass + ", using the first one: " + paths[0]);
            }
            endpoint.getMappingDefinition().setUrlTemplate(paths[0]);
        }

        final RequestMethod[] methods = Arrays.stream(containerDefaults.method())
                .map(m -> RequestMethod.valueOf(m.name()))
                .toArray(RequestMethod[]::new);

        if (methods.length > 0) {
            if (methods.length > 1) {
                context.warning("Multiple htp method types are mapped for endpoint " + controllerClass + ", using the first one: " + methods[0]);
            }
            endpoint.getMappingDefinition().setRequestMethod(methods[0]);
        }
    }

}
