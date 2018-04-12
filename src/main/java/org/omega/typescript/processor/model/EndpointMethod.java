package org.omega.typescript.processor.model;

import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kibork on 2/2/2018.
 */
@Data
@ToString(exclude = "endpoint")
public class EndpointMethod {

    // ------------------ Constants  --------------------

    // ------------------ Fields     --------------------

    private final Endpoint endpoint;

    private final String methodName;

    private final MappingDefinition mappingDefinition;

    private TypeInstanceDefinition returnType;

    private List<MethodParameter> params = new ArrayList<>();

    // ------------------ Properties --------------------

    // ------------------ Logic      --------------------
        
}
