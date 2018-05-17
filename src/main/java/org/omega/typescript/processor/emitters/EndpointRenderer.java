package org.omega.typescript.processor.emitters;

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

    private final EmitContext context;

    private final MethodEmitter methodEmitter;

    // ------------------ Properties --------------------

    // ------------------ Logic      --------------------

    public EndpointRenderer(final EmitContext context) {
        this.context = context;
        this.methodEmitter = new MethodEmitter(context);
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

        RenderUtils.renderImports(usedTypes, writer, (d) -> context.getNamingStrategy().getRelativeFileName(endpoint, d));

        //Render implicit imports
        writer.println(
                Stream.of(
                        "import {Injectable} from '@angular/core';",
                        "import {Observable} from 'rxjs/Observable';",
                        String.format("import {%s} from '%s';",
                                context.getGenConfig().getDefaultHttpClassName(),
                                context.getGenConfig().getDefaultHttpServiceInclude()),
                        String.format("import {HttpRequestMapping,RequestMethod,MethodParamMapping} from '%s';",
                                context.getNamingStrategy().getRelativeFileName(endpoint, "std/service-api")
                        )

                ).collect(Collectors.joining("\n"))
        );
        writer.println();
    }

    private void renderBody(final Endpoint endpoint, final PrintWriter writer) {
        writer.println("@Injectable()");
        writer.printf("export class %s {\n\n", endpoint.getControllerName());

        writer.printf("\tconstructor(private httpService:%s) { }\n\n", context.getGenConfig().getDefaultHttpClassName());
        renderDefaultMapping(endpoint, writer);

        endpoint.getEndpointMethods().forEach(method -> methodEmitter.renderMethod(method, writer));

        writer.println("}\n");
    }

    private void renderDefaultMapping(final Endpoint endpoint, final PrintWriter writer) {
        writer.print("\tdefaultRequestMapping:HttpRequestMapping = ");
        if (endpoint.getMappingDefinition().isPresent()) {
            methodEmitter.renderMapping(writer, endpoint.getMappingDefinition().get()).println(";");
        } else {
            writer.println("null;");
        }
        writer.println();
    }

}
