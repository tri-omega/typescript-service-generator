package org.omega.typescript.processor.test.dto;

import lombok.Data;

import java.util.*;

/**
 * Created by kibork on 4/24/2018.
 */
@Data
public class MapDto {

    // ------------------ Constants  --------------------

    // ------------------ Fields     --------------------

    private Map untypedMap;

    private Map<String, ?> stringMap;

    private HashMap<String, Object> stringHashMap;

    private TreeMap<Long, SimpleDto> dtoMap;

    private Map<SimpleDto, String> invalidMap;

    private Map<TestEnum, String> enumMap;

    // ------------------ Properties --------------------

    // ------------------ Logic      --------------------

    public enum TestEnum {
        TEST1, TEST2
    }

}
