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
        final EndpointProcessorSingleton endpointProcessorSingleton = TestUtils.compileClass("/org/omega/typescript/processor/SimpleController.java");

        final Optional<Endpoint> endpointOption = endpointProcessorSingleton.getEndpointContainer()
                .getEndpoint("org.omega.typescript.processor.test.SimpleController");

        Assert.assertTrue(endpointOption.isPresent());
        final Endpoint endpoint = endpointOption.get();
        Assert.assertEquals(RequestMethod.POST, endpoint.getMappingDefinition().getRequestMethod());
        Assert.assertEquals("SimpleController", endpoint.getControllerName());
        Assert.assertEquals("/api/simple", endpoint.getMappingDefinition().getUrlTemplate());
        Assert.assertEquals("org.omega.typescript.processor.test.SimpleController", endpoint.getControllerClassName());
    }

    @Test
    void buildNamedEndpoint() {
        final EndpointProcessorSingleton endpointProcessorSingleton = TestUtils.compileClass("/org/omega/typescript/processor/test/NamedSimpleController.java");

        final Optional<Endpoint> endpointOption = endpointProcessorSingleton.getEndpointContainer()
                .getEndpoint("org.omega.typescript.processor.test.NamedSimpleController");
        Assert.assertTrue(endpointOption.isPresent());
        final Endpoint endpoint = endpointOption.get();
        Assert.assertEquals("NamedController", endpoint.getControllerName());
        Assert.assertNull(endpoint.getMappingDefinition().getRequestMethod());
        Assert.assertNull(endpoint.getMappingDefinition().getUrlTemplate());
        Assert.assertEquals("org.omega.typescript.processor.test.NamedSimpleController", endpoint.getControllerClassName());
    }
}
