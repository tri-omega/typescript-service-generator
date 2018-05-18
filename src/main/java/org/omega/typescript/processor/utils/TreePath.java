package org.omega.typescript.processor.utils;

import javax.tools.JavaFileObject;
import java.lang.reflect.Method;

/**
 * Created by kibork on 5/17/2018.
 */
public class TreePath {

    // ---------------- Fields & Constants --------------

    private static final String className = "com.sun.source.util.TreePath";

    private final Object instance;

    private final Class<?> clazz;

    // ------------------ Properties --------------------

    // ------------------ Logic      --------------------

    public TreePath(final Object instance) {
        this.instance = instance;
        try {
            this.clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public JavaFileObject getSourceFile() {
        try {
            final Method getCompilationUnit = clazz.getMethod("getCompilationUnit");
            final Object unit = getCompilationUnit.invoke(instance);
            final Method getSourceFile = unit.getClass().getMethod("getSourceFile");
            final Object sourceFile = getSourceFile.invoke(unit);
            return (JavaFileObject)sourceFile;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
