package org.omega.typescript.processor.def;

import org.omega.typescript.processor.ProcessingContext;
import org.omega.typescript.processor.model.RequestMethod;
import org.omega.typescript.processor.utils.TypeUtils;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by kibork on 2/5/2018.
 */
public final class RequestMappingAnnotation extends AnnotationPrototype {

    // ------------------ Constants  --------------------

    // ------------------ Fields     --------------------

    private final static RequestMappingAnnotation instance = new RequestMappingAnnotation();

    // ------------------ Properties --------------------

    public static RequestMappingAnnotation getInstance() {
        return instance;
    }


    // ------------------ Logic      --------------------

    private RequestMappingAnnotation() {
        super("org.springframework.web.bind.annotation.RequestMapping");
    }

    public Optional<List<RequestMethod>> method(final AnnotationMirror annotation, final ProcessingContext context) {
        final Optional<AnnotationValue> methodValue = TypeUtils.getValue(annotation, "method", context);
        return methodValue
                .map(annotationValue -> TypeUtils.getCompileTimeListValue(annotationValue, Object.class))
                .map(Object::toString)
                .map(RequestMethod::valueOf)
                .collect(Collectors.toList());

    }

    public Optional<List<String>> path(final AnnotationMirror annotation, final ProcessingContext context) {
        final Optional<AnnotationValue> path_ = TypeUtils.getValue(annotation, "path", context);
        final Optional<AnnotationValue> value_ = TypeUtils.getValue(annotation, "value", context);

        final Optional<AnnotationValue> path = path_.isPresent() ? path_ : value_;
        return path
                .map(annotationValue -> TypeUtils.getCompileTimeListValue(annotationValue, String.class));
    }
}
