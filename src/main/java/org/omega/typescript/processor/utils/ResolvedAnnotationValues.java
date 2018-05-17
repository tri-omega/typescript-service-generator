package org.omega.typescript.processor.utils;

import org.omega.typescript.processor.services.ProcessingContext;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * Created by kibork on 3/7/2018.
 */
public class ResolvedAnnotationValues {

    // ------------------ Constants  --------------------

    // ------------------ Fields     --------------------

    private final String className;

    private final Map<String, AnnotationValue> valueMap = new HashMap<>();

    // ------------------ Properties --------------------

    public String getClassName() {
        return className;
    }

    public Map<String, AnnotationValue> getValueMap() {
        return valueMap;
    }


    // ------------------ Logic      --------------------

    public ResolvedAnnotationValues(final String className) {
        this.className = className;
    }

    public boolean isTargetClass(final String candidateClass) {
        return StringUtils.equals(className, candidateClass);
    }

    public boolean isTargetClass(final AnnotationMirror annotationMirror) {
        return StringUtils.equals(className, AnnotationUtils.getName(annotationMirror));
    }

    public Optional<AnnotationValue> getValue(final String property) {
        return Optional.ofNullable(valueMap.get(property));
    }

    public <T> T parseValue(final String property, final Function<String, T> parser, final T defaultValue, final ProcessingContext context) {
        return getValue(property)
                .map(av -> AnnotationUtils.readSimpleAnnotationValue(av, context))
                .map(parser)
                .orElse(defaultValue);
    }

    public Boolean readBoolean(final String property, Boolean defaultValue, final ProcessingContext context) {
        return parseValue(property, Boolean::parseBoolean, defaultValue, context);
    }


    public String readString(final String property, String defaultValue, final ProcessingContext context) {
        return parseValue(property, s -> s, defaultValue, context);
    }
}
