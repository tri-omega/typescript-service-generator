package org.omega.typescript.processor.utils;

import java.io.*;

/**
 * Created by kibork on 5/7/2018.
 */
public final class IOUtils {

    // ---------------- Fields & Constants --------------

    // ------------------ Properties --------------------

    // ------------------ Logic      --------------------


    private IOUtils() {
    }

    public static boolean copyResource(final String resourcePath, final File target) {
        try (
                InputStream iStream = new BufferedInputStream(IOUtils.class.getResourceAsStream(resourcePath));
                OutputStream oStream = new BufferedOutputStream(new FileOutputStream(target))
        ) {
            byte buffer[] = new byte[8 * 1024];
            int read;
            while ((read = iStream.read(buffer)) > 0) {
                oStream.write(buffer, 0, read);
            }
            return true;
        } catch (IOException ex) {
            return false;
        }
    }
}
