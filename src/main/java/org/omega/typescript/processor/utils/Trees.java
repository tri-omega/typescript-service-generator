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
