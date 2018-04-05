package org.omega.typescript.processor.test.dto;

import lombok.Data;

/**
 * Created by kibork on 4/5/2018.
 */
@Data
public class WithSubclassDto {

    // ------------------ Constants  --------------------

    // ------------------ Fields     --------------------

    private String field1;

    private SubDto field2;

    // ------------------ Properties --------------------

    // ------------------ Logic      --------------------

    @Data
    public static class SubDto {

        private SubEnum field1 = SubEnum.VALUE1;

        public enum SubEnum {
            VALUE1, VALUE2
        }
    }

}
