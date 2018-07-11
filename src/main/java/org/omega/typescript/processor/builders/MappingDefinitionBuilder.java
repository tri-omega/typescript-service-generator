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

package org.omega.typescript.processor.builders;

import org.omega.typescript.processor.model.MappingDefinition;
import org.omega.typescript.processor.services.ProcessingContext;
import org.omega.typescript.processor.utils.AnnotationUtils;
import org.omega.typescript.processor.utils.ResolvedAnnotationValues;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.lang.model.AnnotatedConstruct;
import javax.lang.model.element.AnnotationValue;
import java.util.List;
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
        final Optional<ResolvedAnnotationValues> foundAnnotation = AnnotationUtils.resolveAnnotation(RequestMapping.class, element, context);
        if (!foundAnnotation.isPresent()) {
            return Optional.empty();
        }

        final MappingDefinition result = new MappingDefinition();

        final ResolvedAnnotationValues annotationValues = foundAnnotation.get();
        annotationValues.getValue("path").ifPresent(av -> readUrl(element, result, av));

        annotationValues.getValue("method").ifPresent(av -> readRequestMethod(element, result, av));

        return Optional.of(result);
    }

    private void readRequestMethod(AnnotatedConstruct element, MappingDefinition result, AnnotationValue av) {
        final List<String> value = AnnotationUtils.readAnnotationValueList(av, context);
        if (value.size() > 0) {
            result.setRequestMethod(org.omega.typescript.processor.model.RequestMethod.valueOf(getValueFromList(element, value, " request mathod ")));
        }
    }

    private void readUrl(AnnotatedConstruct element, MappingDefinition result, AnnotationValue av) {
        final List<String> value = AnnotationUtils.readAnnotationValueList(av, context);
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
