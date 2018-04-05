package org.omega.typescript.processor;

import org.junit.jupiter.api.Test;
import org.omega.typescript.processor.model.Endpoint;
import org.omega.typescript.processor.model.EndpointMethod;
import org.omega.typescript.processor.model.PropertyDefinition;
import org.omega.typescript.processor.model.TypeDefinition;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created by kibork on 4/3/2018.
 */
public class SpecificCompositPropertyBuilderTest {

    // ------------------ Constants  --------------------

    // ------------------ Fields     --------------------

    // ------------------ Properties --------------------

    // ------------------ Logic      --------------------


    private Endpoint getEndpoint() {
        final EndpointProcessorSingleton endpointProcessorSingleton = TestUtils.compileClass(
                "/org/omega/typescript/processor/test/SpecificCompositDtoController.java"
        );

        return endpointProcessorSingleton.getEndpointContainer()
                .getEndpoint("org.omega.typescript.processor.test.SpecificCompositDtoController")
                .orElseThrow(() -> new IllegalStateException("SpecificCompositDtoController endpoint not found"));
    }

    @Test
    public void testSimpleProperties() {
        final Endpoint endpoint = getEndpoint();
        final EndpointMethod getSimpleDto = endpoint.getMethod("get")
                .orElseThrow(() -> new IllegalStateException("Unable to find get method!"));

        final TypeDefinition type = getSimpleDto.getReturnType();
        final List<PropertyDefinition> properties = type.getProperties();
        
        assertEquals(1, properties.size());

        checkProperty(properties.get(0), "newProperty", "getNewProperty", "String");
        
        assertEquals(2, type.getSuperTypes().size());

        {
            final TypeDefinition superType = type.getSuperTypes().get(0);
            assertEquals("org.omega.typescript.processor.test.dto.CompositDto", superType.getFullName());
            assertEquals(0, superType.getSuperTypes().size());
        }

        {
            final TypeDefinition superInterface = type.getSuperTypes().get(1);
            assertEquals("org.omega.typescript.processor.test.dto.HasName", superInterface.getFullName());
            assertEquals(0, superInterface.getSuperTypes().size());

            assertEquals(1, superInterface.getProperties().size());
            checkProperty(superInterface.getProperties().get(0), "name", "getName", "String");
        }
    }

    private void checkProperty(PropertyDefinition propertyDefinition, String tsName, String getterName, String typeName) {
        assertEquals(tsName, propertyDefinition.getName());
        assertEquals(getterName, propertyDefinition.getGetterName());
        assertEquals(typeName, propertyDefinition.getType().getShortName());
    }

}
