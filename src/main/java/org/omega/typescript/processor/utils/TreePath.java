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
