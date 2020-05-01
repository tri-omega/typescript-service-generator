/*
 * Copyright (c) 2018 William Frank (info@williamfrank.net)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.omega.typescript.processor.emitters;

import org.omega.typescript.processor.model.EndpointContainer;
import org.omega.typescript.processor.model.TypeDefinition;
import org.omega.typescript.processor.model.TypeKind;
import org.omega.typescript.processor.model.TypeOracle;
import org.omega.typescript.processor.services.*;
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

    private EndpointEmitter endpointEmitter;

    private ModuleEmitter moduleEmitter;

    // ------------------ Properties --------------------

    // ------------------ Logic      --------------------


    @Override
    public void initContext(final ProcessingContext execContext) {

        final GenConfigBasedNamingStrategy namingStrategy = new GenConfigBasedNamingStrategy(execContext);

        final StorageStrategy storageStrategy = getStorageStrategy(execContext, namingStrategy);
        this.context = new EmitContext(execContext, namingStrategy, storageStrategy);
        addDefinitionRenderer(new InterfaceTypeEmitter(context));
        addDefinitionRenderer(new EnumTypeEmitter(context));

        endpointEmitter = new EndpointEmitter(context);
        moduleEmitter = new ModuleEmitter(context);
    }

    private StorageStrategy getStorageStrategy(final ProcessingContext execContext, final GenConfigBasedNamingStrategy namingStrategy) {
        switch (execContext.getGenConfig().getStorageStrategy().toLowerCase()) {
            case "javac": return new JavacStorageStrategy(execContext, namingStrategy);
            default: return new FileStorageStrategy(execContext, namingStrategy);
        }
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
        final String serviceIncludeFileName = context.getNamingStrategy().getFullFileName(context.getGenConfig().getStdApiFileName());
        final String requestManagerFileName = context.getNamingStrategy().getFullFileName(context.getGenConfig().getRequestManagerFileName());
        IOUtils.copyResource("/ts/service-api.ts", context.getStorageStrategy().getFile(serviceIncludeFileName));
        IOUtils.copyResource("/ts/ServiceRequestManager.ts", context.getStorageStrategy().getFile(requestManagerFileName));

        endpointContainer.getEndpointMap()
                .values()
                .forEach(endpoint -> endpointEmitter.renderEndpoint(endpoint));

        moduleEmitter.renderModuleDefinition(endpointContainer.getEndpointMap().values());
    }

}
