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

package org.omega.typescript.processor.emitters;

import lombok.Getter;
import org.omega.typescript.processor.GenConfig;
import org.omega.typescript.processor.services.FileNamingStrategy;
import org.omega.typescript.processor.services.ProcessingContext;
import org.omega.typescript.processor.services.StorageStrategy;
import org.omega.typescript.processor.utils.LogUtil;

/**
 * Created by kibork on 5/2/2018.
 */
@Getter
public class EmitContext {

    // ------------------ Constants  --------------------

    // ------------------ Fields     --------------------

    private final ProcessingContext processingContext;

    private final StorageStrategy storageStrategy;

    private final FileNamingStrategy namingStrategy;

    private final TypeInstanceEmitter instanceRenderer;

    private final GenConfig genConfig;

    // ------------------ Properties --------------------


    // ------------------ Logic      --------------------


    public EmitContext(final ProcessingContext processingContext, final FileNamingStrategy namingStrategy, final StorageStrategy storageStrategy) {
        this.processingContext = processingContext;
        this.namingStrategy = namingStrategy;
        this.storageStrategy = storageStrategy;
        this.instanceRenderer = new TypeInstanceEmitter(this);
        this.genConfig = processingContext.getGenConfig();
    }

    public void debug(final String msg) {
        LogUtil.debug(processingContext.getProcessingEnv(), msg);
    }

    public void warning(final String msg) {
        LogUtil.warning(processingContext.getProcessingEnv(), msg);
    }

    public void error(final String msg) {
        LogUtil.error(processingContext.getProcessingEnv(), msg);
    }

}
