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
