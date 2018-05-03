package org.omega.typescript.processor.rendering;

import org.omega.typescript.processor.model.Endpoint;
import org.omega.typescript.processor.model.TypeDefinition;

import java.io.BufferedWriter;
import java.io.IOException;

/**
 * Created by kibork on 5/2/2018.
 */
public interface StorageStrategy {
    BufferedWriter createWriter(TypeDefinition definition) throws IOException;

    String getFileName(TypeDefinition definition);

    String getRelativeFileName(TypeDefinition from, TypeDefinition to);

    String getIncludeFileName(Endpoint endpoint);

    String getRelativeFileName(Endpoint endpoint, TypeDefinition to);

    String getFileName(Endpoint endpoint);

    BufferedWriter createWriter(Endpoint endpoint) throws IOException;
}
