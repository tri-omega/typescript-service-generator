/*
 * Copyright (c) 2018 William Frank (info@williamfrank.net)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.omega.typescript.processor.utils;

import org.omega.typescript.processor.services.ProcessingContext;
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

        final TypeElement targetAnnotationMirror = context.getProcessingEnv().getElementUtils()
                .getTypeElement(expectedClassName);

        for (final AnnotationMirror annotation : annotationMirrors) {
            final Map<? extends ExecutableElement, ? extends AnnotationValue> values = annotation.getElementValues();
                    //context.getProcessingEnv().getElementUtils().getElementValuesWithDefaults(annotation); //This doesn't play nice with aliasing...

            if (result.isTargetClass(annotation)) {
                found = true;
                values.forEach((property, value) ->
                        setPropertyAndAliases(context, result, property, value, null, targetAnnotationMirror));
            } else {
                found |= checkForAliasedProperties(context, result, values, false, targetAnnotationMirror);
            }

        }

        return !found ? Optional.empty() : Optional.of(result);
    }

    private static void setPropertyAndAliases(final ProcessingContext context, final ResolvedAnnotationValues result,
              final ExecutableElement valueElement, final AnnotationValue value, final AnnotationMirror aliasForMirror, final TypeElement targetAnnotationType) {
        final String propertyName = getAliasedPropName(context, valueElement, aliasForMirror);

        final boolean newValue = result.getValueMap()
                .putIfAbsent(propertyName, value) == null;

        if (!newValue) {
            return;
        }

        //Check if the target property has an alias
        final ExecutableElement targetProperyElement = targetAnnotationType.getEnclosedElements().stream()
                .filter(e -> propertyName.equals(e.getSimpleName().toString()))
                .filter(e -> e instanceof ExecutableElement)
                .map(e -> (ExecutableElement)e)
                .findFirst().orElse(null);

        if (targetProperyElement == null) {
            return;
        }
        checkPropertyForAlias(context, result, targetProperyElement, value, true, targetAnnotationType);
    }

    private static boolean checkForAliasedProperties(ProcessingContext context, ResolvedAnnotationValues result,
                                                     Map<? extends ExecutableElement, ? extends AnnotationValue> values,
                                                     boolean localToClass, TypeElement targetAnnotationMirror) {
        final AtomicBoolean found = new AtomicBoolean(false);
        values.forEach((valueElement, value) -> {
            //How deep does the rabbit hole go? Can you create meta annotations for AliasFor? Who knows...
            final boolean hasFound = checkPropertyForAlias(context, result, valueElement, value, localToClass, targetAnnotationMirror);
            if (hasFound) {
                found.set(true);
            }
        });
        return found.get();
    }

    private static boolean checkPropertyForAlias(ProcessingContext context, ResolvedAnnotationValues result, ExecutableElement valueElement,
                                                 AnnotationValue value, boolean localToClass, final TypeElement targetAnnotationMirror) {
        final Optional<? extends AnnotationMirror> aliasForOption = getAnnotation(valueElement, AliasFor.class.getName());
        return aliasForOption
                .filter(annotationMirror -> processAliasedProperty(context, result, valueElement, value, annotationMirror, localToClass, targetAnnotationMirror))
                .isPresent();
    }

    private static boolean processAliasedProperty(ProcessingContext context, ResolvedAnnotationValues result,
                                                  ExecutableElement valueElement, AnnotationValue value, AnnotationMirror aliasForMirror,
                                                  final boolean localToClass, final  TypeElement targetAnnotationMirror) {
        final String targetClass = getAliasAnnotationClassName(aliasForMirror, context);

        if ((result.isTargetClass(targetClass)) | (((!StringUtils.hasText(targetClass)) && (localToClass)))) {
            setPropertyAndAliases(context, result, valueElement, value, aliasForMirror, targetAnnotationMirror);
            return true;
        } else {
            return false;
        }
    }

    private static String getAliasedPropName(ProcessingContext context, ExecutableElement valueElement, AnnotationMirror aliasForMirror) {
        if (aliasForMirror == null) {
            return getAnnotationPropertyName(valueElement);
        }
        final String attribute = getValue(aliasForMirror, "attribute", context)
                .map(av -> readSimpleAnnotationValue(av, context)).orElse(null);
        final String valueAttribute = getValue(aliasForMirror, "value", context)
                .map(av -> readSimpleAnnotationValue(av, context)).orElse(null);

        if (StringUtils.hasText(attribute)) {
            return attribute;
        } else if (StringUtils.hasText(valueAttribute)) {
            return valueAttribute;
        }
        
        return getAnnotationPropertyName(valueElement);
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

    public static Optional<? extends AnnotationMirror> getAnnotation(final AnnotatedConstruct element, final String annotationType) {
        final List<? extends AnnotationMirror> annotationMirrors = element.getAnnotationMirrors();
        return annotationMirrors.stream()
                .filter(am -> annotationType.equals(getName(am)))
                .findFirst();
    }

    public static Optional<? extends AnnotationMirror> getAnnotation(final AnnotatedConstruct element, final Class<?> annotationType) {
        return getAnnotation(element, annotationType.getName());
    }

    public static String getName(final AnnotationMirror am) {
        final DeclaredType annotationType = am.getAnnotationType();
        final Element element = annotationType.asElement();
        if (element.getKind() == ElementKind.ANNOTATION_TYPE) {
            return ((QualifiedNameable) element).getQualifiedName().toString();
        } else if  (element.getKind() == ElementKind.CLASS) {
            //User annotation without a loaded definition. Let if be someone else issue
            return null;
        } else {
            throw new IllegalArgumentException("Annotation mirror " + am + " has no annotation type, kind = " + element.getKind());
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
