package org.omega.typescript.processor;

import org.junit.jupiter.api.Test;
import org.omega.typescript.processor.model.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created by kibork on 4/3/2018.
 */
public class CollectionBuilderTest {

    // ------------------ Constants  --------------------

    // ------------------ Fields     --------------------

    // ------------------ Properties --------------------

    // ------------------ Logic      --------------------


    private Endpoint getEndpoint() {
        final EndpointProcessorSingleton endpointProcessorSingleton = TestUtils.compileClass(
                "/org/omega/typescript/processor/test/CollectionController.java"
        );

        return endpointProcessorSingleton.getEndpointContainer()
                .getEndpoint("org.omega.typescript.processor.test.CollectionController")
                .orElseThrow(() -> new IllegalStateException("CollectionController endpoint not found"));
    }

    @Test
    public void testSimpleProperties() {
        final Endpoint endpoint = getEndpoint();
        final EndpointMethod get = endpoint.getMethod("get")
                .orElseThrow(() -> new IllegalStateException("Unable to find get method!"));

        final TypeInstanceDefinition collectionDto = get.getReturnType();

        final PropertyDefinition arrayProperty = collectionDto.getPropertyByName("stringArray")
                .orElseThrow(() -> new RuntimeException("Not found"));
        assertEquals(TypeKind.COLLECTION, arrayProperty.getType().getTypeKind());
        assertEquals("String", arrayProperty.getType().getGenericTypeArguments().get(0).getShortName());

    }

}
