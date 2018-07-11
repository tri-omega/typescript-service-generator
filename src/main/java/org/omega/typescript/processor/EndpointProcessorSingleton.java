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

package org.omega.typescript.processor;

import org.omega.typescript.api.TypeScriptEndpoint;
import org.omega.typescript.processor.services.ProcessingContext;
import org.omega.typescript.processor.model.EndpointContainer;
import org.omega.typescript.processor.model.TypeOracle;
import org.omega.typescript.processor.emitters.Emitter;
import org.omega.typescript.processor.emitters.TypeScriptEmitter;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

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

    private Emitter emitter;

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
        emitter = new TypeScriptEmitter();
    }

    public void clear() {
        endpointContainer.clear();
        oracle.clear();
    }

    public void process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv, final ProcessingEnvironment processingEnv) {
        final List<TypeElement> endpoints = collectRoundEndpoints(roundEnv);
        if (endpoints.isEmpty()) {
            return;
        }

        final ProcessingContext context = new ProcessingContext(roundEnv, processingEnv, oracle, endpointContainer);
        try {

            final AtomicBoolean hasNew = new AtomicBoolean(false);
            endpoints.forEach(type -> {
                final String className = type.getQualifiedName().toString().intern();
                synchronized (className) {
                    if (!endpointContainer.hasEndpoint(className)) {
                        if (!hasNew.get()) {
                            context.getGenConfig().tryLoadConfig(type);
                        }
                        tryAcceptClass(type, context);
                        hasNew.set(true);
                    }
                }
            });

            if (hasNew.get()) {
                emitter.initContext(context);
                emitter.renderTypes(oracle);
                emitter.renderEndpoints(endpointContainer);
            }
        } catch (Exception ex) {
            final StringWriter out = new StringWriter();
            try (PrintWriter exWriter = new PrintWriter(out)) {
                ex.printStackTrace(exWriter);
            }
            context.error("Exception while processing Type Script Generator annotations:" + ex.getMessage()
                    + "\n" + out.toString());
        }
    }

    private List<TypeElement> collectRoundEndpoints(RoundEnvironment roundEnv) {
        final Set<? extends Element> annotated = roundEnv.getElementsAnnotatedWith(TypeScriptEndpoint.class);
        return annotated.stream()
                .filter(element -> ElementKind.CLASS.equals(element.getKind()) || ElementKind.INTERFACE.equals(element.getKind()))
                .map(element -> (TypeElement) element)
                .collect(toList());
    }

    private void tryAcceptClass(final TypeElement type, ProcessingContext context) {
        final String className = type.getQualifiedName().toString().intern();
        if (endpointContainer.hasEndpoint(className)) {
            //Concurrent processing, skip the element
            return;
        }
        oracle.initContext(context);
        endpointContainer.buildEndpoint(type, context);
    }
}
