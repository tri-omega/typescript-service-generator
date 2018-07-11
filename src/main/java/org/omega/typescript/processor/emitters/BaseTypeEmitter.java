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
