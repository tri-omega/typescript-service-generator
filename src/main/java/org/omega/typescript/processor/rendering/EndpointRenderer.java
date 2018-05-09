package org.omega.typescript.processor.rendering;

import org.omega.typescript.processor.model.Endpoint;
import org.omega.typescript.processor.model.TypeDefinition;
import org.omega.typescript.processor.utils.RenderUtils;

import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by kibork on 5/2/2018.
 */
public class EndpointRenderer {

    // ------------------ Constants  --------------------

    // ------------------ Fields     --------------------

    private final RenderingContext context;

    private final MethodRenderer methodRenderer;

    // ------------------ Properties --------------------

    // ------------------ Logic      --------------------

    public EndpointRenderer(final RenderingContext context) {
        this.context = context;
        this.methodRenderer = new MethodRenderer(context);
    }

    public void renderEndpoint(final Endpoint endpoint) {
        try (PrintWriter writer = context.getStorageStrategy().createWriter(endpoint)) {
            renderImports(endpoint, writer);
            renderBody(endpoint, writer);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to render endpoint for type " + endpoint.getControllerClassName(), ex);
        }
    }

    private void renderImports(final Endpoint endpoint, final PrintWriter writer) {
        final Set<TypeDefinition> usedTypes = new HashSet<>();
        endpoint.getEndpointMethods().forEach(method -> {
            RenderUtils.visitTypeInstance(usedTypes, method.getReturnType());
            method.getParams().forEach(p -> RenderUtils.visitTypeInstance(usedTypes, p.getType()));
        });

        RenderUtils.renderImports(usedTypes, writer, (d) -> context.getStorageStrategy().getRelativeFileName(endpoint, d));

        //Render implicit imports
        writer.println(
                Stream.of(
                        "import {Injectable} from '@angular/core';",
                        "import {Observable} from 'rxjs/Observable';",
                        "import {HttpRequestMapping,RequestMethod,MethodParamMapping} from './std/service-api';"

                ).collect(Collectors.joining("\n"))
        );
        writer.println();
    }

    private void renderBody(final Endpoint endpoint, final PrintWriter writer) {
        writer.println("@Injectable()");
        writer.printf("export class %s {\n\n", endpoint.getControllerName());

        renderDefaultMapping(endpoint, writer);

        endpoint.getEndpointMethods().forEach(method -> methodRenderer.renderMethod(method, writer));

        writer.println("}\n");
    }

    private void renderDefaultMapping(Endpoint endpoint, PrintWriter writer) {
        writer.print("\tdefaultRequestMapping:HttpRequestMapping = ");
        if (endpoint.getMappingDefinition().isPresent()) {
            methodRenderer.renderMapping(writer, endpoint.getMappingDefinition().get()).println(";");
        } else {
            writer.println("null;");
        }
        writer.println();
    }

}
