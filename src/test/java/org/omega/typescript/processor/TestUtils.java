package org.omega.typescript.processor;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;
import org.omega.typescript.processor.model.PropertyDefinition;

import javax.tools.JavaFileObject;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created by kibork on 3/6/2018.
 */
public class TestUtils {

    // ------------------ Constants  --------------------

    // ------------------ Fields     --------------------

    // ------------------ Properties --------------------

    // ------------------ Logic      --------------------


    public static EndpointProcessorSingleton compileClass(final String... resourceNames) {
        final EndpointProcessorSingleton endpointProcessorSingleton = EndpointProcessorSingleton.getInstance();
        endpointProcessorSingleton.clear();
        Arrays.stream(resourceNames).forEach(resourceName -> {
            final JavaFileObject simpleControllerObject = JavaFileObjects.forResource(TestUtils.class.getResource(resourceName));

            try {
                Compilation compilation = Compiler.javac()
                        .withProcessors(new ServiceEndpointProcessor())
                        .compile(simpleControllerObject);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });


        return endpointProcessorSingleton;
    }


    static void checkProperty(PropertyDefinition propertyDefinition, String tsName, String typeName) {
        assertEquals(tsName, propertyDefinition.getName());
        assertEquals(typeName, propertyDefinition.getType().getShortName());
    }

}
