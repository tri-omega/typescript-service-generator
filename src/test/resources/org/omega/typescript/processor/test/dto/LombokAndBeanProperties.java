package org.omega.typescript.processor.test.dto;

import lombok.Data;
import org.omega.typescript.api.TypeScriptIgnore;

@Data
public class LombokAndBeanProperties {

    private String property1;

    @TypeScriptIgnore
    private String property2;

    public String getProperty3() {
        return property1;
    }

    @TypeScriptIgnore
    public String getProperty4() {
        return property2;
    }
}
