package org.omega.typescript.processor.emitters;

import org.omega.typescript.processor.model.TypeDefinition;
import org.omega.typescript.processor.model.TypeKind;

import java.io.PrintWriter;
import java.util.stream.Collectors;

/**
 * Created by kibork on 5/2/2018.
 */
public class EnumTypeEmitter extends BaseTypeEmitter {

    // ------------------ Constants  --------------------

    // ------------------ Fields     --------------------

    // ------------------ Properties --------------------

    // ------------------ Logic      --------------------

    public EnumTypeEmitter(final EmitContext context) {
        super(context);
    }

    @Override
    public TypeKind getSupportedDefinitionType() {
        return TypeKind.ENUM;
    }

    @Override
    protected void renderBody(final TypeDefinition enumDefinition, final PrintWriter writer) {
        writer.printf("export enum %s {\n", enumDefinition.getTypeScriptName());
        final String body = enumDefinition.getEnumConstants()
                .stream()
                .map(c -> "\t" + c.getName() + " = '" + c.getName() + "'")
                .collect(Collectors.joining(",\n"));
        if (!body.isEmpty()) {
            writer.println(body);
        }
        writer.println("}");
    }

    @Override
    protected void renderImports(final TypeDefinition definition, final PrintWriter writer)  {

    }
}
