package org.omega.typescript.processor.builders.properties;

import org.omega.typescript.processor.services.ProcessingContext;
import org.omega.typescript.processor.model.PropertyDefinition;

import javax.lang.model.element.TypeElement;
import java.util.List;

/**
 * Created by kibork on 5/9/2018.
 */
public interface TypePropertyLocator {


    List<PropertyDefinition> locateProperties(TypeElement typeElement, ProcessingContext context);

}
