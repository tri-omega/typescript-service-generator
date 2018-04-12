package org.omega.typescript.processor.builders;

import org.omega.typescript.processor.ProcessingContext;
import org.omega.typescript.processor.model.EndpointMethod;
import org.omega.typescript.processor.model.MethodParameter;
import org.omega.typescript.processor.model.PathVariableDefinition;
import org.omega.typescript.processor.utils.AnnotationUtils;
import org.omega.typescript.processor.utils.ResolvedAnnotationValues;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.lang.model.element.VariableElement;
import java.util.Optional;

/**
 * Created by kibork on 3/7/2018.
 */
public class MethodParameterBuilder {

    // ------------------ Constants  --------------------

    // ------------------ Fields     --------------------

    private final ProcessingContext context;

    // ------------------ Properties --------------------

    // ------------------ Logic      --------------------

    public MethodParameterBuilder(final ProcessingContext context) {
        this.context = context;
    }

    public Optional<MethodParameter> builder(final EndpointMethod endpointMethod, final VariableElement variableElement) {
        final Optional<ResolvedAnnotationValues> requestParamOption = AnnotationUtils.resolveAnnotation(RequestParam.class, variableElement, context);
        final Optional<ResolvedAnnotationValues> pathVariableOption = AnnotationUtils.resolveAnnotation(PathVariable.class, variableElement, context);
        final Optional<ResolvedAnnotationValues> requestBodyParam = AnnotationUtils.resolveAnnotation(RequestBody.class, variableElement, context);

        if ((!requestBodyParam.isPresent()) && (!pathVariableOption.isPresent()) && (!requestParamOption.isPresent())) {
            return Optional.empty();
        }

        final String paramName = variableElement.getSimpleName().toString();
        
        final MethodParameter param = new MethodParameter(paramName, endpointMethod);
        param.setRequestParameterName(readVariableDef(requestParamOption, paramName));
        param.setPathVariableName(readVariableDef(pathVariableOption, paramName));
        param.setRequestBody(readVariableDef(requestBodyParam, paramName));

        param.setType(context.getTypeOracle().buildInstance(variableElement.asType()));

        return Optional.of(param);
    }

    private Optional<PathVariableDefinition> readVariableDef(Optional<ResolvedAnnotationValues> requestParamOption, String paramName) {
        return requestParamOption.map(av ->
                new PathVariableDefinition(av.readString("name", paramName, context))
                        .setRequired(av.readBoolean("required", true, context))
        );
    }
}
