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

import lombok.Getter;
import org.omega.typescript.processor.services.ProcessingContext;
import org.omega.typescript.processor.utils.StringUtils;
import org.omega.typescript.processor.utils.TreePath;
import org.omega.typescript.processor.utils.Trees;
import org.omega.typescript.processor.utils.TypeUtils;

import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by kibork on 5/16/2018.
 */
@Getter
public class GenConfig {

    // ---------------- Fields & Constants --------------

    public static final String CONFIG_FILE = "tsg-config.properties";

    private final ProcessingContext context;

    public static final String INTERNAL_PROP_PREFIX = "tsg.";

    private String outputFolder = "sys-gen/";

    private String stdApiFileName = "sys-std";

    private String requestManagerFileName = "sys-std";

    private String generatedFilesSuffix = ".generated";

    private String defaultModuleName = "service-api";

    private String defaultHttpClassName = "#any#";

    private String defaultHttpServiceInclude = "#invalid#";

    private String additionalServiceIncludes = "#invalid#";

    private boolean enableJavaTimeIntegration = true;

    private String zonedDateTimeType;

    private String localDateTimeType;

    private String timeType;

    private String dateType;

    private Map<String, String> pathOverrides = new HashMap<>();

    private Map<String, Pattern> excludedClasses = new HashMap<>();

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
        loadDefaultConfig();
    }

    public boolean load(final File file) {
        if ((!file.exists()) || (!file.isFile()) || (!file.canRead())) {
            return false;
        }
        try (InputStream stream = new FileInputStream(file)) {
            load(stream);
            return true;
        } catch (Exception ex) {
            context.warning("Failed to read file " + file + "\n" + StringUtils.exceptionToString(ex));
            return false;
        }
    }

    public boolean load(final InputStream configData) {
        try {
            final Properties properties = new Properties();
            properties.load(configData);
            properties.forEach((key, value) -> {
                if (key.toString().startsWith(INTERNAL_PROP_PREFIX)) {
                    readTsgProperty(key.toString().substring(INTERNAL_PROP_PREFIX.length()).trim(), value.toString().trim());
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
        } else if ("service-includes".equalsIgnoreCase(propertyName)) {
            additionalServiceIncludes = value;
        } else if ("std-api-file-name".equalsIgnoreCase(propertyName)) {
            stdApiFileName = value;
        } else if ("request-manager-file-name".equalsIgnoreCase(propertyName)) {
            requestManagerFileName = value;
        } else if ("enable-java-time-integration".equalsIgnoreCase(propertyName)) {
            enableJavaTimeIntegration = Boolean.valueOf(value);
        } else if ("java-time.zoned-date-time-type".equals(propertyName)) {
            zonedDateTimeType = value;
        } else if ("java-time.local-date-time-type".equals(propertyName)) {
            localDateTimeType = value;
        } else if ("java-time.time-type".equals(propertyName)) {
            timeType = value;
        } else if ("java-time.date-type".equals(propertyName)) {
            dateType = value;
        } else if (propertyName.startsWith("exclude-classes-regex")) {
            addExcludeFilter(value, propertyName.substring("exclude-classes-regex".length()));
        } else {
            context.error(String.format("Unknown tsg property %s with value %s, tsg is a reserved prefix", propertyName, value));
        }
    }

    private void addExcludeFilter(final String value, String excludeName) {
        if (!StringUtils.hasText(excludeName)) {
            excludeName = UUID.randomUUID().toString();
        } else if (excludeName.startsWith(".")) {
            excludeName = excludeName.substring(1);
        }
        if (!StringUtils.hasText(value)) {
            excludedClasses.remove(excludeName);
        } else {
            excludedClasses.putIfAbsent(excludeName, Pattern.compile(value));
        }
    }

    private void loadDefaultConfig() {
        tryLoadInternal("default-tsg-config.properties");
        tryLoadLocal("/" + CONFIG_FILE);
    }

    private void tryLoadInternal(final String fileName) {
        tryLoadResource(fileName, StandardLocation.CLASS_PATH);
    }

    private void tryLoadLocal(final String resourceName) {
        try (InputStream config = getClass().getResourceAsStream(resourceName)) {
            if (config != null) {
                context.debug("Found resource " + resourceName + " at local classpath. Likely local build");
            }
            if (config != null) {
                load(config);
            }
        } catch (Exception ex) {
            //
        }
    }

    private void tryLoadResource(final String propName, final StandardLocation location) {
        try (InputStream config = context.getProcessingEnv().getFiler().getResource(location, "", propName).openInputStream()) {
            if (config != null) {
                load(config);
            }
        } catch (Exception ex) {
            context.warning("Type Script Generator config file '" + propName + "' not found at " + location);
        }
    }


    public void tryLoadConfig(final TypeElement type) {
        try {
            final Trees trees = Trees.fromEnv(context.getProcessingEnv());
            final TreePath path = trees.getPath(type);
            final JavaFileObject sourceFile = path.getSourceFile();
            final URI uri = sourceFile.toUri();
            final String scheme = uri.getScheme();

            if ("file".equals(scheme)) {
                final File file = new File(uri);
                if (!file.exists()) {
                    context.error("File " + file + " doesn't exists");
                }
                File dir = file.getParentFile();
                while (dir.exists()) {
                    final File configFile = new File(dir, CONFIG_FILE);
                    if (configFile.exists()) {
                        context.debug("Found Type Script Generator config at " + configFile);
                        load(configFile);
                        break;
                    }
                    dir = dir.getParentFile();
                }
            } else {
                context.warning("Annotated class " + TypeUtils.getClassName(type) +
                        " is in exotic location. JavaFileObject kind: " + sourceFile.getKind() + ", name = " + sourceFile.getName() + ", uri = " + uri + ", scheme '" + scheme + "'");
            }
        } catch (Exception e) {
            context.debug("Unable to load javac compilation unit info. Likely local build.\n" + StringUtils.exceptionToString(e));
        }
    }
}
