package org.omega.typescript.processor.builders.properties;

import org.omega.typescript.processor.services.ProcessingContext;
import org.omega.typescript.processor.model.PropertyDefinition;

import javax.lang.model.element.TypeElement;
import java.util.*;

/**
 * Created by kibork on 4/3/2018.
 */
public class PropertyDefinitionBuilder {

    // ------------------ Constants  --------------------

    public static final List<String> PROPERTY_PREFIXES = Arrays.asList("get", "is");

    // ------------------ Fields     --------------------

    private final ProcessingContext context;

    private final ServiceLoader<TypePropertyLocator> locators;

    // ------------------ Properties --------------------

    // ------------------ Logic      --------------------


    public PropertyDefinitionBuilder(final ProcessingContext context) {
        this.context = context;
        this.locators = ServiceLoader.load(TypePropertyLocator.class);
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
