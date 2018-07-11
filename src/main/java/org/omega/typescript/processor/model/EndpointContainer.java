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

package org.omega.typescript.processor.model;

import org.omega.typescript.api.TypeScriptEndpoint;
import org.omega.typescript.processor.builders.EndpointDefinitionBuilder;
import org.omega.typescript.processor.services.ProcessingContext;

import javax.lang.model.element.TypeElement;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by kibork on 2/2/2018.
 */
public class EndpointContainer {

    // ------------------ Constants  --------------------

    // ------------------ Fields     --------------------

    private final Map<String, Endpoint> endpointMap = new ConcurrentHashMap<>();

    // ------------------ Properties --------------------

    public Map<String, Endpoint> getEndpointMap() {
        return Collections.unmodifiableMap(endpointMap);
    }


    // ------------------ Logic      --------------------

    public Optional<Endpoint> getEndpoint(final String controllerClassName) {
        return Optional.ofNullable(endpointMap.get(controllerClassName.intern()));
    }

    public boolean hasEndpoint(final String controllerClassName) {
        return endpointMap.containsKey(controllerClassName);
    }

    public Endpoint buildEndpoint(final TypeElement type, final ProcessingContext context) {
        if (type.getAnnotation(TypeScriptEndpoint.class) == null) {
            throw new IllegalArgumentException("Type is not an Type Script controller " + type.getQualifiedName());
        }
        final String controllerClassName = type.getQualifiedName().toString().intern();
        return endpointMap.computeIfAbsent(controllerClassName,
            (className) ->
                new EndpointDefinitionBuilder(context)
                        .buildEndpoint(type)
        );
    }

    public void clear() {
        endpointMap.clear();
    }
}
