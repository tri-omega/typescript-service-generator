package org.omega.typescript.processor;

import org.junit.jupiter.api.Test;
import org.omega.typescript.processor.model.Endpoint;
import org.omega.typescript.processor.model.EndpointMethod;
import org.omega.typescript.processor.model.PropertyDefinition;
import org.omega.typescript.processor.test.dto.SimpleDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.omega.typescript.processor.TestUtils.checkProperty;

/**
 * Created by kibork on 4/3/2018.
 */
public class CompositPropertyBuilderTest {

    // ------------------ Constants  --------------------

    // ------------------ Fields     --------------------

    // ------------------ Properties --------------------

    // ------------------ Logic      --------------------


    private Endpoint getEndpoint() {
        final EndpointProcessorSingleton endpointProcessorSingleton = TestUtils.compileClass(
                "/org/omega/typescript/processor/test/CompositDtoController.java"
        );

        return endpointProcessorSingleton.getEndpointContainer()
                .getEndpoint("org.omega.typescript.processor.test.CompositDtoController")
                .orElseThrow(() -> new IllegalStateException("CompositDtoController endpoint not found"));
    }

    @Test
    public void testSimpleProperties() {
        final Endpoint endpoint = getEndpoint();
        final EndpointMethod getSimpleDto = endpoint.getMethod("get")
                .orElseThrow(() -> new IllegalStateException("Unable to find get method!"));

        final List<PropertyDefinition> properties = getSimpleDto.getReturnType().getProperties();
        
        assertEquals(2, properties.size());

        checkProperty(properties.get(0), "field1", "getField1", "String");
        checkProperty(properties.get(1), "simpleDto", "getSimpleDto", SimpleDto.class.getSimpleName());
    }



}
