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

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.omega.typescript.processor.model.Endpoint;
import org.omega.typescript.processor.model.EndpointMethod;
import org.omega.typescript.processor.model.MethodParameter;

/**
 * Created by kibork on 3/6/2018.
 */
public class TypeDefinitionBuilderTest {

    // ------------------ Constants  --------------------

    // ------------------ Fields     --------------------

    // ------------------ Properties --------------------

    // ------------------ Logic      --------------------

    private Endpoint getEndpoint() {
        final EndpointProcessorSingleton endpointProcessorSingleton = TestUtils.compileClass(
                "/org/omega/typescript/processor/test/BasicController.java"
        );

        return endpointProcessorSingleton.getEndpointContainer()
                .getEndpoint("org.omega.typescript.processor.test.BasicController")
                .orElseThrow(() -> new IllegalStateException("Basic Controller endpoint not found"));
    }

    @Test
    void buildEndpoint() {
        final Endpoint endpoint = getEndpoint();
        Assert.assertEquals(11, endpoint.getEndpointMethods().size());

        final EndpointMethod endpointMethod = endpoint.getEndpointMethods().get(0);
        Assert.assertEquals("java.lang.String", endpointMethod.getReturnType().getFullName());
        Assert.assertEquals("String", endpointMethod.getReturnType().getShortName());
        Assert.assertEquals("string", endpointMethod.getReturnType().getTypeScriptName());

        final MethodParameter methodParameter = endpointMethod.getParams().get(0);
        Assert.assertEquals("long", methodParameter.getType().getFullName());
        Assert.assertEquals("long", methodParameter.getType().getShortName());
        Assert.assertEquals("number", methodParameter.getType().getTypeScriptName());
    }


}
