package org.omega.typescript.processor.test.module;

import org.omega.typescript.api.TypeScriptEndpoint;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by kibork on 2/5/2018.
 */
@RestController
@TypeScriptEndpoint
@RequestMapping(method = RequestMethod.POST, path = "/api/simple")
public class SimpleController {

    // ------------------ Constants  --------------------

    // ------------------ Fields     --------------------

    // ------------------ Properties --------------------

    // ------------------ Logic      --------------------

}
