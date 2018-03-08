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

    private Optional<VariableDefinition> pathVariableName;

    private Optional<VariableDefinition> requestParameterName;

    private Optional<VariableDefinition> requestBody;

    // ------------------ Properties --------------------

    // ------------------ Logic      --------------------

}
