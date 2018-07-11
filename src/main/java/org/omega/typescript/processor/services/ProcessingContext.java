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

import org.omega.typescript.processor.GenConfig;
import org.omega.typescript.processor.model.EndpointContainer;
import org.omega.typescript.processor.model.TypeOracle;
import org.omega.typescript.processor.utils.LogUtil;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;

/**
 * Created by kibork on 1/22/2018.
 */
public class ProcessingContext {

    // ------------------ Constants  --------------------

    // ------------------ Fields     --------------------

    private final RoundEnvironment roundEnv;

    private final ProcessingEnvironment processingEnv;

    private final TypeOracle typeOracle;

    private final EndpointContainer endpointContainer;

    private final GenConfig genConfig;


    // ------------------ Properties --------------------

    public RoundEnvironment getRoundEnv() {
        return roundEnv;
    }

    public ProcessingEnvironment getProcessingEnv() {
        return processingEnv;
    }

    public TypeOracle getTypeOracle() {
        return typeOracle;
    }

    public EndpointContainer getEndpointContainer() {
        return endpointContainer;
    }

    public GenConfig getGenConfig() {
        return genConfig;
    }

    // ------------------ Logic      --------------------

    public ProcessingContext(RoundEnvironment roundEnv, ProcessingEnvironment processingEnv, TypeOracle typeOracle, EndpointContainer endpointContainer) {
        this.roundEnv = roundEnv;
        this.processingEnv = processingEnv;
        this.typeOracle = typeOracle;
        this.endpointContainer = endpointContainer;
        this.genConfig = new GenConfig(this);
    }

    public void debug(final String msg) {
        LogUtil.debug(processingEnv, msg);
    }

    public void warning(final String msg) {
        LogUtil.warning(processingEnv, msg);
    }

    public void error(final String msg) {
        LogUtil.error(processingEnv, msg);
    }

}
