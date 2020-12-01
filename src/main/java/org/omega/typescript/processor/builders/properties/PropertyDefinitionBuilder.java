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

package org.omega.typescript.processor.builders.properties;

import org.omega.typescript.processor.model.PropertyDefinition;
import org.omega.typescript.processor.services.ProcessingContext;
import org.omega.typescript.processor.utils.IOUtils;
import org.omega.typescript.processor.utils.LogUtil;
import org.omega.typescript.processor.utils.ReflectionUtils;
import org.omega.typescript.processor.utils.StringUtils;

import javax.lang.model.element.TypeElement;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by kibork on 4/3/2018.
 */
public class PropertyDefinitionBuilder {

    // ------------------ Constants  --------------------

    // ------------------ Fields     --------------------

    private final ProcessingContext context;

    private final Iterable<TypePropertyLocator> locators;

    private boolean printedServiceWarning = false;

    // ------------------ Properties --------------------

    // ------------------ Logic      --------------------


    public PropertyDefinitionBuilder(final ProcessingContext context) {
        this.context = context;
        this.locators = getPropertyLocators(context);
    }

    private Iterable<TypePropertyLocator> getPropertyLocators(ProcessingContext context) {
        try {
            final ServiceLoader<TypePropertyLocator> serviceLocator = ReflectionUtils
                    .callOnClass(ServiceLoader.class, null, "load",
                            new Class<?>[] {Class.class, ClassLoader.class, Module.class},
                            new Object[] {TypePropertyLocator.class, this.getClass().getClassLoader(), this.getClass().getModule()}
                    );
            final List<TypePropertyLocator> services = serviceLocator
                    .stream()
                    .map(ServiceLoader.Provider::get)
                    .collect(Collectors.toList());

            return services;
        } catch (Exception e) {
            if (!printedServiceWarning) {
                LogUtil.warning(context.getProcessingEnv(), "Unable to use service interface. Try using resource loader.");
                printedServiceWarning = true;
            }
        }
        //Well, load the services at least manually
        try {
            return readFromResourceFile(context);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        }
    }

    private Iterable<TypePropertyLocator> readFromResourceFile(ProcessingContext context) {
        final String content = IOUtils.requireClasspathResource("META-INF/services/" + TypePropertyLocator.class.getName(), context);
        return Arrays.stream(content.split("\n"))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .filter(s -> !s.startsWith("#"))
                .map(this::createService)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private TypePropertyLocator createService(final String className) {
        try {
            final Object instance = Class.forName(className).getDeclaredConstructor().newInstance();
            return (TypePropertyLocator)instance;
        } catch (Exception e) {
            context.warning("Failed to instantiate service class: " + className + " due to Exception\n" + StringUtils.exceptionToString(e));
            return null;
        }
    }

    public List<PropertyDefinition> buildProperties(final TypeElement typeElement) {
        final Map<String, PropertyDefinition> properties = new LinkedHashMap<>();
        for (final TypePropertyLocator locator : locators) {
            final List<PropertyDefinition> propertyDefinitions = locator.locateProperties(typeElement, context);
            propertyDefinitions.forEach(p -> properties.putIfAbsent(p.getName(), p));
        }
        return new ArrayList<>(properties.values());
    }


}
