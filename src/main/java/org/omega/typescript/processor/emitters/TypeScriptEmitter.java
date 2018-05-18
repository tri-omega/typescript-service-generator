package org.omega.typescript.processor.emitters;

import org.omega.typescript.processor.model.EndpointContainer;
import org.omega.typescript.processor.model.TypeDefinition;
import org.omega.typescript.processor.model.TypeKind;
import org.omega.typescript.processor.model.TypeOracle;
import org.omega.typescript.processor.services.FileStorageStrategy;
import org.omega.typescript.processor.services.GenConfigBasedNamingStrategy;
import org.omega.typescript.processor.services.ProcessingContext;
import org.omega.typescript.processor.utils.IOUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kibork on 5/2/2018.
 */
public class TypeScriptEmitter implements Emitter {

    // ------------------ Constants  --------------------

    // ------------------ Fields     --------------------

    private EmitContext context;

    private Map<TypeKind, TypeDefinitionEmitter> definitionRenderers = new HashMap<>();

    private EndpointRenderer endpointRenderer;

    private ModuleEmitter moduleEmitter;

    // ------------------ Properties --------------------

    // ------------------ Logic      --------------------


    @Override
    public void initContext(final ProcessingContext execContext) {

        final GenConfigBasedNamingStrategy namingStrategy = new GenConfigBasedNamingStrategy(execContext);

        this.context = new EmitContext(execContext, namingStrategy, new FileStorageStrategy(execContext, namingStrategy));
        addDefinitionRenderer(new InterfaceTypeEmitter(context));
        addDefinitionRenderer(new EnumTypeEmitter(context));

        endpointRenderer = new EndpointRenderer(context);
        moduleEmitter = new ModuleEmitter(context);
    }

    private void addDefinitionRenderer(TypeDefinitionEmitter renderer) {
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
        final TypeDefinitionEmitter renderer = definitionRenderers.get(t.getTypeKind());
        if (renderer != null) {
            renderer.render(t);
        }
    }

    @Override
    public synchronized void renderEndpoints(final EndpointContainer endpointContainer) {
        final String serviceIncludeFileName = context.getNamingStrategy().getFullFileName(context.getGenConfig().getStdFileName());
        IOUtils.copyResource("/ts/service-api.ts", context.getStorageStrategy().getFile(serviceIncludeFileName));

        endpointContainer.getEndpointMap()
                .values()
                .forEach(endpoint -> endpointRenderer.renderEndpoint(endpoint));

        moduleEmitter.renderModuleDefinition(endpointContainer.getEndpointMap().values());
    }

}
