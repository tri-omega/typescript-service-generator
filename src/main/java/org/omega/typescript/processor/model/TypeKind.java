package org.omega.typescript.processor.model;

/**
 * Created by kibork on 3/12/2018.
 */
public enum TypeKind {

    UNKNOWN(false),

    PRIMITIVE(false),
    INTERFACE(true),
    ENUM(false),

    GENERIC_PLACEHOLDER(false),

    COLLECTION(true),
    MAP(true);

    private final boolean hasTypeParams;

    public boolean hasTypeParams() {
        return hasTypeParams;
    }

    TypeKind(boolean hasTypeParams) {
        this.hasTypeParams = hasTypeParams;
    }
}
