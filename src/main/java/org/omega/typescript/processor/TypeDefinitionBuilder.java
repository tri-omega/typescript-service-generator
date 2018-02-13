package org.omega.typescript.processor;

import org.omega.typescript.processor.model.DefinitionId;
import org.omega.typescript.processor.model.TypeDefinition;

import javax.lang.model.element.TypeElement;
import java.util.Optional;

/**
 * Created by kibork on 1/23/2018.
 */
public class TypeDefinitionBuilder {

    // ------------------ Constants  --------------------

    // ------------------ Fields     --------------------

    // ------------------ Properties --------------------

    // ------------------ Logic      --------------------

    public static TypeDefinition buildClassDefinition(final TypeElement type, final ProcessingContext context) {
        final String className = type.getQualifiedName().toString().intern();
        synchronized (className) {
            final Optional<TypeDefinition> optionalDefinition = context.getTypeOracle().getType(className);
            if (optionalDefinition.isPresent()) {
                return optionalDefinition.get();
            }

            final TypeDefinition typeDefinition = context.getTypeOracle().create(type, context);

            return typeDefinition;
        }
    }

    public static void initializeType(final TypeDefinition definition, final ProcessingContext context) {
        if (definition.isInitialized()) {
            return;
        }
        final TypeElement typeElement = definition.getDefinitionId().getTypeElement();
//        context.getProcessingEnv().getElementUtils().getAllMembers();
    }


}
