package org.omega.typescript.processor.builders.properties;

import org.omega.typescript.processor.model.PropertyDefinition;
import org.omega.typescript.processor.services.ProcessingContext;
import org.omega.typescript.processor.utils.IOUtils;
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

    // ------------------ Properties --------------------

    // ------------------ Logic      --------------------


    public PropertyDefinitionBuilder(final ProcessingContext context) {
        this.context = context;
        this.locators = getPropertyLocators(context);
    }

    private Iterable<TypePropertyLocator> getPropertyLocators(ProcessingContext context) {
        final ServiceLoader<TypePropertyLocator> services = ServiceLoader.load(TypePropertyLocator.class);
        if (!services.iterator().hasNext()) {
            //Well, load the services at least manually
            final String content = IOUtils.readClasspathResource("META-INF/services/" + TypePropertyLocator.class.getName(), context);
            return Arrays.stream(content.split("\n"))
                    .map(String::trim)
                    .filter(StringUtils::hasText)
                    .map(this::createService)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } else {
            return services;
        }
    }

    private TypePropertyLocator createService(final String className) {
        try {
            final Object instance = Class.forName(className).newInstance();
            return (TypePropertyLocator)instance;
        } catch (Exception e) {
            context.error("Failed to instantiate service " + className + "\n" + StringUtils.exceptionToString(e));
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
