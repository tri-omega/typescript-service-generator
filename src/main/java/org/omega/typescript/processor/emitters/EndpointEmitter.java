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

package org.omega.typescript.processor.emitters;

import org.omega.typescript.processor.model.Endpoint;
import org.omega.typescript.processor.model.TypeDefinition;
import org.omega.typescript.processor.utils.RenderUtils;
import org.omega.typescript.processor.utils.StringUtils;

import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by kibork on 5/2/2018.
 */
public class EndpointEmitter {

    // ------------------ Constants  --------------------

    // ------------------ Fields     --------------------

    private final EmitContext context;

    private final MethodEmitter methodEmitter;

    // ------------------ Properties --------------------

    // ------------------ Logic      --------------------

    public EndpointEmitter(final EmitContext context) {
        this.context = context;
        this.methodEmitter = new MethodEmitter(context);
    }

    public void renderEndpoint(final Endpoint endpoint) {
        try (PrintWriter writer = context.getStorageStrategy().createWriter(endpoint)) {
            renderImports(endpoint, writer);
            renderBody(endpoint, writer);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to render endpoint for type " + endpoint.getControllerClassName() + ":" + ex.getClass() + ">" + ex.getMessage(), ex);
        }
    }

    private void renderImports(final Endpoint endpoint, final PrintWriter writer) {
        final Set<TypeDefinition> usedTypes = new HashSet<>();
        endpoint.getEndpointMethods().forEach(method -> {
            RenderUtils.visitTypeInstance(usedTypes, method.getReturnType());
            method.getParams().forEach(p -> RenderUtils.visitTypeInstance(usedTypes, p.getType()));
        });

        RenderUtils.renderImports(usedTypes, writer, (d) -> context.getNamingStrategy().getRelativeFileName(endpoint, d));

        //Render implicit imports
        writer.println(
                Stream.of(
                        context.getGenConfig().getAdditionalServiceIncludes(),
                        String.format("import {%s} from '%s';",
                                context.getGenConfig().getDefaultHttpClassName(),
                                getHttpServiceInclude(endpoint)),
                        String.format("import {HttpRequestMapping, MethodParamMapping, RequestMethod} from '%s';",
                                context.getNamingStrategy().getRelativeFileName(endpoint, getIncludeFileName())
                        )

                ).map(String::trim)
                .filter(StringUtils::hasText)
                .collect(Collectors.joining("\n"))
        );
        writer.println();
    }

    private String getHttpServiceInclude(final Endpoint endpoint) {
        final String path = context.getGenConfig().getDefaultHttpServiceInclude();
        if (path.startsWith("tsg-std")) {
            return context.getNamingStrategy().getRelativeFileName(endpoint, path);
        }
        return path;
    }

    private String getIncludeFileName() {
        final String fileName = context.getGenConfig().getStdApiFileName();
        if (fileName.endsWith(".ts")) {
            return fileName.substring(0, fileName.length() - ".ts".length());
        }
        return fileName;
    }

    private void renderBody(final Endpoint endpoint, final PrintWriter writer) {
        writer.println("@Injectable()");
        writer.printf("export class %s {\n\n", endpoint.getControllerName());

        writer.printf(context.indent() + "constructor(private httpService:%s) { }\n\n", context.getGenConfig().getDefaultHttpClassName());
        renderDefaultMapping(endpoint, writer);

        endpoint.getEndpointMethods().forEach(method -> methodEmitter.renderMethod(method, writer));

        writer.println("}\n");
    }

    private void renderDefaultMapping(final Endpoint endpoint, final PrintWriter writer) {
        writer.print(context.indent() + "defaultRequestMapping:HttpRequestMapping = ");
        if (endpoint.getMappingDefinition().isPresent()) {
            methodEmitter.renderMapping(writer, endpoint.getMappingDefinition().get()).println(";");
        } else {
            writer.println("null;");
        }
        writer.println();
    }

}
