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
