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

import org.omega.typescript.processor.model.TypeInstanceDefinition;
import org.omega.typescript.processor.model.TypeKind;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by kibork on 5/2/2018.
 */
public class TypeInstanceEmitter {

    // ------------------ Constants  --------------------

    // ------------------ Fields     --------------------

    private final EmitContext context;

    // ------------------ Properties --------------------

    // ------------------ Logic      --------------------

    public TypeInstanceEmitter(final EmitContext context) {
        this.context = context;
    }

    public String renderTypeInstance(final TypeInstanceDefinition instanceDefinition) {
        if (instanceDefinition.getTypeKind() == TypeKind.COLLECTION) {
            final String baseTypeName = getCollectionBaseType(instanceDefinition, 0);
            return baseTypeName + instanceDefinition.getTypeScriptName();
        } else if (instanceDefinition.getTypeKind() == TypeKind.MAP) {
            return getMapInstance(instanceDefinition);
        }
        return instanceDefinition.getTypeScriptName() + genericArguments(instanceDefinition.getGenericTypeArguments());
    }

    private String genericArguments(final List<TypeInstanceDefinition> genericTypeArguments) {
        if (genericTypeArguments.isEmpty()) {
            return "";
        }
        final String body = genericTypeArguments.stream()
                .map(this::renderTypeInstance)
                .collect(Collectors.joining(", "))
        ;

        return "<" + body + ">";
    }

    private String getMapInstance(final TypeInstanceDefinition instanceDefinition) {
        if (!instanceDefinition.getGenericTypeArguments().isEmpty()) {
            final String collectionIndexType = getCollectionBaseType(instanceDefinition, 0);
            if ((!"number".equals(collectionIndexType)) && (!"string".equals(collectionIndexType))) {
                context.warning("Unable to use " + collectionIndexType + "as map index: TypeScript at this point prohibits maps of non indexable types");
                return "{}";
            }

            return "{ [index: " + collectionIndexType + "]: " + getCollectionBaseType(instanceDefinition, 1) + " }";
        } else {
            return "{ }";
        }
    }

    private String getCollectionBaseType(final TypeInstanceDefinition instanceDefinition, final int index) {
        final String baseTypeName;
        if (instanceDefinition.getGenericTypeArguments().size() > index) {
            baseTypeName = renderTypeInstance(instanceDefinition.getGenericTypeArguments().get(index));
        } else {
            baseTypeName = context.getProcessingContext().getTypeOracle().getAny().getTypeScriptName();
        }
        return baseTypeName;
    }
}
