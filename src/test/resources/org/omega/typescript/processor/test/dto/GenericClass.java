package org.omega.typescript.processor.test.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Created by kibork on 4/5/2018.
 */
@Data
public class GenericClass<T extends CompositDto & HasName> {

    // ------------------ Constants  --------------------

    // ------------------ Fields     --------------------

    private SubGeneric<String, ? extends SimpleDto, T, ? extends SimpleGeneric<? extends DobuleGeneric<String, ?>>, ? super Integer> field1;

    private T field2;

    // ------------------ Properties --------------------

    // ------------------ Logic      --------------------

    public static class SubGeneric<R, R2 extends SimpleDto, R3, R4, R5> {

        private R field1;

        private R2 field2;

        private R3 field3;

        private R4 field4;

        private R5 field5;

        public R getField1() {
            return field1;
        }

        public R2 getField2() {
            return field2;
        }
    }

    @Data
    public static class SimpleGeneric<T> {
        private T t;
    }

    @Data
    public static class DobuleGeneric<T1, T2> {
        private T1 t1;
        private T2 t2;
    }


}
