package org.omega.typescript.processor.test;

import org.omega.typescript.api.TypeScriptEndpoint;
import org.omega.typescript.processor.test.dto.SimpleDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by kibork on 4/3/2018.
 */
@RestController
@TypeScriptEndpoint(moduleName = "SimpleDtoController")
@RequestMapping(method = RequestMethod.GET, path = "/api/")
public class SimpleDtoController {

    // ------------------ Constants  --------------------

    // ------------------ Fields     --------------------

    // ------------------ Properties --------------------

    // ------------------ Logic      --------------------

    @GetMapping("get")
    public SimpleDto getSimpleDto() {
        return new SimpleDto();
    }

}
