package org.omega.typescript.processor.emitters;

import org.omega.typescript.processor.model.EndpointContainer;
import org.omega.typescript.processor.services.ProcessingContext;
import org.omega.typescript.processor.model.TypeOracle;

/**
 * Created by kibork on 5/2/2018.
 */
public interface Emitter {
    void initContext(ProcessingContext context);

    void renderTypes(TypeOracle type);

    void renderEndpoints(EndpointContainer endpointContainer);
}
