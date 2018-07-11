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

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * Created by kibork on 3/7/2018.
 */
public class ResolvedAnnotationValues {

    // ------------------ Constants  --------------------

    // ------------------ Fields     --------------------

    private final String className;

    private final Map<String, AnnotationValue> valueMap = new HashMap<>();

    // ------------------ Properties --------------------

    public String getClassName() {
        return className;
    }

    public Map<String, AnnotationValue> getValueMap() {
        return valueMap;
    }


    // ------------------ Logic      --------------------

    public ResolvedAnnotationValues(final String className) {
        this.className = className;
    }

    public boolean isTargetClass(final String candidateClass) {
        return StringUtils.equals(className, candidateClass);
    }

    public boolean isTargetClass(final AnnotationMirror annotationMirror) {
        return StringUtils.equals(className, AnnotationUtils.getName(annotationMirror));
    }

    public Optional<AnnotationValue> getValue(final String property) {
        return Optional.ofNullable(valueMap.get(property));
    }

    public <T> T parseValue(final String property, final Function<String, T> parser, final T defaultValue, final ProcessingContext context) {
        return getValue(property)
                .map(av -> AnnotationUtils.readSimpleAnnotationValue(av, context))
                .map(parser)
                .orElse(defaultValue);
    }

    public Boolean readBoolean(final String property, Boolean defaultValue, final ProcessingContext context) {
        return parseValue(property, Boolean::parseBoolean, defaultValue, context);
    }


    public String readString(final String property, String defaultValue, final ProcessingContext context) {
        return parseValue(property, s -> s, defaultValue, context);
    }
}
