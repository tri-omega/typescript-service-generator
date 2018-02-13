package org.omega.typescript.processor.utils;

import org.omega.typescript.processor.ProcessingContext;

import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by kibork on 2/2/2018.
 */
public final class TypeUtils {

    // ------------------ Constants  --------------------

    // ------------------ Fields     --------------------

    // ------------------ Properties --------------------

    // ------------------ Logic      --------------------


    private TypeUtils() {
    }

    public static List<Element> getMembers(final TypeElement typeElement, final ElementKind kind, final ProcessingContext context) {
        return context.getProcessingEnv().getElementUtils().getAllMembers(typeElement).stream()
                .filter(el -> el.getKind() == kind)
                .collect(Collectors.toList());
    }

    public static Optional<? extends AnnotationMirror> getAnnotation(final Element element, final String annotationType, final ProcessingContext context) {
        final List<? extends AnnotationMirror> annotationMirrors = element.getAnnotationMirrors();
        return annotationMirrors.stream()
                .filter(am -> annotationType.equals(getName(am)))
                .findFirst();
    }

    public static String getName(final AnnotationMirror am) {
        final DeclaredType annotationType = am.getAnnotationType();
        final Element element = annotationType.asElement();
        if (element.getKind() == ElementKind.ANNOTATION_TYPE) {
            return ((TypeElement) element).getQualifiedName().toString();
        } else {
            throw new IllegalArgumentException("Annotation mirror " + am + " has no annotation type");
        }
    }

    public static Optional<AnnotationValue> getValue(final AnnotationMirror am, final String property, final ProcessingContext context) {
        final Map<? extends ExecutableElement, ? extends AnnotationValue> values = am.getElementValues();
        return values.entrySet().stream()
                .filter(e ->  (property.contentEquals(e.getKey().getSimpleName().toString())))
                .map(e -> (AnnotationValue)e.getValue())
            .findFirst()
        ;
    }

    public static List<ExecutableElement> getPropertyGetters(final TypeElement typeElement, final ProcessingContext context) {
        final List<ExecutableElement> methods = TypeUtils.getMembers(typeElement, ElementKind.METHOD, context).stream()
                .map(m -> (ExecutableElement) m)
                .collect(Collectors.toList());

        final List<ExecutableElement> result = methods.stream()
                .filter(m -> m.getSimpleName().toString().startsWith("get") || m.getSimpleName().toString().startsWith("is"))
                .collect(Collectors.toList());

        return result;
    }

    public static <T> List<T> getCompileTimeListValue(final AnnotationValue compileValue, final Class<T> clazz) {
        final Object value = compileValue.getValue();
        if (value instanceof Collection) {
            final Collection<Object> col = (Collection)value;
            return col.stream()
                    .filter(v -> clazz.isAssignableFrom(v.getClass()))
                    .map(v -> (T)v)
                    .collect(Collectors.toList());

        } else if (clazz.isAssignableFrom(value.getClass())) {
            return Collections.singletonList((T)value);
        }
    }

}

