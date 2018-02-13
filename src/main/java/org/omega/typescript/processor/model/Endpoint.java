package org.omega.typescript.processor.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kibork on 1/22/2018.
 */
@Data
public class Endpoint {

    // ------------------ Constants  --------------------

    // ------------------ Fields     --------------------

    private String controllerClassName;

    private String baseUrl;

    private RequestMethod defaultHttpMethod;

    private List<EndpointMethod> endpointMethods = new ArrayList<>();

    // ------------------ Properties --------------------

    // ------------------ Logic      --------------------

    public Endpoint(String controllerClassName) {
        this.controllerClassName = controllerClassName;
    }
}
