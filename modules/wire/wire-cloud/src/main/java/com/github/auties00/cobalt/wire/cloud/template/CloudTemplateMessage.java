package com.github.auties00.cobalt.wire.cloud.template;

import java.util.List;
import java.util.Objects;

/**
 * An approved message template to send, with the values of its variables.
 *
 * <p>Encoded as the {@code template} object of a Cloud {@code /messages} request: the template's
 * {@link #name() name}, its {@link #languageCode() language} and the {@link #components() components} that fill its
 * header, body and button variables. A template without variables is sent with no components.
 */
public final class CloudTemplateMessage {
    /**
     * The template's name.
     */
    private final String name;

    /**
     * The code of the template's language, for example {@code en_US}.
     */
    private final String languageCode;

    /**
     * The values of the template's variables, by component.
     */
    private final List<CloudTemplateMessageComponent> components;

    /**
     * Constructs a new template message.
     *
     * @param name         the template's name
     * @param languageCode the code of the template's language, for example {@code en_US}
     * @param components   the values of the template's variables, or {@code null} for none
     * @throws NullPointerException if {@code name} or {@code languageCode} is {@code null}
     */
    public CloudTemplateMessage(String name, String languageCode, List<CloudTemplateMessageComponent> components) {
        this.name = Objects.requireNonNull(name, "name must not be null");
        this.languageCode = Objects.requireNonNull(languageCode, "languageCode must not be null");
        this.components = components == null ? List.of() : List.copyOf(components);
    }

    /**
     * Returns the template's name.
     *
     * @return the name
     */
    public String name() {
        return name;
    }

    /**
     * Returns the code of the template's language.
     *
     * @return the language code
     */
    public String languageCode() {
        return languageCode;
    }

    /**
     * Returns the values of the template's variables, by component.
     *
     * @return the components, empty when the template has no variables
     */
    public List<CloudTemplateMessageComponent> components() {
        return components;
    }
}
