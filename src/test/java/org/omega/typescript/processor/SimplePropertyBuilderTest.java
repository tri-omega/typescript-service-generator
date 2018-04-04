package org.omega.typescript.processor;

import org.junit.jupiter.api.Test;
import org.omega.typescript.processor.model.Endpoint;
import org.omega.typescript.processor.model.EndpointMethod;
import org.omega.typescript.processor.model.PropertyDefinition;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created by kibork on 4/3/2018.
 */
public class SimplePropertyBuilderTest {

    // ------------------ Constants  --------------------

    // ------------------ Fields     --------------------

    // ------------------ Properties --------------------

    // ------------------ Logic      --------------------


    private Endpoint getEndpoint() {
        final EndpointProcessorSingleton endpointProcessorSingleton = TestUtils.compileClass(
                "/org/omega/typescript/processor/test/SimpleDtoController.java"
        );

        return endpointProcessorSingleton.getEndpointContainer()
                .getEndpoint("org.omega.typescript.processor.test.SimpleDtoController")
                .orElseThrow(() -> new IllegalStateException("SimpleDtoController endpoint not found"));
    }

    @Test
    public void testSimpleProperties() {
        final Endpoint endpoint = getEndpoint();
        final EndpointMethod getSimpleDto = endpoint.getMethod("getSimpleDto")
                .orElseThrow(() -> new IllegalStateException("Unable to find getSimpleDto method!"));

        final List<PropertyDefinition> properties = getSimpleDto.getReturnType().getProperties();
        
        assertEquals(3, properties.size());

        checkProperty(properties.get(0), "field1", "getField1", "String");
        checkProperty(properties.get(1), "field2", "getField2", "long");
        checkProperty(properties.get(2), "customName", "getField3", "Integer");
    }

    private void checkProperty(PropertyDefinition propertyDefinition, String tsName, String getterName, String typeName) {
        assertEquals(tsName, propertyDefinition.getName());
        assertEquals(getterName, propertyDefinition.getGetterName());
        assertEquals(typeName, propertyDefinition.getType().getShortName());
    }

}
