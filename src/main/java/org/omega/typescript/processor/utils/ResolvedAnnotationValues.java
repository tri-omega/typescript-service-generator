package org.omega.typescript.processor.utils;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
}
