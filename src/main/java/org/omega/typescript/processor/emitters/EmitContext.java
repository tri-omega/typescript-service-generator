package org.omega.typescript.processor.emitters;

import lombok.Getter;
import org.omega.typescript.processor.GenConfig;
import org.omega.typescript.processor.services.FileNamingStrategy;
import org.omega.typescript.processor.services.ProcessingContext;
import org.omega.typescript.processor.services.StorageStrategy;
import org.omega.typescript.processor.utils.LogUtil;

/**
 * Created by kibork on 5/2/2018.
 */
@Getter
public class EmitContext {

    // ------------------ Constants  --------------------

    // ------------------ Fields     --------------------

    private final ProcessingContext processingContext;

    private final StorageStrategy storageStrategy;

    private final FileNamingStrategy namingStrategy;

    private final TypeInstanceEmitter instanceRenderer;

    private final GenConfig genConfig;

    // ------------------ Properties --------------------


    // ------------------ Logic      --------------------


    public EmitContext(final ProcessingContext processingContext, final FileNamingStrategy namingStrategy, final StorageStrategy storageStrategy, final GenConfig genConfig) {
        this.processingContext = processingContext;
        this.namingStrategy = namingStrategy;
        this.storageStrategy = storageStrategy;
        this.instanceRenderer = new TypeInstanceEmitter(this);
        this.genConfig = genConfig;
    }

    public void debug(final String msg) {
        LogUtil.debug(processingContext.getProcessingEnv(), msg);
    }

    public void warning(final String msg) {
        LogUtil.warning(processingContext.getProcessingEnv(), msg);
    }

    public void error(final String msg) {
        LogUtil.error(processingContext.getProcessingEnv(), msg);
    }

}
