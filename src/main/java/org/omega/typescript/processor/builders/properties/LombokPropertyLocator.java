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

package org.omega.typescript.processor.builders.properties;

import org.omega.typescript.api.TypeScriptIgnore;
import org.omega.typescript.processor.services.ProcessingContext;
import org.omega.typescript.processor.model.PropertyDefinition;
import org.omega.typescript.processor.utils.AnnotationUtils;
import org.omega.typescript.processor.utils.ResolvedAnnotationValues;
import org.omega.typescript.processor.utils.TypeUtils;

import javax.lang.model.element.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by kibork on 5/9/2018.
 */
public class LombokPropertyLocator implements TypePropertyLocator {

    // ---------------- Fields & Constants --------------

    // ------------------ Properties --------------------

    // ------------------ Logic      --------------------


    @Override
    public List<PropertyDefinition> locateProperties(final TypeElement typeElement, final ProcessingContext context) {
        final Optional<ResolvedAnnotationValues> dataAnnotation = AnnotationUtils.resolveAnnotation("lombok.Data", typeElement, context);
        final boolean allInstanceFields = dataAnnotation.isPresent();

        final List<Element> fields = TypeUtils.getMembers(typeElement, ElementKind.FIELD, context).stream()
                .filter(e -> e.getEnclosingElement() == typeElement)
                .filter(e -> !e.getModifiers().contains(Modifier.TRANSIENT))
                .filter(e -> !AnnotationUtils.getAnnotation(e, TypeScriptIgnore.class).isPresent())
                .filter(e -> !e.getModifiers().contains(Modifier.STATIC))
                .collect(Collectors.toList());

        return fields.stream()
                .filter(f -> allInstanceFields || hasGetter(f, context))
                .map(f -> PropertyHelpers.buildProperty(f, f.getSimpleName().toString(), f.asType(), context))
                .collect(Collectors.toList());
    }

    private boolean hasGetter(final Element field, final ProcessingContext context) {
        final Optional<ResolvedAnnotationValues> getterAnnotation = AnnotationUtils.resolveAnnotation("lombok.Getter", field, context);
        return getterAnnotation.isPresent();
    }
}
