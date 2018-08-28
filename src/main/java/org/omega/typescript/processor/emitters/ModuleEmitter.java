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

import org.omega.typescript.processor.GenConfig;
import org.omega.typescript.processor.model.Endpoint;
import org.omega.typescript.processor.utils.StringUtils;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by kibork on 5/8/2018.
 */
public class ModuleEmitter {

    // ---------------- Fields & Constants --------------

    private final EmitContext context;

    // ------------------ Properties --------------------

    // ------------------ Logic      --------------------

    public ModuleEmitter(final EmitContext context) {
        this.context = context;
    }

    public void renderModuleDefinition(final Collection<Endpoint> endpoints) {
        final Map<String, List<Endpoint>> moduleMap = endpoints.stream()
                .collect(Collectors.groupingBy(Endpoint::getModuleName));

        moduleMap.forEach((name, moduleEndpoints) -> renderModule(moduleEndpoints, name));
    }

    private void renderModule(final Collection<Endpoint> endpoints, String moduleName) {
        final GenConfig config = context.getGenConfig();
        if (!StringUtils.hasText(moduleName)) {
            moduleName = config.getDefaultModuleName();
        }

        try (PrintWriter writer = context.getStorageStrategy().createWriter(context.getNamingStrategy().getFullModuleName(moduleName))) {
            writer.println("import {NgModule} from '@angular/core';\n");
            writer.printf("import {%s} from \"%s\";\n", config.getDefaultHttpClassName(), getHttpServiceInclude());

            final List<Endpoint> endpointList = endpoints.stream()
                    .sorted(Comparator.comparing(Endpoint::getControllerName))
                    .collect(Collectors.toList());

            if (!endpointList.isEmpty()) {
                endpointList.forEach(endpoint ->
                        writer.printf("import {%s} from '%s';\n", endpoint.getControllerName(), context.getNamingStrategy().getIncludeFileName(endpoint))
                );
                writer.println();
            }

            writer.println("@NgModule({");
            writer.println(context.indent() + "providers: [");
            writer.println(
                    Stream.concat(
                        Stream.of(context.indent(2) + config.getDefaultHttpClassName()),
                        endpointList.stream()
                            .map(endpoint -> String.format(context.indent(2) + "%s", endpoint.getControllerName()))
                    )
                    .collect(Collectors.joining(",\n"))
            );
            writer.println(context.indent() + "]");
            writer.println("})");
            writer.printf("export class %s { }\n", getModuleClassName(moduleName));

        } catch (Exception ex) {
            throw new RuntimeException("Failed to render module definition", ex);
        }
    }

    private String getHttpServiceInclude() {
        final String path = context.getGenConfig().getDefaultHttpServiceInclude();
        if (path.startsWith("tsg-std")) {
            return "./" + path;
        }
        return path;
    }


    public String getModuleClassName(final String moduleName) {
        String className = "";
        boolean capitalize = true;
        for (int index = 0; index < moduleName.length(); ++index) {
            char c = moduleName.charAt(index);
            if (c == '-') {
                capitalize = true;
            } else {
                c = capitalize ? Character.toUpperCase(c) : c;
                className += c;
                capitalize = false;
            }
        }
        return className;
    }
}
