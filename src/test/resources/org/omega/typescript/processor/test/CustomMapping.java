package org.omega.typescript.processor.test;

import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by kibork on 3/6/2018.
 */
@Retention(RetentionPolicy.SOURCE)
@RequestMapping("mappingValue")
public @interface CustomMapping {
}
