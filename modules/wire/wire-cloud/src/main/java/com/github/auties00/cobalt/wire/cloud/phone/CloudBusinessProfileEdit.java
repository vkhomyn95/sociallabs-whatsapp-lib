package com.github.auties00.cobalt.wire.cloud.phone;

import java.util.List;
import java.util.Optional;

/**
 * A change to the WhatsApp Business profile of a phone number. A field left {@code null} keeps its current value.
 *
 * <p>The profile picture is changed with the handle of a file uploaded through a resumable upload session.
 */
public final class CloudBusinessProfileEdit {
    private final String about;
    private final String address;
    private final String description;
    private final String email;
    private final List<String> websites;
    private final String vertical;
    private final String profilePictureHandle;

    /**
     * Constructs a business profile change.
     *
     * @param about                the text under the profile name, 1 to 139 characters, or {@code null}
     * @param address              the business address, or {@code null}
     * @param description          the business description, or {@code null}
     * @param email                the contact email, or {@code null}
     * @param websites             up to two websites, or {@code null} to keep them
     * @param vertical             the industry, for example {@code RETAIL}, or {@code null}
     * @param profilePictureHandle the handle of an uploaded picture, or {@code null}
     */
    public CloudBusinessProfileEdit(String about, String address, String description, String email,
                                    List<String> websites, String vertical, String profilePictureHandle) {
        this.about = about;
        this.address = address;
        this.description = description;
        this.email = email;
        this.websites = websites == null ? null : List.copyOf(websites);
        this.vertical = vertical;
        this.profilePictureHandle = profilePictureHandle;
    }

    /**
     * @return the new text under the profile name, or empty to keep it
     */
    public Optional<String> about() {
        return Optional.ofNullable(about);
    }

    /**
     * @return the new address, or empty to keep it
     */
    public Optional<String> address() {
        return Optional.ofNullable(address);
    }

    /**
     * @return the new description, or empty to keep it
     */
    public Optional<String> description() {
        return Optional.ofNullable(description);
    }

    /**
     * @return the new email, or empty to keep it
     */
    public Optional<String> email() {
        return Optional.ofNullable(email);
    }

    /**
     * @return the new websites, or empty to keep them
     */
    public Optional<List<String>> websites() {
        return Optional.ofNullable(websites);
    }

    /**
     * @return the new industry, or empty to keep it
     */
    public Optional<String> vertical() {
        return Optional.ofNullable(vertical);
    }

    /**
     * @return the handle of the new picture, or empty to keep it
     */
    public Optional<String> profilePictureHandle() {
        return Optional.ofNullable(profilePictureHandle);
    }
}
