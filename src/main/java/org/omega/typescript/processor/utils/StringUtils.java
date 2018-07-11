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

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by kibork on 2/12/2018.
 */
public final class StringUtils {

    // ------------------ Constants  --------------------

    // ------------------ Logic      --------------------

    private StringUtils() {
    }

    public static boolean hasText(final String str) {
        return str != null && !str.trim().isEmpty();
    }

    public static boolean equals(final String str1, final String str2) {
        return str1 == null ? str2 == null : str1.equals(str2);
    }

    public static String escapeString(final String str, final String escape) {
        return str.replace(escape, "\\" + escape);
    }

    public static String endWith(final String str, final String suffix) {
        if (str == null) {
            return null;
        }
        if (str.endsWith(suffix)) {
            return str;
        }
        return str + suffix;
    }

    public static String exceptionToString(final Throwable th) {
        final StringWriter out = new StringWriter();
        out.append(th.getMessage()).append("\n");
        try (PrintWriter exWriter = new PrintWriter(out)) {
            th.printStackTrace(exWriter);
        }
        return out.toString();
    }
}
