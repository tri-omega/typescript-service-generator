package org.omega.typescript.processor.rendering;

import org.omega.typescript.processor.model.Endpoint;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by kibork on 5/8/2018.
 */
public class ModuleRenderer {

    // ---------------- Fields & Constants --------------

    private final RenderingContext context;

    // ------------------ Properties --------------------

    // ------------------ Logic      --------------------

    public ModuleRenderer(final RenderingContext context) {
        this.context = context;
    }

    public void renderModuleDefinition(final Collection<Endpoint> endpoints) {
        try (PrintWriter writer = context.getStorageStrategy().createWriter("api.module.ts")) {
            writer.println("import {NgModule} from '@angular/core';\n");

            final List<Endpoint> endpointList = endpoints.stream()
                    .sorted(Comparator.comparing(Endpoint::getControllerName))
                    .collect(Collectors.toList());

            if (!endpointList.isEmpty()) {
                endpointList.forEach(endpoint ->
                        writer.printf("import {%s} from '%s';\n", endpoint.getControllerName(), context.getStorageStrategy().getFileName(endpoint))
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
