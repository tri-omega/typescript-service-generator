package org.omega.typescript.processor.test.dto;

import lombok.Getter;

/**
 * Created by kibork on 4/3/2018.
 */
public class CompositeDto {

    // ------------------ Constants  --------------------

    // ------------------ Fields     --------------------

    @Getter private String field1;

    @Getter private final SimpleDto simpleDto = null;

    // ------------------ Properties --------------------

    // ------------------ Logic      --------------------

}
