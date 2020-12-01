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
            return requireClasspathResource(name, context);
        } catch (IllegalArgumentException ex) {
            return "Error: " + StringUtils.exceptionToString(ex);
        }
    }

    public static String requireClasspathResource(final String name, final ProcessingContext context) throws IllegalArgumentException {
        try {
            final FileObject fileObject = context.getProcessingEnv().getFiler().getResource(StandardLocation.CLASS_PATH, "", name);
            return fileObject.getCharContent(true).toString();
        } catch (IOException ex) {
            throw new IllegalArgumentException("Unable to find " + name, ex);
        }
    }
}
