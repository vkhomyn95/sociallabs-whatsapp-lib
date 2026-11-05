package com.github.auties00.cobalt.wire.cloud.template;

/**
 * The kind of media in a template's header.
 */
public enum CloudTemplateMediaType {
    IMAGE("image"),
    VIDEO("video"),
    DOCUMENT("document");

    /**
     * The value of the parameter's {@code type} field.
     */
    private final String token;

    CloudTemplateMediaType(String token) {
        this.token = token;
    }

    /**
     * Returns the value of the parameter's {@code type} field.
     *
     * @return the token
     */
    public String token() {
        return token;
    }
}
