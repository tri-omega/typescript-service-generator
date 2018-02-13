package org.omega.typescript.processor;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import java.util.Set;

/**
 * Created by kibork on 1/22/2018.
 */
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes(value = {"org.omega.typescript.api.TypeScriptEndpoint"})
public class ServiceEndpointProcessor extends AbstractProcessor {

    // ------------------ Constants  --------------------

    // ------------------ Fields     --------------------

    // ------------------ Properties --------------------

    // ------------------ Logic      --------------------

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return super.getSupportedAnnotationTypes();
    }

    @Override
    public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
        if (!isInitialized()) {
            throw new IllegalStateException("Annotation processor called before it was initialized");
        }
        EndpointProcessorSingleton.getInstance().process(annotations, roundEnv, processingEnv);
        return false;
    }
}
