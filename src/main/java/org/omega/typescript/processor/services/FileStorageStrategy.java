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

package org.omega.typescript.processor.services;

import java.io.File;

/**
 * Created by kibork on 5/2/2018.
 */
public class FileStorageStrategy extends BaseStorageStrategy {

    // ------------------ Constants  --------------------

    // ------------------ Fields     --------------------

    // ------------------ Properties --------------------

    // ------------------ Logic      --------------------


    public FileStorageStrategy(final ProcessingContext context, final FileNamingStrategy fileNamingStrategy) {
        super(context, fileNamingStrategy);
    }

    @Override
    public File getFile(final String filename) {
        final File targetFile = new File(filename).getAbsoluteFile();
        if (targetFile.exists()) {
            final boolean result = targetFile.delete();
            if (!result) {
                context.error("Failed to delete file " + targetFile);
            }
        } else if (!targetFile.getParentFile().exists()) {
            final boolean result = targetFile.getParentFile().mkdirs();
            if (!result) {
                context.error("Failed to create containing folder " + targetFile.getParentFile());
            }
        }
        return targetFile;
    }


}
