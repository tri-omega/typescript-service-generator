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

import org.omega.typescript.api.TypeScriptEndpoint;
import org.omega.typescript.processor.model.Endpoint;
import org.omega.typescript.processor.services.ProcessingContext;
import org.omega.typescript.processor.utils.AnnotationUtils;
import org.omega.typescript.processor.utils.ResolvedAnnotationValues;
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

        endpoint.setContainer(context.getTypeOracle().buildContainer(type));

        final Optional<ResolvedAnnotationValues> endpointDefinition = AnnotationUtils.resolveAnnotation(TypeScriptEndpoint.class, type, context);
        endpointDefinition.ifPresent(annotation -> processMetadata(context, annotation, endpoint, type));


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

    private void processMetadata(final ProcessingContext context, final ResolvedAnnotationValues annotation, final Endpoint endpoint, final Element type) {
        final String userName = annotation.readString("name", "", context);
        if (StringUtils.hasText(userName)) {
            endpoint.setControllerName(userName);
        } else {
            endpoint.setControllerName(type.getSimpleName().toString());
        }

        final String moduleName = annotation.readString("moduleName", "", context);
        endpoint.setModuleName(moduleName);
    }

}
