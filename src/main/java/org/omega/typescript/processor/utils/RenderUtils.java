package org.omega.typescript.processor.utils;

import org.omega.typescript.processor.model.TypeDefinition;
import org.omega.typescript.processor.model.TypeInstanceDefinition;
import org.omega.typescript.processor.model.TypeKind;

import java.io.BufferedWriter;
import java.io.IOException;
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

    public static void renderImports(final Collection<TypeDefinition> usedTypes, final BufferedWriter writer, final Function<TypeDefinition, String> importPathResolver)
        throws IOException {
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
        writer.append(imports);
        if (!imports.isEmpty()) {
            writer.newLine();
            writer.newLine();
        }

    }

    public static void visitTypeInstance(final Set<TypeDefinition> knownTypes, final TypeInstanceDefinition instance) {
        knownTypes.add(instance.getTypeDefinition());
        instance.getGenericTypeArguments().forEach(i -> visitTypeInstance(knownTypes, i));
    }
}
