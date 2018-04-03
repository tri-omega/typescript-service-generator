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
