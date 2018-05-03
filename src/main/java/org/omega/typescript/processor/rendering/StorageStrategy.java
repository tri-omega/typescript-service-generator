package org.omega.typescript.processor.rendering;

import org.omega.typescript.processor.model.TypeDefinition;

import java.io.BufferedWriter;
import java.io.IOException;

/**
 * Created by kibork on 5/2/2018.
 */
public interface StorageStrategy {
    BufferedWriter createWriter(TypeDefinition definition) throws IOException;

    String getTypeFilename(TypeDefinition definition);

    String getRelativeFileName(TypeDefinition from, TypeDefinition to);
}
