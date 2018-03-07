package org.omega.typescript.processor.builders;

import org.omega.typescript.processor.ProcessingContext;
import org.omega.typescript.processor.model.MappingDefinition;
import org.omega.typescript.processor.utils.TypeUtils;
import org.springframework.core.annotation.AliasFor;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.lang.model.AnnotatedConstruct;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by kibork on 3/6/2018.
 */
public class MappingDefinitionBuilder {

    // ------------------ Constants  --------------------

    // ------------------ Fields     --------------------

    private final ProcessingContext context;

    // ------------------ Properties --------------------

    // ------------------ Logic      --------------------

    public MappingDefinitionBuilder(ProcessingContext context) {
        this.context = context;
    }

    /**
     * Reads mapping information from annotated elements classes or methods
     *
     * @param element type or method
     * @return mapping information detected
     */
    public Optional<MappingDefinition> build(final AnnotatedConstruct element) {
        //Annotations present on the element
        final List<? extends AnnotationMirror> annotationMirrors = TypeUtils.getAllAnnotations(element);

        final MappingDefinition result = new MappingDefinition();
        boolean found = false;

        for (final AnnotationMirror annotation : annotationMirrors) {
            if (RequestMapping.class.getName().equals(TypeUtils.getName(annotation))) {
                found = true;

                TypeUtils.getValue(annotation, "path", context).ifPresent(av -> readUrl(element, result, av));
                TypeUtils.getValue(annotation, "value", context).ifPresent(av -> readUrl(element, result, av));

                TypeUtils.getValue(annotation, "method", context).ifPresent(av -> readRequestMethod(element, result, av));
            } else {
                final Map<? extends ExecutableElement, ? extends AnnotationValue> values = context.getProcessingEnv()
                        .getElementUtils()
                        .getElementValuesWithDefaults(annotation);
                values.forEach((valueElement, value) -> {
                    final Optional<? extends AnnotationMirror> aliasForOption = TypeUtils.getAnnotation(valueElement, AliasFor.class.getName(), context);
                    if (aliasForOption.isPresent()) {
                        final AnnotationMirror aliasForMirror = aliasForOption.get();
                        final String targetClass = getAliasAnnotationClassName(aliasForMirror);

                        if (RequestMapping.class.getName().equals(targetClass)) {
                            processRequestMappingAlias(result, valueElement, value, aliasForMirror);
                        }
                    }
                });
            }

        }

        return !found ? Optional.empty() : Optional.of(result);
    }

    private String getAliasAnnotationClassName(AnnotationMirror aliasForMirror) {
        return TypeUtils.getValue(aliasForMirror, "annotation", context)
                .map(av -> TypeUtils.readClassAnnotationValue(av, context))
                .filter(t -> t instanceof DeclaredType)
                .map(t -> (DeclaredType)t)
                .map(DeclaredType::asElement)
                .filter(e -> e instanceof TypeElement)
                .map(te -> ((TypeElement)te).getQualifiedName().toString())
                .orElse("");
    }

    private void processRequestMappingAlias(MappingDefinition result, ExecutableElement valueElement, AnnotationValue value, AnnotationMirror aliasForMirror) {
        final String attribute = TypeUtils.getValue(aliasForMirror, "attribute", context)
            .map(av -> TypeUtils.readSimpleAnnotationValue(av, context))
            .orElse(valueElement.getSimpleName().toString());

        if ("path".equals(attribute)) {
            readUrl(valueElement, result, value);
        } else if ("value".equals(attribute)) {
            readUrl(valueElement, result, value);
        }

        if ("method".equals(attribute)) {
            readRequestMethod(valueElement, result, value);
        }
    }

    private void readRequestMethod(AnnotatedConstruct element, MappingDefinition result, AnnotationValue av) {
        final List<String> value = TypeUtils.readAnnotationValueList(av, context);
        if (value.size() > 0) {
            result.setRequestMethod(org.omega.typescript.processor.model.RequestMethod.valueOf(getValueFromList(element, value, " request mathod ")));
        }
    }

    private void readUrl(AnnotatedConstruct element, MappingDefinition result, AnnotationValue av) {
        final List<String> value = TypeUtils.readAnnotationValueList(av, context);
        if (value.size() > 0) {
            result.setUrlTemplate(getValueFromList(element, value, " path "));
        }
    }

    private <T> T getValueFromList(AnnotatedConstruct element, List<T> values, String name) {
        final T value = values.get(0);
        if (values.size() > 1) {
            context.warning(
                    "Request Mapping annotation value " + name + " derived from " + element.toString() + " has multiple values: " +
                            values.stream().map(v -> "" + v).collect(Collectors.joining(", ")) + ", using first value " +
                            value
            );
        }
        return value;
    }

}
