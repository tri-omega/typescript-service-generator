package org.omega.typescript.processor.model;

import lombok.Data;

/**
 * Created by kibork on 4/3/2018.
 */
@Data
public class PropertyDefinition {

    private String name;

    private String getterName;

    private TypeDefinition type;

}
