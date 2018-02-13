package org.omega.typescript.processor;

import org.omega.typescript.api.TypeScriptEndpoint;
import org.omega.typescript.processor.model.Endpoint;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.stream.Collectors.toList;

/**
 * Created by kibork on 1/22/2018.
 */
public final class EndpointProcessorSingleton {

    // ------------------ Constants  --------------------

    // ------------------ Fields     --------------------

    private static final EndpointProcessorSingleton instance = new EndpointProcessorSingleton();

    private EndpointContainer endpointContainer = new EndpointContainer();

    private TypeOracle oracle = new TypeOracle();


    // ------------------ Properties --------------------

    public static EndpointProcessorSingleton getInstance() {
        return instance;
    }

    public EndpointContainer getEndpointContainer() {
        return endpointContainer;
    }

    public TypeOracle getOracle() {
        return oracle;
    }

    // ------------------ Logic      --------------------


    private EndpointProcessorSingleton() {
    }

    public void clear() {
        endpointContainer.clear();
        oracle.clear();
    }

    public void process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv, final ProcessingEnvironment processingEnv) {
        final List<TypeElement> endpoints = collectRoundEndpoints(roundEnv);

        endpoints.forEach(type -> {
            final String className = type.getQualifiedName().toString().intern();
            synchronized (className) {
                if (!endpointContainer.hasEndpoint(className)) {
                    tryAcceptClass(type, roundEnv, processingEnv);
                }
            }
        });
    }

    private List<TypeElement> collectRoundEndpoints(RoundEnvironment roundEnv) {
        final Set<? extends Element> annotated = roundEnv.getElementsAnnotatedWith(TypeScriptEndpoint.class);
        return annotated.stream()
                .filter(element -> ElementKind.CLASS.equals(element.getKind()))
                .map(element -> (TypeElement) element)
                .collect(toList());
    }

    private void tryAcceptClass(final TypeElement type, final RoundEnvironment roundEnv, final ProcessingEnvironment processingEnv) {
        final String className = type.getQualifiedName().toString().intern();
        if (endpointContainer.hasEndpoint(className)) {
            //Concurrent processing, skip the element
            return;
        }
        final ProcessingContext context = new ProcessingContext(roundEnv, processingEnv, oracle, endpointContainer);
        endpointContainer.buildEndpoint(type, context);
    }
}
