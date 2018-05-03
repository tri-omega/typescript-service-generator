package org.omega.typescript.processor.rendering;

import org.omega.typescript.processor.model.TypeDefinition;
import org.omega.typescript.processor.model.TypeInstanceDefinition;
import org.omega.typescript.processor.model.TypeKind;

import java.io.BufferedWriter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by kibork on 4/24/2018.
 */
public class InterfaceRenderer {

    // ------------------ Constants  --------------------

    // ------------------ Fields     --------------------

    private final RenderingContext context;

    // ------------------ Properties --------------------

    // ------------------ Logic      --------------------


    public InterfaceRenderer(final RenderingContext context) {
        this.context = context;
    }

    public void renderInterface(final TypeDefinition definition) {
        if (definition.getTypeKind() != TypeKind.INTERFACE) {
            throw new IllegalArgumentException("Invalid renderer for type " + definition);
        }

        try (BufferedWriter writer = context.getStorageStrategy().createWriter(definition)) {
            renderImports(definition, writer);
            renderBody(definition, writer);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to render interface for type " + definition.getFullName(), ex);
        }
    }

    private void renderBody(final TypeDefinition definition, final BufferedWriter writer) throws Exception {
        writer.append("export interface ")
                .append(definition.getTypeScriptName())
                .append(getGenericDecl(definition))
                .append(getExtendsDecl(definition.getSuperTypes(), ", "))
                .append(" {");
        writer.newLine();
        final TypeInstanceRenderer instanceRenderer = context.getInstanceRenderer();

        final String properties = definition.getProperties().stream()
                .map(p -> "\t" + p.getName() + ": " + instanceRenderer.renderTypeInstance(p.getType()) + ";")
                .collect(Collectors.joining("\n"));
        if (!properties.isEmpty()) {
            writer.append(properties);
            writer.newLine();
        }
        writer.append("}");
        writer.newLine();
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
        final TypeInstanceRenderer instanceRenderer = context.getInstanceRenderer();
        return " extends " + superTypes.stream()
                .map(instanceRenderer::renderTypeInstance)
                .collect(Collectors.joining(delimiter));
    }

    private void renderImports(final TypeDefinition definition, final BufferedWriter writer) throws Exception {
        final Set<TypeDefinition> usedTypes = new HashSet<>();
        definition.getProperties().forEach(p -> visitTypeInstance(usedTypes, p.getType()));
        definition.getSuperTypes().forEach(i -> visitTypeInstance(usedTypes, i));
        definition.getGenericTypeParams().stream()
            .flatMap(gp -> gp.getSuperTypes().stream())
            .forEach(i -> visitTypeInstance(usedTypes, i));


        final List<TypeDefinition> importTypes = usedTypes.stream()
                .filter(Objects::nonNull)
                .filter(t -> !t.isPredefined())
                .filter(t -> t.getTypeKind() == TypeKind.INTERFACE || t.getTypeKind() == TypeKind.ENUM)
                .sorted(Comparator.comparing(TypeDefinition::getTypeScriptName))
                .distinct()
                .collect(Collectors.toList());

        final String imports = importTypes.stream()
                .map(t -> "import {" + t.getTypeScriptName() + "} from '" + context.getStorageStrategy().getRelativeFileName(definition, t) + "';")
                .collect(Collectors.joining("\n"));
        writer.append(imports);
        if (!imports.isEmpty()) {
            writer.newLine();
            writer.newLine();
        }
    }

    private void visitTypeInstance(final Set<TypeDefinition> knownTypes, final TypeInstanceDefinition instance) {
        knownTypes.add(instance.getTypeDefinition());
        instance.getGenericTypeArguments().forEach(i -> visitTypeInstance(knownTypes, i));
    }
}
