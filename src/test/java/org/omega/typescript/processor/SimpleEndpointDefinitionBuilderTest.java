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
