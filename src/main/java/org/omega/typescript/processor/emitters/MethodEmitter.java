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
        writer.printf("\tpublic %s", method.getMethodName() + "(");

        writer.print(getParamDeclaration(method));

        writer.printf("): Observable<%s> {\n", context.getInstanceRenderer().renderTypeInstance(method.getReturnType()));
        writer.printf("\t\tconst mapping:HttpRequestMapping = ");
        renderMapping(writer, method.getMappingDefinition()).println(";");

        if (!method.getParams().isEmpty()) {
            writer.println("\t\tconst params: MethodParamMapping[] = [");
            writer.println(
                    method.getParams().stream()
                            .map(this::buildParamMapping)
                            .collect(Collectors.joining(",\n"))
            );
            writer.println("\t\t];");
        } else {
            writer.println("\t\tconst params: MethodParamMapping[] = [];");
        }
        writer.printf("\t\treturn httpService.execute(this.defaultRequestMapping, mapping, params);\n");
        writer.println("\t}\n");
    }

    private String buildParamMapping(MethodParameter param) {
        final StringBuilder paramData = new StringBuilder();
        paramData.append(String.format("\t\t\t{paramName: '%s', isRequired: %s, ",
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
        writer.printf("{urlTemplate: '%s', method: RequestMethod.%s}",
                StringUtils.escapeString(
                        mappingDefinitionOptional.map(MappingDefinition::getUrlTemplate).orElse(""), "'"),
                        mappingDefinitionOptional.map(MappingDefinition::getRequestMethod)
                            .map(Enum::name).orElse("null")
        );
        return writer;
    }

}
