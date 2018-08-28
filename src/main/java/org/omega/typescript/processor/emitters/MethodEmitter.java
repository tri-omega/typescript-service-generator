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

package org.omega.typescript.processor.emitters;

import org.omega.typescript.processor.model.EndpointMethod;
import org.omega.typescript.processor.model.MappingDefinition;
import org.omega.typescript.processor.model.MethodParameter;
import org.omega.typescript.processor.model.PathVariableDefinition;
import org.omega.typescript.processor.utils.StringUtils;

import java.io.PrintWriter;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by kibork on 5/8/2018.
 */
public class MethodEmitter {

    // ---------------- Fields & Constants --------------

    // ------------------ Properties --------------------

    private final EmitContext context;

    // ------------------ Logic      --------------------

    public MethodEmitter(final EmitContext context) {
        this.context = context;
    }

    public void renderMethod(final EndpointMethod method, final PrintWriter writer) {
        writer.printf(context.indent() + "public %s", method.getMethodName() + "(");

        writer.print(getParamDeclaration(method));

        writer.printf("): Observable<%s> {\n", context.getInstanceRenderer().renderTypeInstance(method.getReturnType()));
        writer.printf(context.indent(2) + "const mapping:HttpRequestMapping = ");
        renderMapping(writer, method.getMappingDefinition()).println(";");

        if (!method.getParams().isEmpty()) {
            writer.println(context.indent(2) + "const params: MethodParamMapping[] = [");
            writer.println(
                    method.getParams().stream()
                            .map(this::buildParamMapping)
                            .collect(Collectors.joining(",\n"))
            );
            writer.println(context.indent(2) + "];");
        } else {
            writer.println(context.indent(2) + "const params: MethodParamMapping[] = [];");
        }
        writer.printf(context.indent(2) + "return this.httpService.execute(this.defaultRequestMapping, mapping, params);\n");
        writer.println(context.indent() + "}\n");
    }

    private String buildParamMapping(MethodParameter param) {
        final StringBuilder paramData = new StringBuilder();
        paramData.append(String.format(context.indent(3) + "{paramName: '%s', isRequired: %s, ",
                StringUtils.escapeString(param.getName(), "'"), hasRequiredMarker(param)));
        apendOption("pathVariableName", param.getPathVariableName(), paramData);
        apendOption("requestParameterName", param.getRequestParameterName(), paramData);
        paramData.append(String.format(" isRequestBody: %s, value: %s}",
                param.getRequestBody().isPresent(), param.getName()
        ));
        return paramData.toString();
    }

    private boolean hasRequiredMarker(final MethodParameter param) {
        return Stream.of(param.getPathVariableName(), param.getRequestParameterName(), param.getRequestBody())
                .filter(Optional::isPresent)
                .map(Optional::get)
                .anyMatch(PathVariableDefinition::isRequired);
    }

    private void apendOption(final String variableName, final Optional<PathVariableDefinition> variableDefinition, final StringBuilder paramData) {
        variableDefinition.ifPresent(pathVariableDefinition ->
                paramData.append(String.format("%s: '%s', ", variableName, pathVariableDefinition.getName()))
        );
    }

    private String getParamDeclaration(EndpointMethod method) {
        return method.getParams().stream()
                    .map(param ->
                            String.format("%s: %s", param.getName(), context.getInstanceRenderer().renderTypeInstance(param.getType()))
                    ).collect(Collectors.joining(", "));
    }

    public PrintWriter renderMapping(final PrintWriter writer, final MappingDefinition mapping) {
        final Optional<MappingDefinition> mappingDefinitionOptional = Optional.ofNullable(mapping);
        writer.printf("{urlTemplate: '%s', method: %s}",
                StringUtils.escapeString(
                        mappingDefinitionOptional.map(MappingDefinition::getUrlTemplate).orElse(""), "'"),
                        mappingDefinitionOptional.map(MappingDefinition::getRequestMethod)
                            .map(v -> "RequestMethod." + v.name()).orElse("null")
        );
        return writer;
    }

}
