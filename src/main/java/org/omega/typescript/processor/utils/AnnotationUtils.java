package org.omega.typescript.processor.utils;

import org.omega.typescript.processor.ProcessingContext;
import org.springframework.core.annotation.AliasFor;

import javax.lang.model.AnnotatedConstruct;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by kibork on 3/7/2018.
 */
public final class AnnotationUtils {

    // ------------------ Constants  --------------------

    // ------------------ Fields     --------------------

    // ------------------ Properties --------------------

    // ------------------ Logic      --------------------


    private AnnotationUtils() {
    }

    public static Optional<ResolvedAnnotationValues> resolveAnnotation(final Class<?> clazz, final AnnotatedConstruct element, final ProcessingContext context) {
        return resolveAnnotation(clazz.getName(), element, context);
    }

    public static Optional<ResolvedAnnotationValues> resolveAnnotation(final String expectedClassName, final AnnotatedConstruct element, final ProcessingContext context) {
        final List<? extends AnnotationMirror> annotationMirrors = getAllAnnotations(element);
        if (annotationMirrors.isEmpty()) {
            return Optional.empty();
        }

        boolean found = false;
        final ResolvedAnnotationValues result = new ResolvedAnnotationValues(expectedClassName);

        for (final AnnotationMirror annotation : annotationMirrors) {
            final Map<? extends ExecutableElement, ? extends AnnotationValue> values = context.getProcessingEnv()
                    .getElementUtils()
                    .getElementValuesWithDefaults(annotation);

            if (result.isTargetClass(annotation)) {
                found = true;
                values.forEach((property, value) ->
                        result.getValueMap().putIfAbsent(getAnnotationPropertyName(property), value)
                );

            } else {
                found |= checkForAliasedProperties(context, result, values);
            }

        }

        return !found ? Optional.empty() : Optional.of(result);

    }

    private static boolean checkForAliasedProperties(ProcessingContext context, ResolvedAnnotationValues result, Map<? extends ExecutableElement, ? extends AnnotationValue> values) {
        final AtomicBoolean found = new AtomicBoolean(false);
        values.forEach((valueElement, value) -> {
            //How deep does the rabbit hole go? Can you create meta annotations for AliasFor? Who knows...
            final Optional<? extends AnnotationMirror> aliasForOption = getAnnotation(valueElement, AliasFor.class.getName(), context);
            if (aliasForOption.isPresent()) {
                final boolean isMatch = processAliasedProperty(context, result, valueElement, value, found, aliasForOption.get());
                if (isMatch) {
                    found.set(true);
                }
            }
        });
        return found.get();
    }

    private static boolean processAliasedProperty(ProcessingContext context, ResolvedAnnotationValues result, ExecutableElement valueElement, AnnotationValue value, AtomicBoolean found, AnnotationMirror aliasForMirror) {
        final String targetClass = getAliasAnnotationClassName(aliasForMirror, context);

        if (result.isTargetClass(targetClass)) {
            final String attribute = getValue(aliasForMirror, "attribute", context)
                    .map(av -> readSimpleAnnotationValue(av, context))
                    .orElse(getAnnotationPropertyName(valueElement));

            result.getValueMap().putIfAbsent(attribute, value);
            return true;
        } else {
            return false;
        }
    }

    private static String getAnnotationPropertyName(ExecutableElement property) {
        return property.getSimpleName().toString();
    }


    private static String getAliasAnnotationClassName(final AnnotationMirror aliasForMirror, final ProcessingContext context) {
        return getValue(aliasForMirror, "annotation", context)
                .map(av -> readClassAnnotationValue(av, context))
                .filter(t -> t instanceof DeclaredType)
                .map(t -> (DeclaredType)t)
                .map(DeclaredType::asElement)
                .filter(e -> e instanceof TypeElement)
                .map(te -> ((TypeElement)te).getQualifiedName().toString())
                .orElse("");
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

    public static List<? extends AnnotationMirror> getAllAnnotations(final AnnotatedConstruct annotatedConstruct) {
        final List<? extends AnnotationMirror> directAnnotations;
        if (annotatedConstruct instanceof DeclaredType) {
            final DeclaredType declaredType = (DeclaredType) annotatedConstruct;
            final QualifiedNameable element = (QualifiedNameable)declaredType.asElement();
            if (element.getQualifiedName().toString().startsWith("java.lang.annotation.")) {
                return Collections.emptyList();
            }
            directAnnotations = element.getAnnotationMirrors();
        } else {
            directAnnotations = annotatedConstruct.getAnnotationMirrors();
        }
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

    public static TypeMirror readClassAnnotationValue(final AnnotationValue av, final ProcessingContext context) {
        final Object value = av.getValue();
        if (!(value instanceof TypeMirror)) {
            return error(context, "Unable to read " + av + " as Class Name Value");
        } else {
            return (TypeMirror) value;
        }
    }

    private static <T> T error(ProcessingContext context, String msg) {
        context.error(msg);
        throw new IllegalArgumentException(msg);
    }
}
