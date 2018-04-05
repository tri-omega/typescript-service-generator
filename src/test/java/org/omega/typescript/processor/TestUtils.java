package org.omega.typescript.processor;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;
import org.omega.typescript.processor.model.PropertyDefinition;

import javax.tools.JavaFileObject;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created by kibork on 3/6/2018.
 */
public class TestUtils {

    // ------------------ Constants  --------------------

    // ------------------ Fields     --------------------

    // ------------------ Properties --------------------

    // ------------------ Logic      --------------------


    public static EndpointProcessorSingleton compileClass(final String resourceName) {
        final JavaFileObject simpleControllerObject = JavaFileObjects.forResource(TestUtils.class.getResource(resourceName));
        final EndpointProcessorSingleton endpointProcessorSingleton = EndpointProcessorSingleton.getInstance();
        endpointProcessorSingleton.clear();

        Compilation compilation = Compiler.javac()
                .withProcessors(new ServiceEndpointProcessor())
                .compile(simpleControllerObject);

        return endpointProcessorSingleton;
    }


    static void checkProperty(PropertyDefinition propertyDefinition, String tsName, String getterName, String typeName) {
        assertEquals(tsName, propertyDefinition.getName());
        assertEquals(getterName, propertyDefinition.getGetterName());
        assertEquals(typeName, propertyDefinition.getType().getShortName());
    }

    static void checkProperty(PropertyDefinition propertyDefinition, String tsName, String typeName) {
        assertEquals(tsName, propertyDefinition.getName());
        assertEquals(typeName, propertyDefinition.getType().getShortName());
    }
}
