package org.omega.typescript.processor.utils;

import org.omega.typescript.processor.ProcessingContext;

import javax.lang.model.AnnotatedConstruct;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public static Optional<? extends AnnotationMirror> getAnnotation(final AnnotatedConstruct element, final String annotationType, final ProcessingContext context) {
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

        return methods.stream()
                .filter(m -> m.getSimpleName().toString().startsWith("get") || m.getSimpleName().toString().startsWith("is"))
                .collect(Collectors.toList());
    }

    public static List<? extends AnnotationMirror> getAllAnnotations(final AnnotatedConstruct annotatedConstruct) {
        final List<? extends AnnotationMirror> directAnnotations = annotatedConstruct.getAnnotationMirrors();
        final List<? extends AnnotationMirror> derevedAnnotations = directAnnotations.stream()
                .flatMap(am -> getAllAnnotations(am.getAnnotationType()).stream())
                .collect(Collectors.toList());
        return Stream.concat(directAnnotations.stream(), derevedAnnotations.stream())
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    public static List<String> readAnnotationValueList(final AnnotationValue av, final ProcessingContext context) {
        if (!(av.getValue() instanceof List)) {
            final String msg = "Unable to read " + av + " as annotation value list";
            return error(context, msg);
        }
        final Collection<AnnotationValue> values = (List<AnnotationValue>)av.getValue();
        return values.stream()
                .map(v -> readSimpleAnnotationValue(v, context))
                .collect(Collectors.toList());
    }

    public static String readSimpleAnnotationValue(final AnnotationValue av, final ProcessingContext context) {
        final Object value = av.getValue();
        if ((value instanceof List) || (value instanceof TypeMirror)) {
            error(context, "Unable to read " + av + " as simple value");
        }
        if (value instanceof VariableElement) {
            final Element enumValue = (VariableElement)value;
            return "" + enumValue.getSimpleName();
        }
        return "" + value;
    }

    private static List<String> error(ProcessingContext context, String msg) {
        context.error(msg);
        throw new IllegalArgumentException(msg);
    }
}

