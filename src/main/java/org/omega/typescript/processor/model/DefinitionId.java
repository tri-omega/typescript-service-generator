package org.omega.typescript.processor.model;

import org.omega.typescript.processor.ProcessingContext;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;

/**
 * Created by kibork on 1/22/2018.
 */
public class DefinitionId {

    // ------------------ Constants  --------------------

    // ------------------ Fields     --------------------

    private final String name;

    private final String packageName;

    private final TypeElement typeElement;

    // ------------------ Properties --------------------

    public String getName() {
        return name;
    }

    public String getPackageName() {
        return packageName;
    }

    public TypeElement getTypeElement() {
        return typeElement;
    }


    // ------------------ Logic      --------------------

    public DefinitionId(final TypeElement typeElement, final ProcessingContext context) {
        this.typeElement = typeElement;
        this.name = typeElement.getSimpleName().toString();
        this.packageName = context.getProcessingEnv().getElementUtils()
                .getPackageOf(typeElement).getQualifiedName().toString();
    }

}
