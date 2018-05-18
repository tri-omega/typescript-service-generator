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
