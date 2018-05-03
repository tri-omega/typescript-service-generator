package org.omega.typescript.processor;

import org.junit.jupiter.api.Test;
import org.omega.typescript.processor.model.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created by kibork on 4/3/2018.
 */
public class MapBuilderTest {

    // ------------------ Constants  --------------------

    // ------------------ Fields     --------------------

    // ------------------ Properties --------------------

    // ------------------ Logic      --------------------


    private Endpoint getEndpoint() {
        final EndpointProcessorSingleton endpointProcessorSingleton = TestUtils.compileClass(
                "/org/omega/typescript/processor/test/MapController.java"
        );

        return endpointProcessorSingleton.getEndpointContainer()
                .getEndpoint("org.omega.typescript.processor.test.MapController")
                .orElseThrow(() -> new IllegalStateException("MapController endpoint not found"));
    }

    @Test
    public void testSimpleProperties() {
        final Endpoint endpoint = getEndpoint();
        final EndpointMethod get = endpoint.getMethod("get")
                .orElseThrow(() -> new IllegalStateException("Unable to find get method!"));

        final TypeInstanceDefinition mapDto = get.getReturnType();
        assertEquals(4, mapDto.getProperties().size());

        checkMapProperty(mapDto, "stringMap", "String", "Object");
        checkMapProperty(mapDto, "stringHashMap", "String", "Object");
        checkMapProperty(mapDto, "dtoMap", "Long", "SimpleDto");

        {
            final PropertyDefinition arrayProperty = mapDto.getPropertyByName("untypedMap")
                    .orElseThrow(() -> new IllegalArgumentException("Property untypedMap not found"));
            assertEquals(TypeKind.MAP, arrayProperty.getType().getTypeKind(), "Failed property untypedMap");
            assertEquals(0, arrayProperty.getType().getGenericTypeArguments().size(), "Failed property untypedMap");
        }
    }

    private void checkMapProperty(final TypeInstanceDefinition collectionDto, final String propName, final String keyType, final String valueType) {
        final PropertyDefinition arrayProperty = collectionDto.getPropertyByName(propName)
                .orElseThrow(() -> new IllegalArgumentException("Property " + propName + " not found"));
        assertEquals(TypeKind.MAP, arrayProperty.getType().getTypeKind(), "Failed property " + propName);
        assertEquals(2, arrayProperty.getType().getGenericTypeArguments().size(), "Failed property " + propName);
        assertEquals(keyType, arrayProperty.getType().getGenericTypeArguments().get(0).getShortName(), "Failed property " + propName);
        assertEquals(valueType, arrayProperty.getType().getGenericTypeArguments().get(1).getShortName(), "Failed property " + propName);
    }

}
