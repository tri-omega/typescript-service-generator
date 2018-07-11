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

import org.omega.typescript.processor.model.TypeContainer;
import org.omega.typescript.processor.services.ProcessingContext;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.QualifiedNameable;

/**
 * Created by kibork on 5/2/2018.
 */
public class TypeContainerBuilder {

    // ------------------ Constants  --------------------

    // ------------------ Fields     --------------------

    private final ProcessingContext context;

    // ------------------ Properties --------------------

    // ------------------ Logic      --------------------

    public TypeContainerBuilder(final ProcessingContext context) {
        this.context = context;
    }

    public TypeContainer buildContainer(final Element element) {
        if (element == null) {
            return null;
        }
        final Element enclosingElement = element.getEnclosingElement();
        final String shortName = enclosingElement.getSimpleName().toString();
        final TypeContainer typeContainer =
                new TypeContainer()
                    .setShortName(shortName)
                    .setFullName(shortName)
                ;

        if (enclosingElement instanceof QualifiedNameable) {
            typeContainer.setFullName(((QualifiedNameable)enclosingElement).getQualifiedName().toString());
        }
        if (enclosingElement.getKind() != ElementKind.PACKAGE) {
            typeContainer.setPackageElement(false);
            typeContainer.setContainer(buildContainer(enclosingElement));
        } else {
            typeContainer.setPackageElement(true);
        }
        return typeContainer;
    }
}
