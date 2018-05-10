package org.omega.typescript.processor.model;

import lombok.Data;
import org.omega.typescript.processor.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by kibork on 1/22/2018.
 */
@Data
public class Endpoint {

    // ------------------ Constants  --------------------

    // ------------------ Fields     --------------------

    private String controllerClassName;

    private String controllerName;

    private String moduleName;

    private Optional<MappingDefinition> mappingDefinition = Optional.empty();

    private List<EndpointMethod> endpointMethods = new ArrayList<>();

    // ------------------ Properties --------------------

    // ------------------ Logic      --------------------

    public Endpoint(String controllerClassName) {
        this.controllerClassName = controllerClassName;
    }

    public Optional<EndpointMethod> getMethod(final String name) {
        return endpointMethods.stream()
                .filter(m -> StringUtils.equals(name, m.getMethodName()))
                .findAny();
    }
}
