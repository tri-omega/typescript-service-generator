package org.omega.typescript.processor.services;

import org.omega.typescript.processor.model.Endpoint;
import org.omega.typescript.processor.model.TypeDefinition;

/**
 * Created by kibork on 5/16/2018.
 */
public interface FileNamingStrategy {
    String getFullTypeFileName(TypeDefinition definition);

    String getRelativeFileName(Endpoint endpoint, String toTargetFolder);

    String getRelativeFileName(TypeDefinition from, TypeDefinition to);

    String getIncludeFileName(Endpoint endpoint);

    String getRelativeFileName(Endpoint endpoint, TypeDefinition to);

    String getGetFullFileName(Endpoint endpoint);

    String getFullFileName(String fileName);

    String getFullModuleName(String moduleName);
}
