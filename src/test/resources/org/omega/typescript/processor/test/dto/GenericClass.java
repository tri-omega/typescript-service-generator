package org.omega.typescript.processor.test.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by kibork on 4/5/2018.
 */
@Data
public class GenericClass<T extends CompositDto & HasName> {

    // ------------------ Constants  --------------------

    // ------------------ Fields     --------------------

    private SubGeneric<String, ? extends SimpleDto, T, ? extends SimpleGeneric<? extends DoubleGeneric<String, ?>>, ? super Integer> field1;

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

        public <T extends SimpleGeneric<String>> T getField6() { return null; }

        public <T extends SimpleGeneric<String> & HasName> T getField7() { return null; }
    }

    @Data
    public static class SimpleGeneric<T> {
        private T t;
    }

    @Data
    public static class DoubleGeneric<T1, T2> {
        private T1 t1;
        private T2 t2;
    }


}
