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

package org.omega.typescript.processor.utils;

import org.omega.typescript.processor.model.TypeDefinition;
import org.omega.typescript.processor.model.TypeInstanceDefinition;
import org.omega.typescript.processor.model.TypeKind;

import java.io.PrintWriter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by kibork on 5/2/2018.
 */
public class RenderUtils {

    // ---------------- Fields & Constants --------------

    // ------------------ Properties --------------------

    // ------------------ Logic      --------------------

    public static void renderImports(final Collection<TypeDefinition> usedTypes, final PrintWriter writer,
                                     final Function<TypeDefinition, String> importPathResolver) {
        final List<TypeDefinition> importTypes = usedTypes.stream()
                .filter(Objects::nonNull)
                .filter(t -> !t.isPredefined())
                .filter(t -> t.getTypeKind() == TypeKind.INTERFACE || t.getTypeKind() == TypeKind.ENUM)
                .sorted(Comparator.comparing(TypeDefinition::getTypeScriptName))
                .distinct()
                .collect(Collectors.toList());

        final String imports = importTypes.stream()
                .map(t -> "import {" + t.getTypeScriptName() + "} from '" + importPathResolver.apply(t) + "';")
                .collect(Collectors.joining("\n"));
        if (!imports.isEmpty()) {
            writer.println(imports);
            writer.println();
        }

    }

    public static void visitTypeInstance(final Set<TypeDefinition> knownTypes, final TypeInstanceDefinition instance) {
        knownTypes.add(instance.getTypeDefinition());
        instance.getGenericTypeArguments().forEach(i -> visitTypeInstance(knownTypes, i));
    }
}
