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

    private String name;

    private EndpointMethod method;

    private TypeDefinition type;

    private Optional<String> pathVariableName;

    private Optional<String> parameterName;

    private boolean requestBodyMarker;

    // ------------------ Properties --------------------

    // ------------------ Logic      --------------------

}
