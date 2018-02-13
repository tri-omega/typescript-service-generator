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

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by kibork on 2/5/2018.
 */
class SimpleEndpointDefinitionCreatorTest {

    @Test
    void buildEndpoint() {
        final JavaFileObject simpleControllerObject = JavaFileObjects.forResource(getClass().getResource("/org/omega/typescript/processor/test/SimpleController.java"));
        final EndpointProcessorSingleton endpointProcessorSingleton = EndpointProcessorSingleton.getInstance();
        endpointProcessorSingleton.clear();
        Compilation compilation = Compiler.javac()
                         .withProcessors(new ServiceEndpointProcessor())
                         .compile(simpleControllerObject)
                ;

        final Optional<Endpoint> endpointOption = endpointProcessorSingleton.getEndpointContainer()
                .getEndpoint("org.omega.typescript.processor.test.SimpleController");

        Assert.assertTrue(endpointOption.isPresent());
        final Endpoint endpoint = endpointOption.get();
        Assert.assertEquals(RequestMethod.POST, endpoint.getDefaultHttpMethod());
        Assert.assertEquals("/api/simple", endpoint.getBaseUrl());
        Assert.assertEquals("org.omega.typescript.processor.test.SimpleController", endpoint.getControllerClassName());

    }

    @Test
    void readRequestMapping() {
    }

    @Test
    void mapHttpMethod() {
    }

    @Test
    void mapRequestPath() {
    }
}
