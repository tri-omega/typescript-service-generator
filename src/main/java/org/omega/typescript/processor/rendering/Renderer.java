package org.omega.typescript.processor.rendering;

import org.omega.typescript.processor.EndpointContainer;
import org.omega.typescript.processor.ProcessingContext;
import org.omega.typescript.processor.TypeOracle;

/**
 * Created by kibork on 5/2/2018.
 */
public interface Renderer {
    void initContext(ProcessingContext context);

    void renderTypes(TypeOracle type);

    void renderEndpoints(EndpointContainer endpointContainer);
}
