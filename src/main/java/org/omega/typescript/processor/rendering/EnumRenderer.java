package org.omega.typescript.processor.rendering;

import org.omega.typescript.processor.model.TypeDefinition;
import org.omega.typescript.processor.model.TypeKind;

import java.io.BufferedWriter;
import java.util.stream.Collectors;

/**
 * Created by kibork on 5/2/2018.
 */
public class EnumRenderer {

    // ------------------ Constants  --------------------

    // ------------------ Fields     --------------------

    private final RenderingContext context;

    // ------------------ Properties --------------------

    // ------------------ Logic      --------------------

    public EnumRenderer(RenderingContext context) {
        this.context = context;
    }

    public void renderEnum(final TypeDefinition enumDefinition) {
        if (enumDefinition.getTypeKind() != TypeKind.ENUM) {
            throw new IllegalArgumentException("Invalid renderer for type " + enumDefinition);
        }

        try (BufferedWriter writer = context.getStorageStrategy().createWriter(enumDefinition)) {
            writer.append(String.format("export enum %s {", enumDefinition.getTypeScriptName()));
            writer.newLine();
            final String body = enumDefinition.getEnumConstants()
                    .stream()
                    .map(c -> "\t" + c.getName() + " = '" + c.getName() + "'")
                    .collect(Collectors.joining(",\n"));
            if (!body.isEmpty()) {
                writer.append(body);
                writer.newLine();
            }
            writer.append("}");
            writer.newLine();
        } catch (Exception ex) {
            throw new RuntimeException("Failed to render interface for type " + enumDefinition.getFullName(), ex);
        }
    }
}
