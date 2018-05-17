package org.omega.typescript.processor.emitters;

import org.omega.typescript.processor.model.TypeDefinition;

import java.io.PrintWriter;

/**
 * Created by kibork on 5/2/2018.
 */
public abstract class BaseTypeEmitter implements TypeDefinitionEmitter {

    // ------------------ Fields     --------------------

    protected final EmitContext context;

    // ------------------ Properties --------------------

    // ------------------ Logic      --------------------


    public BaseTypeEmitter(final EmitContext context) {
        this.context = context;
    }

    @Override
    public void render(final TypeDefinition definition) {
        if (definition.getTypeKind() != getSupportedDefinitionType()) {
            throw new IllegalArgumentException("Invalid renderer for type " + definition);
        }

        try (PrintWriter writer = context.getStorageStrategy().createWriter(definition)) {
            renderImports(definition, writer);
            renderBody(definition, writer);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to render interface for type " + definition.getFullName(), ex);
        }
    }

    protected abstract void renderBody(TypeDefinition definition, PrintWriter writer) throws Exception;

    protected abstract void renderImports(TypeDefinition definition, PrintWriter writer) throws Exception;
}
