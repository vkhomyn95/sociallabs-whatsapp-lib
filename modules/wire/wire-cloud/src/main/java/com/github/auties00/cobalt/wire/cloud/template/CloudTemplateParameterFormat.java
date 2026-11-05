package com.github.auties00.cobalt.wire.cloud.template;

import java.util.Locale;

/**
 * How the variables of a template are written.
 */
public enum CloudTemplateParameterFormat {
    /**
     * Numbered variables, {@code {{1}}}, filled in order.
     */
    POSITIONAL,

    /**
     * Named variables, {@code {{first_name}}}, filled by name.
     */
    NAMED;

    /**
     * Returns the format with the given Cloud token.
     *
     * @param token the value of {@code parameter_format}, in any case
     * @return the format, or {@code null} when {@code token} is {@code null} or unknown
     */
    public static CloudTemplateParameterFormat of(String token) {
        if (token == null) {
            return null;
        }
        return switch (token.toUpperCase(Locale.ROOT)) {
            case "POSITIONAL" -> POSITIONAL;
            case "NAMED" -> NAMED;
            default -> null;
        };
    }
}
