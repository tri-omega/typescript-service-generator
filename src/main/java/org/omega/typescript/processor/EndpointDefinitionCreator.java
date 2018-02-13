package org.omega.typescript.processor;

import org.omega.typescript.processor.def.RequestMappingAnnotation;
import org.omega.typescript.processor.model.Endpoint;
import org.omega.typescript.processor.model.RequestMethod;
import org.omega.typescript.processor.utils.TypeUtils;

import javax.lang.model.element.*;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by kibork on 1/22/2018.
 */
public class EndpointDefinitionCreator {

    // ------------------ Constants  --------------------

    // ------------------ Fields     --------------------

    // ------------------ Properties --------------------

    // ------------------ Logic      --------------------

    public static Endpoint buildEndpoint(final String controllerClass, final TypeElement type, final ProcessingContext context) {
        final Endpoint endpoint = new Endpoint(controllerClass);
        final Optional<? extends AnnotationMirror> requestMapping =
                RequestMappingAnnotation.getInstance().get(type, context);
        
        requestMapping.ifPresent(annotationMirror -> readRequestMapping(controllerClass, context, endpoint, annotationMirror));

        final List<ExecutableElement> methods = TypeUtils.getMembers(type, ElementKind.METHOD, context).stream()
                .map(m -> (ExecutableElement) m)
                .collect(Collectors.toList());

        return endpoint;
    }

    static void readRequestMapping(String controllerClass, ProcessingContext context, Endpoint endpoint, AnnotationMirror containerDefaults) {
        mapRequestPath(controllerClass, context, endpoint, containerDefaults);
        mapHttpMethod(controllerClass, context, endpoint, containerDefaults);
    }

    static void mapHttpMethod(String controllerClass, ProcessingContext context, Endpoint endpoint, AnnotationMirror containerDefaults) {
        final Optional<RequestMethod[]> method = RequestMappingAnnotation.getInstance().method(containerDefaults, context);

        if (method.isPresent()) {
            final RequestMethod[] requestMethods = method.get();
            if (requestMethods.length > 0) {
                if (requestMethods.length > 1) {
                    context.warning("Multiple htp method types are mapped for endpoint " + controllerClass + ", using the first one: " + requestMethods[0]);
                }
                endpoint.setDefaultHttpMethod(requestMethods[0]);
            }
        }
    }

    static void mapRequestPath(String controllerClass, ProcessingContext context, Endpoint endpoint, AnnotationMirror containerDefaults) {
        final Optional<String[]> pathOption = RequestMappingAnnotation.getInstance().path(containerDefaults, context);
        if (pathOption.isPresent()) {
            final String[] paths = pathOption.get();
            if (paths.length > 0) {
                if (paths.length > 1) {
                    context.warning("Multiple paths are mapped for endpoint " + controllerClass + ", using the first one: " + paths[0]);
                }
                endpoint.setBaseUrl(paths[0]);
            }
        }
    }


}
