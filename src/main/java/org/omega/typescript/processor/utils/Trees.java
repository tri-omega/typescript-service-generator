package org.omega.typescript.processor.utils;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import java.lang.reflect.Method;

/**
 * Created by kibork on 5/17/2018.
 */
public class Trees {

    // ---------------- Fields & Constants --------------

    private static final String className = "com.sun.source.util.Trees";

    private final Object treesInstance;
    private final Class<?> treesClass;

    // ------------------ Properties --------------------

    // ------------------ Logic      --------------------

    private Trees(final Object treesInstance, final Class<?> treesClass) {
        this.treesInstance = treesInstance;
        this.treesClass = treesClass;
    }

    public static Trees fromEnv(final ProcessingEnvironment environment) {
        try {
            final Class<?> treesClass = Class.forName(className);
            final Method method = treesClass.getMethod("instance", ProcessingEnvironment.class);

            final Object treesInstance = method.invoke(null, environment);
            return new Trees(treesInstance, treesClass);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public TreePath getPath(final Element element) {
        try {
            final Method getPath = treesClass.getMethod("getPath", Element.class);
            final Object invoke = getPath.invoke(treesInstance, element);

            return new TreePath(invoke);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
