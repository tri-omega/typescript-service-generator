package org.omega.typescript.processor.test.dto;

import lombok.Data;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Created by kibork on 4/24/2018.
 */
@Data
public class CollectionDto {

    // ------------------ Constants  --------------------

    // ------------------ Fields     --------------------

    private String[] stringArray;

    private List<String> stringList;

    private Set<String> stringSet;

    private Collection<Long> longCollection;

    // ------------------ Properties --------------------

    // ------------------ Logic      --------------------

}
