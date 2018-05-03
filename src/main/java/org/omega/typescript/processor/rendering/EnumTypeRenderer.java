package org.omega.typescript.processor.rendering;

import org.omega.typescript.processor.model.TypeDefinition;
import org.omega.typescript.processor.model.TypeKind;

import java.io.BufferedWriter;
import java.util.stream.Collectors;

/**
 * Created by kibork on 5/2/2018.
 */
public class EnumTypeRenderer extends BaseTypeRenderer {

    // ------------------ Constants  --------------------

    // ------------------ Fields     --------------------

    // ------------------ Properties --------------------

    // ------------------ Logic      --------------------

    public EnumTypeRenderer(final RenderingContext context) {
        super(context);
    }

    @Override
    public TypeKind getSupportedDefinitionType() {
        return TypeKind.ENUM;
    }

    @Override
    protected void renderBody(final TypeDefinition enumDefinition, final BufferedWriter writer) throws Exception {
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
    }

    @Override
    protected void renderImports(final TypeDefinition definition, final BufferedWriter writer)  {

    }
}
