package org.omega.typescript.processor.test.dto;

import lombok.Data;

/**
 * Created by kibork on 4/3/2018.
 */
@Data
public abstract class SpecificCompositDto extends CompositDto implements HasName {

    // ------------------ Constants  --------------------

    // ------------------ Fields     --------------------

    private String newProperty;

    // ------------------ Properties --------------------

    // ------------------ Logic      --------------------


    public SpecificCompositDto(SimpleDto simpleDto, String newProperty) {
        super(simpleDto);
        this.newProperty = newProperty;
    }
}
