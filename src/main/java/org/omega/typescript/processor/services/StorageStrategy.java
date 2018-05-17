package org.omega.typescript.processor.services;

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

    PrintWriter createWriter(Endpoint endpoint) throws IOException;
}
