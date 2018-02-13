package org.omega.typescript.processor.def;

import org.omega.typescript.processor.ProcessingContext;
import org.omega.typescript.processor.utils.TypeUtils;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import java.util.List;
import java.util.Optional;

/**
 * Created by kibork on 2/5/2018.
 */
public abstract class AnnotationPrototype {

    // ------------------ Constants  --------------------

    // ------------------ Fields     --------------------

    private final String qualifiedName;

    // ------------------ Properties --------------------

    public String getQualifiedName() {
        return qualifiedName;
    }

    // ------------------ Logic      --------------------

    protected AnnotationPrototype(final String qualifiedName) {
        this.qualifiedName = qualifiedName;
    }

    public Optional<? extends AnnotationMirror> get(final Element element, final ProcessingContext context) {
        final Optional<? extends AnnotationMirror> annotation =
                TypeUtils.getAnnotation(element, qualifiedName, context);
        return annotation;
    }
}
