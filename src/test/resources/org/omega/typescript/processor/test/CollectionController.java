package org.omega.typescript.processor.test;

import org.omega.typescript.api.TypeScriptEndpoint;
import org.omega.typescript.processor.test.dto.CollectionDto;
import org.omega.typescript.processor.test.dto.SimpleDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by kibork on 4/24/2018.
 */
@RestController
@TypeScriptEndpoint(moduleName = "CollectionController")
@RequestMapping(method = RequestMethod.GET, path = "/api/")
public class CollectionController {

    // ------------------ Constants  --------------------

    // ------------------ Fields     --------------------

    // ------------------ Properties --------------------

    // ------------------ Logic      --------------------

    @GetMapping("get")
    public CollectionDto get() {
        return null;
    }
}
