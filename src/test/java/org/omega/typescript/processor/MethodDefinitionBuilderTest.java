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
import org.omega.typescript.processor.model.*;

import java.util.List;
import java.util.Optional;

/**
 * Created by kibork on 3/6/2018.
 */
public class MethodDefinitionBuilderTest {

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

    private void checkMethod(final EndpointMethod method, final String methodName, final String urlTemplate, final RequestMethod requestMethod) {
        Assert.assertEquals(methodName, method.getMethodName());
        Assert.assertEquals(urlTemplate, method.getMappingDefinition().getUrlTemplate());
        Assert.assertEquals(requestMethod, method.getMappingDefinition().getRequestMethod());
    }


    private void checkVar(Optional<PathVariableDefinition> parameterName, String elementId, boolean b) {
        final PathVariableDefinition vd = parameterName.orElseThrow(IllegalArgumentException::new);
        Assert.assertEquals(elementId, vd.getName());
        Assert.assertEquals(b, vd.isRequired());
    }

    private void checkParam(MethodParameter param, String readParamName, String requestParamName, boolean isRequired, Optional<PathVariableDefinition> paramType) {
        Assert.assertEquals(readParamName, param.getName());

        final boolean isRequestParam = param.getRequestParameterName() == paramType;
        final boolean isPathVariable = param.getPathVariableName() == paramType;
        final boolean isRequestBody = param.getRequestBody() == paramType;

        Assert.assertTrue((param.getRequestParameterName().isPresent() == isRequestParam) &&
                (param.getPathVariableName().isPresent() == isPathVariable) &&
                (param.getRequestBody().isPresent() == isRequestBody));

        checkVar(paramType, requestParamName, isRequired);
    }

    private EndpointMethod getMethod(String getByElementIdAsNamedParam) {
        final Endpoint endpoint = getEndpoint();

        return endpoint.getMethod(getByElementIdAsNamedParam)
                .orElseThrow(IllegalArgumentException::new);
    }

    @Test
    void checkNamedRequestParam() {
        final EndpointMethod endpointMethod = getMethod("getByElementIdAsNamedParam");

        final List<MethodParameter> params = endpointMethod.getParams();
        Assert.assertEquals(1, params.size());

        final MethodParameter param = params.get(0);
        checkParam(param, "id", "elementId", false, param.getRequestParameterName());
    }

    @Test
    void checkUnNamedRequestParam() {
        final EndpointMethod endpointMethod = getMethod("getByElementIdAsParam");
        final List<MethodParameter> params = endpointMethod.getParams();
        Assert.assertEquals(1, params.size());
        final MethodParameter param = params.get(0);
        checkParam(param, "id", "id", true, param.getRequestParameterName());
    }

    @Test
    void checkGetByElementInNamedPath() {
        final EndpointMethod endpointMethod = getMethod("getByElementInNamedPath");
        final List<MethodParameter> params = endpointMethod.getParams();
        Assert.assertEquals(1, params.size());
        final MethodParameter param = params.get(0);
        checkParam(param, "id", "elementId", true, param.getPathVariableName());
    }

    @Test
    void checkGetByElementInPath() {
        final EndpointMethod endpointMethod = getMethod("getByElementInPath");
        final List<MethodParameter> params = endpointMethod.getParams();
        Assert.assertEquals(1, params.size());
        final MethodParameter param = params.get(0);
        checkParam(param, "id", "id", true, param.getPathVariableName());
    }

    @Test
    void checkPostDataAsParamAndUrl() {
        final EndpointMethod endpointMethod = getMethod("postDataAsParamAndUrl");
        final List<MethodParameter> params = endpointMethod.getParams();
        Assert.assertEquals(2, params.size());

        {
            final MethodParameter param = params.get(0);
            checkParam(param, "data", "data", true, param.getRequestParameterName());
        }
        {
            final MethodParameter param = params.get(1);
            checkParam(param, "id", "id", true, param.getPathVariableName());
        }
    }

    @Test
    void checkPutDataAsBody() {
        final EndpointMethod endpointMethod = getMethod("putDataAsBody");
        final List<MethodParameter> params = endpointMethod.getParams();
        Assert.assertEquals(1, params.size());
        final MethodParameter param = params.get(0);
        checkParam(param, "data", "data", false, param.getRequestBody());
    }

}
