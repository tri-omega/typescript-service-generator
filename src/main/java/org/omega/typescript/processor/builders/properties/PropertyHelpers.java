package org.omega.typescript.processor.builders.properties;

import org.omega.typescript.api.TypeScriptName;
import org.omega.typescript.processor.ProcessingContext;
import org.omega.typescript.processor.model.PropertyDefinition;
import org.omega.typescript.processor.utils.AnnotationUtils;
import org.omega.typescript.processor.utils.StringUtils;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;
import java.util.Optional;

/**
 * Created by kibork on 5/9/2018.
 */
public final class PropertyHelpers {

    // ---------------- Fields & Constants --------------

    // ------------------ Properties --------------------

    // ------------------ Logic      --------------------


    private PropertyHelpers() {
    }

    static String getTypeScriptName(final Element getter, final String defaultName, final ProcessingContext context) {
        final Optional<? extends AnnotationMirror> customName = AnnotationUtils.getAnnotation(getter, TypeScriptName.class);
        return customName
                .flatMap(am -> AnnotationUtils.getValue(am, "value", context))
                .map(av -> AnnotationUtils.readSimpleAnnotationValue(av, context))
                .flatMap(name -> StringUtils.hasText(name) ? Optional.of(name) : Optional.empty())
                .orElse(defaultName);
    }

    public static PropertyDefinition buildProperty(final Element getter, final String defaultName, final TypeMirror returnType, final ProcessingContext context) {
        final PropertyDefinition property = new PropertyDefinition();
        property.setName(getTypeScriptName(getter, defaultName, context));
        property.setType(context.getTypeOracle().buildInstance(returnType));
        return property;
    }
}
