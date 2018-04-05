package org.omega.typescript.processor;

import org.junit.jupiter.api.Test;
import org.omega.typescript.processor.model.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created by kibork on 4/3/2018.
 */
public class SimpleEnumBuilderTest {

    // ------------------ Constants  --------------------

    // ------------------ Fields     --------------------

    // ------------------ Properties --------------------

    // ------------------ Logic      --------------------


    private Endpoint getEndpoint() {
        final EndpointProcessorSingleton endpointProcessorSingleton = TestUtils.compileClass(
                "/org/omega/typescript/processor/test/SimpleEnumController.java"
        );

        return endpointProcessorSingleton.getEndpointContainer()
                .getEndpoint("org.omega.typescript.processor.test.SimpleEnumController")
                .orElseThrow(() -> new IllegalStateException("SimpleEnumController endpoint not found"));
    }

    @Test
    public void testSimpleProperties() {
        final Endpoint endpoint = getEndpoint();
        final EndpointMethod getSimpleDto = endpoint.getMethod("get")
                .orElseThrow(() -> new IllegalStateException("Unable to find get method!"));

        final TypeDefinition type = getSimpleDto.getReturnType();
        assertEquals(TypeKind.ENUM, type.getTypeKind());

        assertEquals(2, type.getEnumConstants().size());
        assertEquals("VALUE1", type.getEnumConstants().get(0).getName());
        assertEquals("VALUE2", type.getEnumConstants().get(1).getName());
    }

}
