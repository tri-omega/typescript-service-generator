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
import org.omega.typescript.processor.model.TypeInstanceDefinition;
import org.omega.typescript.processor.model.TypeKind;
import org.omega.typescript.processor.utils.RenderUtils;

import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by kibork on 4/24/2018.
 */
public class InterfaceTypeEmitter extends BaseTypeEmitter {

    // ------------------ Constants  --------------------

    // ------------------ Fields     --------------------

    // ------------------ Properties --------------------

    // ------------------ Logic      --------------------


    public InterfaceTypeEmitter(final EmitContext context) {
        super(context);
    }

    public TypeKind getSupportedDefinitionType() {
        return TypeKind.INTERFACE;
    }

    @Override
    protected void renderBody(final TypeDefinition definition, final PrintWriter writer) {
        writer.append("export interface ")
                .append(definition.getTypeScriptName())
                .append(getGenericDecl(definition))
                .append(getExtendsDecl(definition.getSuperTypes(), ", "))
                .append(" {");
        writer.println();
        final TypeInstanceEmitter instanceRenderer = context.getInstanceRenderer();

        final String properties = definition.getProperties().stream()
                .map(p -> context.indent() + p.getName() + ": " + instanceRenderer.renderTypeInstance(p.getType()) + ";")
                .collect(Collectors.joining("\n"));
        if (!properties.isEmpty()) {
            writer.append(properties);
            writer.println();
        }
        writer.append("}");
        writer.println();
    }

    private String getGenericDecl(final TypeDefinition definition) {
        if (definition.getGenericTypeParams().isEmpty()) {
            return "";
        }
        final String generics = definition.getGenericTypeParams().stream()
                .map(t -> t.getTypeScriptName() + getExtendsDecl(t.getSuperTypes(), " & "))
                .collect(Collectors.joining(", "));
        return "<" + generics + ">";
    }

    private String getExtendsDecl(final List<TypeInstanceDefinition> superTypes_, String delimiter) {
        final TypeDefinition anyTypeDef = context.getProcessingContext().getTypeOracle().getAny();
        final List<TypeInstanceDefinition> superTypes = superTypes_.stream()
                .filter(t -> t.getTypeDefinition() != anyTypeDef)
                .collect(Collectors.toList());
        if (superTypes.isEmpty()) {
            return "";
        }
        final TypeInstanceEmitter instanceRenderer = context.getInstanceRenderer();
        return " extends " + superTypes.stream()
                .map(instanceRenderer::renderTypeInstance)
                .collect(Collectors.joining(delimiter));
    }

    @Override
    protected void renderImports(final TypeDefinition definition, final PrintWriter writer) {
        final Set<TypeDefinition> usedTypes = new HashSet<>();
        definition.getProperties().forEach(p -> RenderUtils.visitTypeInstance(usedTypes, p.getType()));
        definition.getSuperTypes().forEach(i -> RenderUtils.visitTypeInstance(usedTypes, i));
        definition.getGenericTypeParams().stream()
            .flatMap(gp -> gp.getSuperTypes().stream())
            .forEach(i -> RenderUtils.visitTypeInstance(usedTypes, i));
        //Don't add an import for itself
        usedTypes.remove(definition);
        RenderUtils.<TypeDefinition>renderImports(usedTypes, writer, (d) -> context.getNamingStrategy().getRelativeFileName(definition, d));
    }

}
