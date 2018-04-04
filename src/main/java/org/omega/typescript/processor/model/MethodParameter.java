package org.omega.typescript.processor.model;

import lombok.Data;

import java.util.Optional;

/**
 * Created by kibork on 2/2/2018.
 */
@Data
public class MethodParameter {

    // ------------------ Constants  --------------------

    // ------------------ Fields     --------------------

    private final String name;

    private final EndpointMethod method;

    private TypeDefinition type;

    private Optional<PathVariableDefinition> pathVariableName;

    private Optional<PathVariableDefinition> requestParameterName;

    private Optional<PathVariableDefinition> requestBody;

    // ------------------ Properties --------------------

    // ------------------ Logic      --------------------

}
