package org.omega.typescript.processor;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.Diagnostic;

/**
 * Created by kibork on 1/22/2018.
 */
public final class LogUtil {

    // ------------------ Constants  --------------------

    // ------------------ Fields     --------------------

    // ------------------ Properties --------------------

    // ------------------ Logic      --------------------

    private LogUtil() {
    }

    public static void debug(final ProcessingEnvironment env, String msg) {
        env.getMessager().printMessage(Diagnostic.Kind.NOTE, msg);
    }

    public static void warning(final ProcessingEnvironment env, String msg) {
        env.getMessager().printMessage(Diagnostic.Kind.WARNING, msg);
    }

    public static void error(final ProcessingEnvironment env, String msg) {
        env.getMessager().printMessage(Diagnostic.Kind.ERROR, msg);
    }
}
