package org.omega.typescript.processor;

import lombok.Getter;
import org.omega.typescript.processor.services.ProcessingContext;
import org.omega.typescript.processor.utils.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by kibork on 5/16/2018.
 */
@Getter
public class GenConfig {

    // ---------------- Fields & Constants --------------

    private final ProcessingContext context;

    public static final String INTERNAL_PROP_PREFIX = "tsg.";

    private String outputFolder = "get/";

    private String generatedFilesSuffix = ".generated";

    private String defaultModuleName = "service-api";

    private String defaultHttpClassName = "";

    private String defaultHttpServiceInclude;

    private Map<String, String> pathOverrides = new HashMap<>();

    // ------------------ Properties --------------------

    public String getDefaultModuleName() {
        return defaultModuleName;
    }

    public String getDefaultHttpClassName() {
        return defaultHttpClassName;
    }

    public String getDefaultHttpServiceInclude() {
        return defaultHttpServiceInclude;
    }


    // ------------------ Logic      --------------------


    public GenConfig(final ProcessingContext context) {
        this.context = context;
    }

    public boolean load(final InputStream configData) {
        try {
            final Properties properties = new Properties();
            properties.load(configData);
            properties.forEach((key, value) -> {
                if (key.toString().startsWith(INTERNAL_PROP_PREFIX)) {
                    readTsgProperty(key.toString().substring(INTERNAL_PROP_PREFIX.length()), value.toString());
                } else {
                    pathOverrides.put(key.toString().trim(), value.toString().trim());
                }
            });

            return true;
        } catch (IOException e) {
            context.error("Unable to load configuration: " + e.getLocalizedMessage());
            return false;
        }
    }

    private void readTsgProperty(final String propertyName, final String value) {
        if ("default-module-name".equalsIgnoreCase(propertyName)) {
            defaultModuleName = value;
        } else if ("http-service-class".equalsIgnoreCase(propertyName)) {
            defaultHttpClassName = value;
        } else if ("http-service-include".equalsIgnoreCase(propertyName)) {
            defaultHttpServiceInclude = value;
        } else if ("output-folder".equalsIgnoreCase(propertyName)) {
            outputFolder = StringUtils.endWith(value, "/");
        } else if ("generated-suffix".equalsIgnoreCase(propertyName)) {
            generatedFilesSuffix = value;
        } else {
            context.error(String.format("Unknown tsg property %s with value %s, tsg is a reserved prefix", propertyName, value));
        }
    }

}
