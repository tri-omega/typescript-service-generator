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
import org.omega.typescript.processor.model.TypeDefinition;

import javax.lang.model.element.*;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by kibork on 2/2/2018.
 */
public final class TypeUtils {

    // ------------------ Constants  --------------------

    public static final String ARRAY_TYPE_NAME = "[]";

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

    public static List<ExecutableElement> getPropertyGetters(final TypeElement typeElement, final ProcessingContext context) {
        final List<ExecutableElement> methods = getMethods(typeElement, context);

        return methods.stream()
                .filter(m -> m.getSimpleName().toString().startsWith("get") || m.getSimpleName().toString().startsWith("is"))
                .collect(Collectors.toList());
    }

    public static List<ExecutableElement> getMethods(TypeElement typeElement, ProcessingContext context) {
        return TypeUtils.getMembers(typeElement, ElementKind.METHOD, context).stream()
                    .map(m -> (ExecutableElement) m)
                    .collect(Collectors.toList());
    }

    public static String getClassName(TypeElement typeElement) {
        return typeElement.getQualifiedName().toString();
    }

    public static String getClassName(final TypeMirror typeMirror, final ProcessingContext context) {
        if (typeMirror.getKind().isPrimitive()) {
            return typeMirror.toString();
        } else if (typeMirror.getKind() == TypeKind.ARRAY) {
            return ARRAY_TYPE_NAME;
        }
        final Types typeUtils = context.getProcessingEnv().getTypeUtils();
        final Element element = typeUtils.asElement(typeMirror);
        if (element instanceof QualifiedNameable) {
            final QualifiedNameable nameable = (QualifiedNameable)element;
            return nameable.getQualifiedName().toString();
        }
        return element.getSimpleName().toString();
    }

    public static String getGenericTypeName(final TypeDefinition containerType, final String genericName) {
        return containerType.getFullName() + "#" + genericName;
    }

}

