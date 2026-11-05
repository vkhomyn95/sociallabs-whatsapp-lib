package com.github.auties00.cobalt.wire.cloud.phone;

import java.util.List;
import java.util.Optional;

/**
 * The WhatsApp Business profile of a phone number, as the Cloud API reports it.
 *
 * <p>Unlike the linked {@code BusinessProfile}, this carries every field of the Cloud
 * {@code whatsapp_business_profile} edge: the "about" text, the industry ({@linkplain #vertical() vertical}) and
 * the address of the profile picture.
 */
public final class CloudBusinessProfile {
    private final String about;
    private final String address;
    private final String description;
    private final String email;
    private final List<String> websites;
    private final String vertical;
    private final String profilePictureUrl;

    /**
     * Constructs a business profile.
     *
     * @param about             the text under the profile name, or {@code null}
     * @param address           the business address, or {@code null}
     * @param description       the business description, or {@code null}
     * @param email             the contact email, or {@code null}
     * @param websites          the websites, or {@code null} for none
     * @param vertical          the industry, for example {@code RETAIL}, or {@code null}
     * @param profilePictureUrl a temporary address of the profile picture, or {@code null}
     */
    public CloudBusinessProfile(String about, String address, String description, String email,
                                List<String> websites, String vertical, String profilePictureUrl) {
        this.about = about;
        this.address = address;
        this.description = description;
        this.email = email;
        this.websites = websites == null ? List.of() : List.copyOf(websites);
        this.vertical = vertical;
        this.profilePictureUrl = profilePictureUrl;
    }

    /**
     * @return the text under the profile name, or empty when not set
     */
    public Optional<String> about() {
        return Optional.ofNullable(about);
    }

    /**
     * @return the business address, or empty when not set
     */
    public Optional<String> address() {
        return Optional.ofNullable(address);
    }

    /**
     * @return the business description, or empty when not set
     */
    public Optional<String> description() {
        return Optional.ofNullable(description);
    }

    /**
     * @return the contact email, or empty when not set
     */
    public Optional<String> email() {
        return Optional.ofNullable(email);
    }

    /**
     * @return the websites, empty when none
     */
    public List<String> websites() {
        return websites;
    }

    /**
     * @return the industry, for example {@code RETAIL}, or empty when not set
     */
    public Optional<String> vertical() {
        return Optional.ofNullable(vertical);
    }

    /**
     * @return a temporary address of the profile picture, or empty when there is none
     */
    public Optional<String> profilePictureUrl() {
        return Optional.ofNullable(profilePictureUrl);
    }
}
