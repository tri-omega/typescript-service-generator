package org.omega.typescript.processor.builders.properties;

import org.omega.typescript.api.TypeScriptIgnore;
import org.omega.typescript.processor.services.ProcessingContext;
import org.omega.typescript.processor.model.PropertyDefinition;
import org.omega.typescript.processor.utils.AnnotationUtils;
import org.omega.typescript.processor.utils.ResolvedAnnotationValues;
import org.omega.typescript.processor.utils.TypeUtils;

import javax.lang.model.element.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by kibork on 5/9/2018.
 */
public class LombokPropertyLocator implements TypePropertyLocator {

    // ---------------- Fields & Constants --------------

    // ------------------ Properties --------------------

    // ------------------ Logic      --------------------


    @Override
    public List<PropertyDefinition> locateProperties(final TypeElement typeElement, final ProcessingContext context) {
        final Optional<ResolvedAnnotationValues> dataAnnotation = AnnotationUtils.resolveAnnotation("lombok.Data", typeElement, context);
        final boolean allInstanceFields = dataAnnotation.isPresent();

        final List<Element> fields = TypeUtils.getMembers(typeElement, ElementKind.FIELD, context).stream()
                .filter(e -> e.getEnclosingElement() == typeElement)
                .filter(e -> !e.getModifiers().contains(Modifier.TRANSIENT))
                .filter(e -> !AnnotationUtils.getAnnotation(e, TypeScriptIgnore.class).isPresent())
                .filter(e -> !e.getModifiers().contains(Modifier.STATIC))
                .collect(Collectors.toList());

        return fields.stream()
                .filter(f -> allInstanceFields || hasGetter(f, context))
                .map(f -> PropertyHelpers.buildProperty(f, f.getSimpleName().toString(), f.asType(), context))
                .collect(Collectors.toList());
    }

    private boolean hasGetter(final Element field, final ProcessingContext context) {
        final Optional<ResolvedAnnotationValues> getterAnnotation = AnnotationUtils.resolveAnnotation("lombok.Getter", field, context);
        return getterAnnotation.isPresent();
    }
}
