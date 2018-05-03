package org.omega.typescript.processor.rendering;

import org.omega.typescript.processor.EndpointContainer;
import org.omega.typescript.processor.ProcessingContext;
import org.omega.typescript.processor.TypeOracle;
import org.omega.typescript.processor.model.TypeDefinition;

/**
 * Created by kibork on 5/2/2018.
 */
public class TypeScriptRenderer implements Renderer {

    // ------------------ Constants  --------------------

    // ------------------ Fields     --------------------

    private RenderingContext context;

    // ------------------ Properties --------------------

    // ------------------ Logic      --------------------


    @Override
    public void initContext(final ProcessingContext context) {
        this.context = new RenderingContext(context, new FileStorageStrategy(context));
    }

    @Override
    public void renderTypes(final TypeOracle oracle) {
        oracle.getKnownTypes()
                .stream()
                .filter(t -> !t.isPredefined())
                .forEach(this::renderType);
   }

    private void renderType(TypeDefinition t) {
        switch (t.getTypeKind()) {
            case INTERFACE: context.getInterfaceRenderer().renderInterface(t); break;
            case ENUM: context.getEnumRenderer().renderEnum(t); break;
        }
    }

    @Override
    public void renderEndpoints(final EndpointContainer endpointContainer) {
        
    }

}
