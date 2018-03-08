package org.omega.typescript.processor.test;

import org.omega.typescript.api.TypeScriptEndpoint;
import org.omega.typescript.api.TypeScriptName;
import org.springframework.web.bind.annotation.*;

/**
 * Created by kibork on 3/6/2018.
 */
@RestController
@TypeScriptEndpoint
@RequestMapping(method = RequestMethod.GET, path = "/api/")
public class BasicController {

    // ------------------ Constants  --------------------

    // ------------------ Fields     --------------------

    // ------------------ Properties --------------------

    // ------------------ Logic      --------------------

    @RequestMapping("get")
    public String getByElementIdAsNamedParam(@RequestParam(value = "elementId", required = false) long id) {
        return null;
    }

    @GetMapping(path = "getP")
    public String getByElementIdAsParam(@RequestParam long id) {
        return null;
    }

    @RequestMapping(path = "get/{elementId}")
    public String getByElementInNamedPath(@PathVariable("elementId") Long id) {
        return null;
    }

    @GetMapping(path = "get/{id}")
    public String getByElementInPath(@PathVariable Long id) {
        return null;
    }

    @PostMapping("post")
    public String postDataAsBody(@RequestBody String data) {
        return null;
    }

    @PostMapping(path = "post")
    public String postDataAsParam(@RequestParam String data) {
        return null;
    }

    @PostMapping(path = "post/{id}")
    public String postDataAsParamAndUrl(@RequestParam String data, @PathVariable Long id) {
        return null;
    }

    @PutMapping(path = "put")
    public String putDataAsBody(@RequestBody(required = false) String data) {
        return null;
    }

    @DeleteMapping(path = "delete")
    public String deleteById(@RequestParam long id) {
        return null;
    }

    @PatchMapping(path = "patch")
    public String patchById(@RequestParam long id) {
         return null;
    }

    @CustomMapping
    @TypeScriptName("typeScriptMethodName")
    public String customMethod() {
        return null;
    }

}
