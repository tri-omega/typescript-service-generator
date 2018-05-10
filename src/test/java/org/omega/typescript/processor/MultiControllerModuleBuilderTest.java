package org.omega.typescript.processor;

import org.junit.jupiter.api.Test;

/**
 * Created by kibork on 3/6/2018.
 */
public class MultiControllerModuleBuilderTest {

    // ------------------ Constants  --------------------

    // ------------------ Fields     --------------------

    // ------------------ Properties --------------------

    // ------------------ Logic      --------------------

    @Test
    void testModuleCreation() {
        final EndpointProcessorSingleton endpointProcessorSingleton = TestUtils.compileClass(
                "/org/omega/typescript/processor/test/module/SimpleController.java",
                "/org/omega/typescript/processor/test/module/SimpleController2.java"
        );
    }


}
