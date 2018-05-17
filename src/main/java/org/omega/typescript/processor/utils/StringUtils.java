package org.omega.typescript.processor.utils;

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
}
