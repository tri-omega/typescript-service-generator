package org.omega.typescript.processor;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.omega.typescript.processor.model.Endpoint;
import org.omega.typescript.processor.model.RequestMethod;

import javax.tools.JavaFileObject;

import java.util.Optional;

/**
 * Created by kibork on 2/5/2018.
 */
class SimpleEndpointDefinitionBuilderTest {

    @Test
    void buildEndpoint() {
        final EndpointProcessorSingleton endpointProcessorSingleton = compileClass("/org/omega/typescript/processor/test/SimpleController.java");

        final Optional<Endpoint> endpointOption = endpointProcessorSingleton.getEndpointContainer()
                .getEndpoint("org.omega.typescript.processor.test.SimpleController");

        Assert.assertTrue(endpointOption.isPresent());
        final Endpoint endpoint = endpointOption.get();
        Assert.assertEquals(RequestMethod.POST, endpoint.getDefaultHttpMethod());
        Assert.assertEquals("SimpleController", endpoint.getControllerName());
        Assert.assertEquals("/api/simple", endpoint.getBaseUrl());
        Assert.assertEquals("org.omega.typescript.processor.test.SimpleController", endpoint.getControllerClassName());
    }

    @Test
    void buildNamedEndpoint() {
        final EndpointProcessorSingleton endpointProcessorSingleton = compileClass("/org/omega/typescript/processor/test/NamedSimpleController.java");

        final Optional<Endpoint> endpointOption = endpointProcessorSingleton.getEndpointContainer()
                .getEndpoint("org.omega.typescript.processor.test.NamedSimpleController");
        Assert.assertTrue(endpointOption.isPresent());
        final Endpoint endpoint = endpointOption.get();
        Assert.assertEquals("NamedController", endpoint.getControllerName());
        Assert.assertNull(endpoint.getDefaultHttpMethod());
        Assert.assertNull(endpoint.getBaseUrl());
        Assert.assertEquals("org.omega.typescript.processor.test.NamedSimpleController", endpoint.getControllerClassName());
    }

    private EndpointProcessorSingleton compileClass(String name) {
        final JavaFileObject simpleControllerObject = JavaFileObjects.forResource(getClass().getResource(name));
        final EndpointProcessorSingleton endpointProcessorSingleton = EndpointProcessorSingleton.getInstance();
        endpointProcessorSingleton.clear();
        Compilation compilation = Compiler.javac()
                         .withProcessors(new ServiceEndpointProcessor())
                         .compile(simpleControllerObject);
        return endpointProcessorSingleton;
    }    
}
