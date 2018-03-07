package org.omega.typescript.processor;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;

import javax.tools.JavaFileObject;

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


}
