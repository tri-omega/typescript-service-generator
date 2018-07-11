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

import org.omega.typescript.api.TypeScriptName;
import org.omega.typescript.processor.services.ProcessingContext;
import org.omega.typescript.processor.model.PropertyDefinition;
import org.omega.typescript.processor.utils.AnnotationUtils;
import org.omega.typescript.processor.utils.StringUtils;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;
import java.util.Optional;

/**
 * Created by kibork on 5/9/2018.
 */
public final class PropertyHelpers {

    // ---------------- Fields & Constants --------------

    // ------------------ Properties --------------------

    // ------------------ Logic      --------------------


    private PropertyHelpers() {
    }

    static String getTypeScriptName(final Element getter, final String defaultName, final ProcessingContext context) {
        final Optional<? extends AnnotationMirror> customName = AnnotationUtils.getAnnotation(getter, TypeScriptName.class);
        return customName
                .flatMap(am -> AnnotationUtils.getValue(am, "value", context))
                .map(av -> AnnotationUtils.readSimpleAnnotationValue(av, context))
                .flatMap(name -> StringUtils.hasText(name) ? Optional.of(name) : Optional.empty())
                .orElse(defaultName);
    }

    public static PropertyDefinition buildProperty(final Element getter, final String defaultName, final TypeMirror returnType, final ProcessingContext context) {
        final PropertyDefinition property = new PropertyDefinition();
        property.setName(getTypeScriptName(getter, defaultName, context));
        property.setType(context.getTypeOracle().buildInstance(returnType));
        return property;
    }
}
