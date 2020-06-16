package org.omega.typescript.processor.test.dto;

import lombok.Getter;

/**
 * Created by kibork on 4/3/2018.
 */
public class SelfCompositeDto {

    // ------------------ Constants  --------------------

    // ------------------ Fields     --------------------

    @Getter private String field1;

    @Getter private final SelfCompositeDto selfDto = new SelfCompositeDto();

    // ------------------ Properties --------------------

    // ------------------ Logic      --------------------

}
