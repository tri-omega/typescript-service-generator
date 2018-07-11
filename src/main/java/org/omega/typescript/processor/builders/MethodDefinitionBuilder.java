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
import org.omega.typescript.processor.model.Endpoint;
import org.omega.typescript.processor.model.EndpointMethod;
import org.omega.typescript.processor.model.MappingDefinition;
import org.omega.typescript.processor.services.ProcessingContext;
import org.omega.typescript.processor.utils.StringUtils;

import javax.lang.model.element.ExecutableElement;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by kibork on 3/6/2018.
 */
public class MethodDefinitionBuilder {

    // ------------------ Constants  --------------------

    // ------------------ Fields     --------------------

    private final ProcessingContext context;

    private final MappingDefinitionBuilder mappingDefinitionBuilder;

    private final MethodParameterBuilder methodParameterBuilder;

    // ------------------ Properties --------------------

    // ------------------ Logic      --------------------

    public MethodDefinitionBuilder(final ProcessingContext context, final MappingDefinitionBuilder mappingDefinitionBuilder) {
        this.context = context;
        this.mappingDefinitionBuilder = mappingDefinitionBuilder;
        this.methodParameterBuilder = new MethodParameterBuilder(context);
    }

    public Optional<EndpointMethod> build(final Endpoint endpoint, final ExecutableElement methodElement) {
        final Optional<MappingDefinition> mappingDefinitionOption = mappingDefinitionBuilder.build(methodElement);

        if (!mappingDefinitionOption.isPresent()) {
            return Optional.empty();
        }
        final String methodName = getMethodName(methodElement);

        final EndpointMethod method = new EndpointMethod(endpoint, methodName, mappingDefinitionOption.get());

        defaultRequestMethod(endpoint, method);

        method.setParams(methodElement.getParameters().stream()
                .map(p -> methodParameterBuilder.builder(method, p))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList())
        );

        method.setReturnType(context.getTypeOracle().buildInstance(methodElement.getReturnType()));

        return Optional.of(method);
    }

    private String getMethodName(final ExecutableElement methodElement) {
        final TypeScriptName annotation = methodElement.getAnnotation(TypeScriptName.class);
        if ((annotation != null) && (StringUtils.hasText(annotation.value()))) {
            return annotation.value();
        }
        return methodElement.getSimpleName().toString();
    }

    private void defaultRequestMethod(Endpoint endpoint, EndpointMethod method) {
        if (endpoint.getMappingDefinition().isPresent()) {
            if (method.getMappingDefinition().getRequestMethod() == null) {
                method.getMappingDefinition().setRequestMethod(endpoint.getMappingDefinition().get().getRequestMethod());
            }
        }
    }
}
