package com.github.auties00.cobalt.wire.cloud.phone;

import java.util.Optional;

/**
 * The webhook callback URLs that apply to a business phone number.
 *
 * <p>Meta resolves the callback of a phone number's webhook notifications in order: the
 * {@link #phoneNumber() phone-number override} when one is set, then the
 * {@link #whatsappBusinessAccount() WhatsApp Business Account override}, then the
 * {@link #application() application's default} configured in the App Dashboard. Each URL is present only
 * when the server reported it.
 */
public final class CloudWebhookConfiguration {
    /**
     * The phone-number override, or {@code null} when none is set.
     */
    private final String phoneNumber;

    /**
     * The WhatsApp Business Account override, or {@code null} when none is set.
     */
    private final String whatsappBusinessAccount;

    /**
     * The application's default callback, or {@code null} when not reported.
     */
    private final String application;

    /**
     * Constructs a new webhook configuration.
     *
     * @param phoneNumber             the phone-number override, or {@code null} when none is set
     * @param whatsappBusinessAccount the WhatsApp Business Account override, or {@code null} when none is set
     * @param application             the application's default callback, or {@code null} when not reported
     */
    public CloudWebhookConfiguration(String phoneNumber, String whatsappBusinessAccount, String application) {
        this.phoneNumber = phoneNumber;
        this.whatsappBusinessAccount = whatsappBusinessAccount;
        this.application = application;
    }

    /**
     * Returns the callback URL overridden for this phone number.
     *
     * @return an {@link Optional} carrying the URL, or empty when the phone number has no override
     */
    public Optional<String> phoneNumber() {
        return Optional.ofNullable(phoneNumber);
    }

    /**
     * Returns the callback URL overridden for the phone number's WhatsApp Business Account.
     *
     * @return an {@link Optional} carrying the URL, or empty when the account has no override
     */
    public Optional<String> whatsappBusinessAccount() {
        return Optional.ofNullable(whatsappBusinessAccount);
    }

    /**
     * Returns the application's default callback URL.
     *
     * @return an {@link Optional} carrying the URL, or empty when not reported
     */
    public Optional<String> application() {
        return Optional.ofNullable(application);
    }

    /**
     * Returns the callback URL Meta delivers this phone number's notifications to.
     *
     * @return an {@link Optional} carrying the most specific URL set, or empty when none was reported
     */
    public Optional<String> effectiveCallbackUrl() {
        return phoneNumber()
                .or(this::whatsappBusinessAccount)
                .or(this::application);
    }
}
