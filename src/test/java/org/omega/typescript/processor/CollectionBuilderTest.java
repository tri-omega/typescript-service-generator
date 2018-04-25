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

        checkCollectionProperty(collectionDto, "stringArray", "String");

        checkCollectionProperty(collectionDto, "stringList", "String");
        checkCollectionProperty(collectionDto, "stringSet", "String");
        checkCollectionProperty(collectionDto, "longCollection", "Long");

        {
            final PropertyDefinition arrayProperty = collectionDto.getPropertyByName("untypedList")
                    .orElseThrow(() -> new IllegalArgumentException("Property untypedList not found"));
            assertEquals(TypeKind.COLLECTION, arrayProperty.getType().getTypeKind(), "Failed property untypedList");
            assertEquals(0, arrayProperty.getType().getGenericTypeArguments().size(), "Failed property untypedList");

        }
    }

    private void checkCollectionProperty(final TypeInstanceDefinition collectionDto, final String propName, String elementType) {
        final PropertyDefinition arrayProperty = collectionDto.getPropertyByName(propName)
                .orElseThrow(() -> new IllegalArgumentException("Property " + propName + " not found"));
        assertEquals(TypeKind.COLLECTION, arrayProperty.getType().getTypeKind(), "Failed property " + propName);
        assertEquals(1, arrayProperty.getType().getGenericTypeArguments().size(), "Failed property " + propName);
        assertEquals(elementType, arrayProperty.getType().getGenericTypeArguments().get(0).getShortName(), "Failed property " + propName);
    }

}
