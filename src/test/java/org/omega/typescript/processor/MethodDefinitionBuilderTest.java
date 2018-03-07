package org.omega.typescript.processor;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.omega.typescript.processor.model.Endpoint;
import org.omega.typescript.processor.model.EndpointMethod;
import org.omega.typescript.processor.model.RequestMethod;

/**
 * Created by kibork on 3/6/2018.
 */
public class MethodDefinitionBuilderTest {

    // ------------------ Constants  --------------------

    // ------------------ Fields     --------------------

    // ------------------ Properties --------------------

    // ------------------ Logic      --------------------

    @Test
    void buildEndpoint() {
        final EndpointProcessorSingleton endpointProcessorSingleton = TestUtils.compileClass(
                "/org/omega/typescript/processor/test/BasicController.java"
        );

        final Endpoint endpoint = endpointProcessorSingleton.getEndpointContainer()
                .getEndpoint("org.omega.typescript.processor.test.BasicController")
                .orElseThrow(() -> new IllegalStateException("Basic Controller endpoint not found"));

        Assert.assertEquals(11, endpoint.getEndpointMethods().size());

        checkMethod(endpoint.getEndpointMethods().get(0), "getByElementIdAsNamedParam", "get", RequestMethod.GET);
        checkMethod(endpoint.getEndpointMethods().get(1), "getByElementIdAsParam", "getP", RequestMethod.GET);
        checkMethod(endpoint.getEndpointMethods().get(2), "getByElementInNamedPath", "get/{elementId}", RequestMethod.GET);
        checkMethod(endpoint.getEndpointMethods().get(3), "getByElementInPath", "get/{id}", RequestMethod.GET);
        checkMethod(endpoint.getEndpointMethods().get(4), "postDataAsBody", "post", RequestMethod.POST);
        checkMethod(endpoint.getEndpointMethods().get(5), "postDataAsParam", "post", RequestMethod.POST);
        checkMethod(endpoint.getEndpointMethods().get(6), "postDataAsParamAndUrl", "post/{id}", RequestMethod.POST);
        checkMethod(endpoint.getEndpointMethods().get(7), "putDataAsBody", "put", RequestMethod.PUT);
        checkMethod(endpoint.getEndpointMethods().get(8), "deleteById", "delete", RequestMethod.DELETE);
        checkMethod(endpoint.getEndpointMethods().get(9), "patchById", "patch", RequestMethod.PATCH);
        checkMethod(endpoint.getEndpointMethods().get(10), "typeScriptMethodName", "mappingValue", RequestMethod.GET);
    }

    private void checkMethod(final EndpointMethod method, final String namedParam, final String urlTemplate, final RequestMethod requestMethod) {
        Assert.assertEquals(namedParam, method.getMethodName());
        Assert.assertEquals(urlTemplate, method.getMappingDefinition().getUrlTemplate());
        Assert.assertEquals(requestMethod, method.getMappingDefinition().getRequestMethod());
    }

}
