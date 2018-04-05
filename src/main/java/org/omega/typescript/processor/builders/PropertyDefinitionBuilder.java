package org.omega.typescript.processor.builders;

import org.omega.typescript.api.TypeScriptIgnore;
import org.omega.typescript.api.TypeScriptName;
import org.omega.typescript.processor.ProcessingContext;
import org.omega.typescript.processor.model.PropertyDefinition;
import org.omega.typescript.processor.utils.AnnotationUtils;
import org.omega.typescript.processor.utils.StringUtils;
import org.omega.typescript.processor.utils.TypeUtils;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by kibork on 4/3/2018.
 */
public class PropertyDefinitionBuilder {

    // ------------------ Constants  --------------------

    public static final List<String> PROPERTY_PREFIXES = Arrays.asList("get", "is"
//            , "has" 
    );

    // ------------------ Fields     --------------------

    private final ProcessingContext context;

    // ------------------ Properties --------------------

    // ------------------ Logic      --------------------


    public PropertyDefinitionBuilder(ProcessingContext context) {
        this.context = context;
    }

    public List<PropertyDefinition> buildPropertyGetters(final TypeElement typeElement) {
        final List<ExecutableElement> methods = TypeUtils.getMethods(typeElement, context);

        //Selects only "own" getters of the class
        final List<ExecutableElement> getters = methods.stream()
                .filter(e -> e.getEnclosingElement() == typeElement)
                .filter(e -> e.getModifiers().contains(Modifier.PUBLIC))
                .filter(e -> !e.getModifiers().contains(Modifier.TRANSIENT))
                .filter(this::isGetter)
                .filter(e -> !AnnotationUtils.getAnnotation(e, TypeScriptIgnore.class).isPresent())
                .collect(Collectors.toList());

        return getters.stream()
                .map(this::buildProperty)
                .collect(Collectors.toList());
    }

    private boolean isGetter(final ExecutableElement e) {
        if (!e.getParameters().isEmpty()) {
            return false;
        }
        final String methodName = e.getSimpleName().toString();
        return PROPERTY_PREFIXES.stream()
                .anyMatch(prefix -> {
                    //Check if the method starts with the prefix followed by a camel case decl
                    if ((methodName.length() > prefix.length()) && (methodName.startsWith(prefix))) {
                        final String restOfTheName = methodName.substring(prefix.length());
                        return Character.isUpperCase(restOfTheName.charAt(0));
                    } else {
                        return false;
                    }
                });
    }

    private String getTypeScriptName(final ExecutableElement getter) {
        final Optional<? extends AnnotationMirror> customName = AnnotationUtils.getAnnotation(getter, TypeScriptName.class);
        return customName
                .flatMap(am -> AnnotationUtils.getValue(am, "value", context))
                .map(av -> AnnotationUtils.readSimpleAnnotationValue(av, context))
                .flatMap(name -> StringUtils.hasText(name) ? Optional.of(name) : Optional.empty())
                .orElse(buildPropertyName(getter));
    }

    private String buildPropertyName(ExecutableElement getter) {
        final String methodName = getter.getSimpleName().toString();
        for (final String prefix : PROPERTY_PREFIXES) {
            if (methodName.startsWith(prefix)) {
                final String capitalizedName = methodName.substring(prefix.length());
                if (capitalizedName.length() > 1) {
                    final String firstCharacted = capitalizedName.substring(0, 1).toLowerCase();
                    return firstCharacted + capitalizedName.substring(1);
                } else {
                    return capitalizedName.toLowerCase();
                }
            }
        }
        //May be appropriate to throw an iae?
        context.warning("Suspicious property name without property prefix " + methodName + " in class " + getter.getEnclosingElement().getSimpleName());
        return methodName;
    }

    private PropertyDefinition buildProperty(final ExecutableElement getter) {
        final PropertyDefinition property = new PropertyDefinition();
        property.setGetterName(getter.getSimpleName().toString());
        property.setName(getTypeScriptName(getter));
        property.setType(context.getTypeOracle().getOrDefineType(getter.getReturnType()));
        return property;
    }

}
