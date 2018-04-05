package org.omega.typescript.processor.test.dto;

import org.omega.typescript.api.TypeScriptIgnore;
import org.omega.typescript.api.TypeScriptName;

/**
 * Created by kibork on 4/3/2018.
 */
public class SimpleDto {

    // ------------------ Constants  --------------------

    // ------------------ Fields     --------------------

    private String field1;

    private long field2;

    private Integer field3;

    private int field4;

    private int field5;

    private int field6;

    // ------------------ Properties --------------------

    public String getField1() {
        return field1;
    }

    public SimpleDto setField1(String field1) {
        this.field1 = field1;
        return this;
    }

    public long getField2() {
        return field2;
    }

    public SimpleDto setField2(long field2) {
        this.field2 = field2;
        return this;
    }

    @TypeScriptName("customName")
    public Integer getField3() {
        return field3;
    }

    public SimpleDto setField3(Integer field3) {
        this.field3 = field3;
        return this;
    }

    @TypeScriptIgnore
    public int getField4() {
        return field4;
    }

    public SimpleDto setField4(int field4) {
        this.field4 = field4;
        return this;
    }

    protected int getField5() {
        return field5;
    }

    public SimpleDto setField5(int field5) {
        this.field5 = field5;
        return this;
    }

    // ------------------ Logic      --------------------

}
