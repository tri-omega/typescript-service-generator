package org.omega.typescript.processor.builders;

import org.omega.typescript.processor.ProcessingContext;
import org.omega.typescript.processor.model.MappingDefinition;
import org.omega.typescript.processor.utils.TypeUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.lang.model.AnnotatedConstruct;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.TypeElement;
import java.util.List;
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
     * @param element type or method
     * @return mapping information detected
     */
    public MappingDefinition build(final AnnotatedConstruct element) {
        //Annotations present on the element
        final List<? extends AnnotationMirror> annotationMirrors = TypeUtils.getAllAnnotations(element);

        final MappingDefinition result = new MappingDefinition();

        for (final AnnotationMirror annotation : annotationMirrors) {
            final TypeElement annotationElement = (TypeElement) annotation.getAnnotationType().asElement();
            if (RequestMapping.class.getName().equals(TypeUtils.getName(annotation))) {
                TypeUtils.getValue(annotation, "path", context).ifPresent(av -> readUrl(element, result, av));
                TypeUtils.getValue(annotation, "value", context).ifPresent(av -> readUrl(element, result, av));

                TypeUtils.getValue(annotation, "method", context).ifPresent(av -> readRequestMethod(element, result, av));
            }

        }

        return result;
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
