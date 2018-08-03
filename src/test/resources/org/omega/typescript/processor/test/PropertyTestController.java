package org.omega.typescript.processor.test;

import org.omega.typescript.api.TypeScriptEndpoint;
import org.omega.typescript.processor.test.dto.LombokAndBeanProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@TypeScriptEndpoint(moduleName = "SimpleController")
public class PropertyTestController {

    @GetMapping("value")
    public LombokAndBeanProperties getLombokAndBeanProperties() {
        return null;
    }

}
