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
import org.omega.typescript.processor.model.RequestMethod;

import java.util.Optional;

/**
 * Created by kibork on 2/5/2018.
 */
class SimpleEndpointDefinitionBuilderTest {

    @Test
    void buildEndpoint() {
        final EndpointProcessorSingleton endpointProcessorSingleton = TestUtils.compileClass(
                "/org/omega/typescript/processor/test/SimpleController.java"
        );

        final Optional<Endpoint> endpointOption = endpointProcessorSingleton.getEndpointContainer()
                .getEndpoint("org.omega.typescript.processor.test.SimpleController");

        Assert.assertTrue(endpointOption.isPresent());
        final Endpoint endpoint = endpointOption.get();
        Assert.assertTrue(endpoint.getMappingDefinition().isPresent());
        Assert.assertEquals(RequestMethod.POST, endpoint.getMappingDefinition().get().getRequestMethod());
        Assert.assertEquals("SimpleController", endpoint.getControllerName());
        Assert.assertEquals("/api/simple", endpoint.getMappingDefinition().get().getUrlTemplate());
        Assert.assertEquals("org.omega.typescript.processor.test.SimpleController", endpoint.getControllerClassName());
        Assert.assertEquals("org.omega.typescript.processor.test", endpoint.getContainer().getFullName());
    }

    @Test
    void buildNamedEndpoint() {
        final EndpointProcessorSingleton endpointProcessorSingleton = TestUtils.compileClass(
                "/org/omega/typescript/processor/test/NamedSimpleController.java"
        );

        final Optional<Endpoint> endpointOption = endpointProcessorSingleton.getEndpointContainer()
                .getEndpoint("org.omega.typescript.processor.test.NamedSimpleController");
        Assert.assertTrue(endpointOption.isPresent());
        final Endpoint endpoint = endpointOption.get();
        Assert.assertEquals("NamedController", endpoint.getControllerName());
        Assert.assertFalse(endpoint.getMappingDefinition().isPresent());
        Assert.assertEquals("org.omega.typescript.processor.test.NamedSimpleController", endpoint.getControllerClassName());
        Assert.assertEquals("NamedSimpleControllerModule", endpoint.getModuleName());
    }
}
