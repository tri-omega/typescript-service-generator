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
import org.omega.typescript.processor.utils.TypeUtils;

import javax.lang.model.element.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by kibork on 5/9/2018.
 */
public class JavaBeanPropertyLocator implements TypePropertyLocator {

    // ---------------- Fields & Constants --------------

    public static final List<String> PROPERTY_PREFIXES = Arrays.asList("get", "is");

    // ------------------ Properties --------------------

    // ------------------ Logic      --------------------

    @Override
    public List<PropertyDefinition> locateProperties(final TypeElement typeElement, final ProcessingContext context) {
        final List<ExecutableElement> methods = TypeUtils.getMethods(typeElement, context);

        //Selects only "own" getters of the class
        final List<ExecutableElement> getters = methods.stream()
                .filter(e -> e.getEnclosingElement() == typeElement)
                .filter(e -> e.getModifiers().contains(Modifier.PUBLIC))
                .filter(e -> !e.getModifiers().contains(Modifier.TRANSIENT))
                .filter(e -> !AnnotationUtils.getAnnotation(e, TypeScriptIgnore.class).isPresent())
                .filter(this::isGetter)
                .collect(Collectors.toList());

        return getters.stream()
                .map(getter -> PropertyHelpers.buildProperty(
                            getter, buildPropertyName(getter, context), getter.getReturnType(), context
                        )
                )
                .collect(Collectors.toList());

    }

    private boolean isGetter(final ExecutableElement e) {
        if (!e.getParameters().isEmpty()) {
            return false;
        }
        final String methodName = e.getSimpleName().toString();
        return PROPERTY_PREFIXES.stream()
                .anyMatch(prefix -> {
                    //Check if the method starts with the prefix followed by a camel case decl
                    if ((methodName.length() > prefix.length()) && (methodName.startsWith(prefix))) {
                        final String restOfTheName = methodName.substring(prefix.length());
                        return Character.isUpperCase(restOfTheName.charAt(0));
                    } else {
                        return false;
                    }
                });
    }

    private String buildPropertyName(final Element getter, ProcessingContext context) {
        final String methodName = getter.getSimpleName().toString();
        for (final String prefix : PROPERTY_PREFIXES) {
            if (methodName.startsWith(prefix)) {
                final String capitalizedName = methodName.substring(prefix.length());
                if (capitalizedName.length() > 1) {
                    final String firstCharacted = capitalizedName.substring(0, 1).toLowerCase();
                    return firstCharacted + capitalizedName.substring(1);
                } else {
                    return capitalizedName.toLowerCase();
                }
            }
        }
        //May be appropriate to throw an iae?
        context.warning("Suspicious property name without property prefix " + methodName + " in class " + getter.getEnclosingElement().getSimpleName());
        return methodName;
    }


}
