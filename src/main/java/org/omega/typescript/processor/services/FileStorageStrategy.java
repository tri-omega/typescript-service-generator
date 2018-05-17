package org.omega.typescript.processor.services;

import org.omega.typescript.processor.model.Endpoint;
import org.omega.typescript.processor.model.TypeDefinition;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by kibork on 5/2/2018.
 */
public class FileStorageStrategy implements StorageStrategy {

    // ------------------ Constants  --------------------

    // ------------------ Fields     --------------------

    private final ProcessingContext context;

    private final FileNamingStrategy fileNamingStrategy;

    // ------------------ Properties --------------------

    // ------------------ Logic      --------------------


    public FileStorageStrategy(final ProcessingContext context, final FileNamingStrategy fileNamingStrategy) {
        this.context = context;
        this.fileNamingStrategy = fileNamingStrategy;
    }

    @Override
    public PrintWriter createWriter(final TypeDefinition definition) throws IOException {
        return createWriter(fileNamingStrategy.getFullTypeFileName(definition));
    }

    @Override
    public PrintWriter createWriter(final String filename) throws IOException {
        final File targetFile = getFile(filename);
        return new PrintWriter(new FileWriter(targetFile, false));
    }

    @Override
    public File getFile(final String filename) {
        final File targetFile = new File(filename).getAbsoluteFile();
        if (targetFile.exists()) {
            final boolean result = targetFile.delete();
            if (!result) {
                context.error("Failed to delete file " + targetFile);
            }
        } else if (!targetFile.getParentFile().exists()) {
            final boolean result = targetFile.getParentFile().mkdirs();
            if (!result) {
                context.error("Failed to create containing folder " + targetFile.getParentFile());
            }
        }
        return targetFile;
    }


    @Override
    public PrintWriter createWriter(final Endpoint endpoint) throws IOException {
        return createWriter(fileNamingStrategy.getGetFullFileName(endpoint));
    }

}
