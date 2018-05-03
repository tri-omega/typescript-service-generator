package org.omega.typescript.processor.rendering;

import org.omega.typescript.processor.EndpointContainer;
import org.omega.typescript.processor.ProcessingContext;
import org.omega.typescript.processor.TypeOracle;
import org.omega.typescript.processor.model.TypeDefinition;
import org.omega.typescript.processor.model.TypeKind;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kibork on 5/2/2018.
 */
public class TypeScriptRenderer implements Renderer {

    // ------------------ Constants  --------------------

    // ------------------ Fields     --------------------

    private RenderingContext context;

    private Map<TypeKind, TypeDefinitionRenderer> definitionRenderers = new HashMap<>();

    private EndpointRenderer endpointRenderer;

    // ------------------ Properties --------------------

    // ------------------ Logic      --------------------


    @Override
    public void initContext(final ProcessingContext execContext) {
        this.context = new RenderingContext(execContext, new FileStorageStrategy(execContext));
        addDefinitionRenderer(new InterfaceTypeRenderer(context));
        addDefinitionRenderer(new EnumTypeRenderer(context));

        endpointRenderer = new EndpointRenderer(context);
    }

    private void addDefinitionRenderer(TypeDefinitionRenderer renderer) {
        definitionRenderers.put(renderer.getSupportedDefinitionType(), renderer);
    }

    @Override
    public synchronized void renderTypes(final TypeOracle oracle) {
        oracle.getKnownTypes()
                .stream()
                .filter(t -> !t.isPredefined())
                .forEach(this::renderType);
   }

    private void renderType(final TypeDefinition t) {
        final TypeDefinitionRenderer renderer = definitionRenderers.get(t.getTypeKind());
        if (renderer != null) {
            renderer.render(t);
        }
    }

    @Override
    public synchronized void renderEndpoints(final EndpointContainer endpointContainer) {
        endpointContainer.getEndpointMap()
                .values()
                .forEach(endpoint -> endpointRenderer.renderEndpoint(endpoint));
    }

}
