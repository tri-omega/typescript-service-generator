package org.omega.typescript.processor.rendering;

import org.omega.typescript.processor.model.Endpoint;
import org.omega.typescript.processor.model.TypeDefinition;
import org.omega.typescript.processor.utils.RenderUtils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by kibork on 5/2/2018.
 */
public class EndpointRenderer {

    // ------------------ Constants  --------------------

    // ------------------ Fields     --------------------

    private final RenderingContext context;

    // ------------------ Properties --------------------

    // ------------------ Logic      --------------------

    public EndpointRenderer(final RenderingContext context) {
        this.context = context;
    }

    public void renderEndpoint(final Endpoint endpoint) {
        try (BufferedWriter writer = context.getStorageStrategy().createWriter(endpoint)) {
            renderImports(endpoint, writer);
            renderBody(endpoint, writer);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to render endpoint for type " + endpoint.getControllerClassName(), ex);
        }
    }

    private void renderImports(final Endpoint endpoint, final BufferedWriter writer) throws IOException {
        final Set<TypeDefinition> usedTypes = new HashSet<>();
        endpoint.getEndpointMethods().forEach(method -> {
            RenderUtils.visitTypeInstance(usedTypes, method.getReturnType());
            method.getParams().forEach(p -> RenderUtils.visitTypeInstance(usedTypes, p.getType()));
        });

        RenderUtils.renderImports(usedTypes, writer, (d) -> context.getStorageStrategy().getRelativeFileName(endpoint, d));
    }

    private void renderBody(final Endpoint endpoint, final BufferedWriter writer) throws IOException {
        writer.append("@Injectable()");
        writer.newLine();
        writer.append("export class ").append(endpoint.getControllerName()).append(" {");
        writer.newLine();

        

        writer.append("}");
        writer.newLine();
    }
}
