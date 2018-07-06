package org.omega.typescript.processor.test;

import org.omega.typescript.api.TypeScriptEndpoint;
import org.omega.typescript.api.TypeScriptName;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;

/**
 * Created by kibork on 3/6/2018.
 */
@RestController
@TypeScriptEndpoint
@RequestMapping(method = RequestMethod.GET, path = "/api/")
public class JavaTimeController {

    // ------------------ Constants  --------------------

    // ------------------ Fields     --------------------

    // ------------------ Properties --------------------

    // ------------------ Logic      --------------------

    @RequestMapping("getLocalDate")
    public LocalDate getLocalDate() {
        return null;
    }

    @RequestMapping("getLocalTime")
    public LocalTime getLocalTime() {
        return null;
    }

    @RequestMapping("getLocalDateTime")
    public LocalDateTime getLocalDateTime() {
        return null;
    }

    @RequestMapping("getZonedDateTime")
    public ZonedDateTime getZonedDateTime() {
        return null;
    }
}
