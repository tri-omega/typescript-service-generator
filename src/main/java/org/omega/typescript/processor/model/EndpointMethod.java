package org.omega.typescript.processor.model;

import lombok.Data;

/**
 * Created by kibork on 2/2/2018.
 */
@Data
public class EndpointMethod {

    // ------------------ Constants  --------------------

    // ------------------ Fields     --------------------

    private Endpoint endpoint;

    private String methodName;

    private String path;

    private TypeDefinition returnType;



    // ------------------ Properties --------------------

    // ------------------ Logic      --------------------

}
