package org.omega.typescript.processor;

import org.junit.jupiter.api.Test;
import org.omega.typescript.processor.model.Endpoint;
import org.omega.typescript.processor.model.EndpointMethod;
import org.omega.typescript.processor.model.TypeDefinition;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.omega.typescript.processor.TestUtils.checkProperty;

/**
 * Created by kibork on 4/3/2018.
 */
public class WithSubClassControllerTest {

    // ------------------ Constants  --------------------

    // ------------------ Fields     --------------------

    // ------------------ Properties --------------------

    // ------------------ Logic      --------------------


    private Endpoint getEndpoint() {
        final EndpointProcessorSingleton endpointProcessorSingleton = TestUtils.compileClass(
                "/org/omega/typescript/processor/test/WithSubClassController.java"
        );

        return endpointProcessorSingleton.getEndpointContainer()
                .getEndpoint("org.omega.typescript.processor.test.WithSubClassController")
                .orElseThrow(() -> new IllegalStateException("WithSubClassController endpoint not found"));
    }

    @Test
    public void testSimpleProperties() {
        final Endpoint endpoint = getEndpoint();
        final EndpointMethod get = endpoint.getMethod("get")
                .orElseThrow(() -> new IllegalStateException("Unable to find get method!"));

        final TypeDefinition type = get.getReturnType();

        assertEquals(2, type.getProperties().size());
        checkProperty(type.getProperties().get(0), "field1", "String");
        checkProperty(type.getProperties().get(1), "field2", "SubDto");

        assertEquals("org.omega.typescript.processor.test.dto.WithSubclassDto.SubDto", type.getProperties().get(1).getType().getFullName());

        assertEquals("org.omega.typescript.processor.test.dto.WithSubclassDto.SubDto.SubEnum", type.getProperties().get(1).getType()
                .getProperties().get(0).getType().getFullName());
    }

}
