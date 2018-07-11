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

import lombok.Data;
import org.omega.typescript.processor.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by kibork on 1/22/2018.
 */
@Data
public class Endpoint {

    // ------------------ Constants  --------------------

    // ------------------ Fields     --------------------

    private String controllerClassName;

    private String controllerName;

    private String moduleName;

    private Optional<MappingDefinition> mappingDefinition = Optional.empty();

    private List<EndpointMethod> endpointMethods = new ArrayList<>();

    private TypeContainer container = null;

    // ------------------ Properties --------------------

    // ------------------ Logic      --------------------

    public Endpoint(String controllerClassName) {
        this.controllerClassName = controllerClassName;
    }

    public Optional<EndpointMethod> getMethod(final String name) {
        return endpointMethods.stream()
                .filter(m -> StringUtils.equals(name, m.getMethodName()))
                .findAny();
    }
}
