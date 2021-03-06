/*
 * Copyright (c) 2018 William Frank (info@williamfrank.net)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
            assertEquals("CompositeDto", typeParam.getSuperTypes().get(0).getShortName());
            assertEquals("HasName", typeParam.getSuperTypes().get(1).getShortName());
        }


        assertEquals(2, type.getProperties().size());
        {
            final PropertyDefinition property = type.getProperties().get(0);
            checkProperty(property, "field1", "SubGeneric");
            final TypeInstanceDefinition propertyType = property.getType();
            assertEquals(5, propertyType.getGenericTypeArguments().size());
            assertEquals(5, propertyType.getTypeDefinition().getGenericTypeParams().size());
            assertEquals("String", propertyType.getGenericTypeArguments().get(0).getShortName());
            assertEquals("SimpleDto", propertyType.getGenericTypeArguments().get(1).getShortName());
            assertEquals("T", propertyType.getGenericTypeArguments().get(2).getShortName());
            
            assertEquals("Object", propertyType.getGenericTypeArguments().get(4).getShortName());

            {
                final TypeInstanceDefinition pType = propertyType.getGenericTypeArguments().get(3);
                assertEquals("SimpleGeneric", pType.getShortName());
                assertEquals(1, pType.getGenericTypeArguments().size());

                {
                    final TypeInstanceDefinition innerType = pType.getGenericTypeArguments().get(0);
                    assertEquals("DoubleGeneric", innerType.getShortName());
                    assertEquals(2, innerType.getGenericTypeArguments().size());
                    assertEquals("String", innerType.getGenericTypeArguments().get(0).getShortName());
                    assertEquals("Object", innerType.getGenericTypeArguments().get(1).getShortName());
                }
            }

            {
                final PropertyDefinition p6 = propertyType.getPropertyByName("field6")
                        .orElseThrow(IllegalArgumentException::new);
                assertEquals("SimpleGeneric", p6.getType().getShortName());
            }
            {
                final PropertyDefinition p7 = propertyType.getPropertyByName("field7")
                        .orElseThrow(IllegalArgumentException::new);
                assertEquals("Object", p7.getType().getShortName());
            }

        }

//        checkProperty(type.getProperties().get(1), "field2", "SubDto");
    }

}
