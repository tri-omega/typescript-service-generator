package org.omega.typescript.processor.utils;

import org.omega.typescript.processor.ProcessingContext;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by kibork on 2/2/2018.
 */
public final class TypeUtils {

    // ------------------ Constants  --------------------

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

}

