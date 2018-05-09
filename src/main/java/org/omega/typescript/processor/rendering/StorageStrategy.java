package org.omega.typescript.processor.rendering;

import org.omega.typescript.processor.model.Endpoint;
import org.omega.typescript.processor.model.TypeDefinition;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by kibork on 5/2/2018.
 */
public interface StorageStrategy {
    PrintWriter createWriter(TypeDefinition definition) throws IOException;

    PrintWriter createWriter(String filename) throws IOException;

    File getFile(String filename);

    String getFileName(TypeDefinition definition);

    String getRelativeFileName(TypeDefinition from, TypeDefinition to);

    String getIncludeFileName(Endpoint endpoint);

    String getRelativeFileName(Endpoint endpoint, TypeDefinition to);

    String getFileName(Endpoint endpoint);

    PrintWriter createWriter(Endpoint endpoint) throws IOException;
}
