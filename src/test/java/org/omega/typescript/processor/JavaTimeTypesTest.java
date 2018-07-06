package org.omega.typescript.processor;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.omega.typescript.processor.model.Endpoint;
import org.omega.typescript.processor.model.EndpointMethod;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created by kibork on 4/3/2018.
 */
public class JavaTimeTypesTest {

    // ------------------ Constants  --------------------

    // ------------------ Fields     --------------------

    // ------------------ Properties --------------------

    // ------------------ Logic      --------------------


    private Endpoint getEndpoint() {
        final EndpointProcessorSingleton endpointProcessorSingleton = TestUtils.compileClass(
                "/org/omega/typescript/processor/test/JavaTimeController.java"
        );

        return endpointProcessorSingleton.getEndpointContainer()
                .getEndpoint("org.omega.typescript.processor.test.JavaTimeController")
                .orElseThrow(() -> new IllegalStateException("JavaTimeController endpoint not found"));
    }

    @Test
    public void testSimpleProperties() {
        final Endpoint endpoint = getEndpoint();

        checkReturnType(endpoint, "getLocalDate", "string");
        checkReturnType(endpoint, "getLocalTime", "string");
        checkReturnType(endpoint, "getLocalDateTime", "string");
        checkReturnType(endpoint, "getZonedDateTime", "number");
    }

    private void checkReturnType(final Endpoint endpoint, final String methodName, final String returnName) {
        final EndpointMethod method = endpoint.getMethod(methodName)
                .orElseThrow(() -> new IllegalStateException("Unable to find " + methodName + " method!"));

        Assert.assertEquals(returnName, method.getReturnType().getTypeScriptName());
    }

}
