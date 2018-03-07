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
}
