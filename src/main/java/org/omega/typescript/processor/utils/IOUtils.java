package org.omega.typescript.processor.utils;

import org.omega.typescript.processor.services.ProcessingContext;

import javax.tools.FileObject;
import javax.tools.StandardLocation;
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

    public static String readClasspathResource(final String name, final ProcessingContext context) {
        try {
            final FileObject fileObject = context.getProcessingEnv().getFiler().getResource(StandardLocation.CLASS_PATH, "", name);
            return fileObject.getCharContent(true).toString();
        } catch (IOException ex) {
            return "Error: " + StringUtils.exceptionToString(ex);
        }
    }
}
