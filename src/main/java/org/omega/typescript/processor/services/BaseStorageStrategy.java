package org.omega.typescript.processor.services;

import org.omega.typescript.processor.model.Endpoint;
import org.omega.typescript.processor.model.TypeDefinition;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by kibork on 5/1/2020.
 */
public abstract class BaseStorageStrategy implements StorageStrategy {

    // ---------------- Fields & Constants --------------

    protected final ProcessingContext context;

    protected final FileNamingStrategy fileNamingStrategy;

    // ------------------ Logic      --------------------

    public BaseStorageStrategy(final ProcessingContext context, final FileNamingStrategy fileNamingStrategy) {
        this.context = context;
        this.fileNamingStrategy = fileNamingStrategy;
    }

    @Override
    public PrintWriter createWriter(final TypeDefinition definition) throws IOException {
        return createWriter(fileNamingStrategy.getFullTypeFileName(definition));
    }

    @Override
    public PrintWriter createWriter(final Endpoint endpoint) throws IOException {
        return createWriter(fileNamingStrategy.getGetFullFileName(endpoint));
    }

    @Override
    public PrintWriter createWriter(final String filename) throws IOException {
        final File targetFile = getFile(filename);
        return new PrintWriter(new FileWriter(targetFile, false));
    }

}
