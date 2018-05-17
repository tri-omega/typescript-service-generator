package org.omega.typescript.processor.emitters;

import org.omega.typescript.processor.model.Endpoint;
import org.omega.typescript.processor.utils.StringUtils;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by kibork on 5/8/2018.
 */
public class ModuleEmitter {

    // ---------------- Fields & Constants --------------

    private final EmitContext context;

    private final String defaultModuleName = "api";

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
        if (!StringUtils.hasText(moduleName)) {
            moduleName = defaultModuleName;
        }

        try (PrintWriter writer = context.getStorageStrategy().createWriter(context.getNamingStrategy().getFullModuleName(moduleName))) {
            writer.println("import {NgModule} from '@angular/core';\n");

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
            writer.println("\tproviders: [");
            writer.println(
                endpointList.stream()
                    .map(endpoint -> String.format("\t\t%s", endpoint.getControllerName()))
                    .collect(Collectors.joining(",\n"))
            );
            writer.println("\t]");
            writer.println("})");
            writer.println("export class APIModule { }");

        } catch (Exception ex) {
            throw new RuntimeException("Failed to render module definition", ex);
        }
    }
}
