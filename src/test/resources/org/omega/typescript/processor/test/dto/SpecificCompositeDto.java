package org.omega.typescript.processor.test.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * Created by kibork on 4/3/2018.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class SpecificCompositeDto extends CompositeDto implements HasName, Serializable {

    // ------------------ Constants  --------------------

    // ------------------ Fields     --------------------

    private String newProperty;

    // ------------------ Properties --------------------

    // ------------------ Logic      --------------------


    public SpecificCompositeDto(SimpleDto simpleDto, String newProperty) {
        super();
        this.newProperty = newProperty;
    }
}
