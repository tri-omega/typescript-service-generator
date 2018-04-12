package org.omega.typescript.processor;

import org.junit.jupiter.api.Test;
import org.omega.typescript.processor.model.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.omega.typescript.processor.TestUtils.checkProperty;

/**
 * Created by kibork on 4/3/2018.
 */
public class GenericClassControllerTest {

    // ------------------ Constants  --------------------

    // ------------------ Fields     --------------------

    // ------------------ Properties --------------------

    // ------------------ Logic      --------------------


    private Endpoint getEndpoint() {
        final EndpointProcessorSingleton endpointProcessorSingleton = TestUtils.compileClass(
                "/org/omega/typescript/processor/test/GenericClassController.java"
        );

        return endpointProcessorSingleton.getEndpointContainer()
                .getEndpoint("org.omega.typescript.processor.test.GenericClassController")
                .orElseThrow(() -> new IllegalStateException("GenericClassController endpoint not found"));
    }

    @Test
    public void testSimpleProperties() {
        final Endpoint endpoint = getEndpoint();
        final EndpointMethod get = endpoint.getMethod("get")
                .orElseThrow(() -> new IllegalStateException("Unable to find get method!"));

        final TypeInstanceDefinition type = get.getReturnType();

        final TypeDefinition genericClass = EndpointProcessorSingleton.getInstance().getOracle()
                .getType("org.omega.typescript.processor.test.dto.GenericClass")
                .orElseThrow(() -> new IllegalStateException("Class Not found"));

        assertEquals(1, genericClass.getGenericTypeParams().size());
        {
            final TypeDefinition typeParam = genericClass.getGenericTypeParams().get(0);
            assertEquals("T", typeParam.getShortName());
            assertEquals("org.omega.typescript.processor.test.dto.GenericClass#T", typeParam.getFullName());
            assertEquals(TypeKind.GENERIC_PLACEHOLDER, typeParam.getTypeKind());
            assertEquals(2, typeParam.getSuperTypes().size());
            assertEquals("CompositDto", typeParam.getSuperTypes().get(0).getShortName());
            assertEquals("HasName", typeParam.getSuperTypes().get(1).getShortName());
        }


        assertEquals(2, type.getProperties().size());
        {
            final PropertyDefinition property = type.getProperties().get(0);
            checkProperty(property, "field1", "SubGeneric");
            final TypeInstanceDefinition propertyType = property.getType();
            assertEquals(5, propertyType.getGenericTypeArguments().size());
            assertEquals("String", propertyType.getGenericTypeArguments().get(0).getShortName());
            assertEquals("SimpleDto", propertyType.getGenericTypeArguments().get(1).getShortName());
            assertEquals("T", propertyType.getGenericTypeArguments().get(2).getShortName());
            
            assertEquals("SimpleGeneric", propertyType.getGenericTypeArguments().get(3).getShortName());

            assertEquals("Object", propertyType.getGenericTypeArguments().get(4).getShortName());

        }

//        checkProperty(type.getProperties().get(1), "field2", "SubDto");
    }

}
