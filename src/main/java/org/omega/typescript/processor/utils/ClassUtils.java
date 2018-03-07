package org.omega.typescript.processor.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by kibork on 3/6/2018.
 */
public final class ClassUtils {

    // ------------------ Constants  --------------------

    // ------------------ Fields     --------------------

    // ------------------ Properties --------------------

    // ------------------ Logic      --------------------


    private ClassUtils() {
    }

    public static List<Annotation> getClassAnnottions(final AnnotatedElement element) {
        final Annotation[] annotations = element.getAnnotations();
        final List<Annotation> childAnnotations = Arrays.stream(annotations)
                .flatMap(a -> getClassAnnottions(a.getClass()).stream())
                .collect(Collectors.toList());
        return Stream.concat(Arrays.stream(annotations), childAnnotations.stream())
                .collect(Collectors.toList());
    }

}
