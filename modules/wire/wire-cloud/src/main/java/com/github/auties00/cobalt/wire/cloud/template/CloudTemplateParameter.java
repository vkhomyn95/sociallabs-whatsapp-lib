package com.github.auties00.cobalt.wire.cloud.template;

import java.util.Objects;
import java.util.Optional;

/**
 * The value of one variable of a template being sent.
 *
 * <p>Text, currency and date values fill header and body variables; media and location values fill a header of
 * that format; payload, coupon-code and flow-action values fill buttons.
 */
public sealed interface CloudTemplateParameter permits CloudTemplateParameter.Text, CloudTemplateParameter.Currency,
        CloudTemplateParameter.DateTime, CloudTemplateParameter.Media, CloudTemplateParameter.Location,
        CloudTemplateParameter.Payload, CloudTemplateParameter.CouponCode, CloudTemplateParameter.FlowAction {

    /**
     * A text value, also the suffix of a dynamic URL button.
     */
    final class Text implements CloudTemplateParameter {
        /**
         * The text.
         */
        private final String text;

        /**
         * The name of the variable for a template with named variables, or {@code null}.
         */
        private final String parameterName;

        /**
         * Constructs a positional text value.
         *
         * @param text the text
         */
        public Text(String text) {
            this(text, null);
        }

        /**
         * Constructs a text value.
         *
         * @param text          the text
         * @param parameterName the name of the variable for a template with named variables, or {@code null}
         * @throws NullPointerException if {@code text} is {@code null}
         */
        public Text(String text, String parameterName) {
            this.text = Objects.requireNonNull(text, "text must not be null");
            this.parameterName = parameterName;
        }

        /**
         * Returns the text.
         *
         * @return the text
         */
        public String text() {
            return text;
        }

        /**
         * Returns the name of the variable.
         *
         * @return the name, or empty for a positional variable
         */
        public Optional<String> parameterName() {
            return Optional.ofNullable(parameterName);
        }
    }

    /**
     * An amount of money, shown localised.
     */
    final class Currency implements CloudTemplateParameter {
        /**
         * The text shown when the amount cannot be localised.
         */
        private final String fallbackValue;

        /**
         * The ISO 4217 code.
         */
        private final String code;

        /**
         * The amount multiplied by 1000.
         */
        private final long amount1000;

        /**
         * Constructs a currency value.
         *
         * @param fallbackValue the text shown when the amount cannot be localised
         * @param code          the ISO 4217 code
         * @param amount1000    the amount multiplied by 1000
         * @throws NullPointerException if {@code fallbackValue} or {@code code} is {@code null}
         */
        public Currency(String fallbackValue, String code, long amount1000) {
            this.fallbackValue = Objects.requireNonNull(fallbackValue, "fallbackValue must not be null");
            this.code = Objects.requireNonNull(code, "code must not be null");
            this.amount1000 = amount1000;
        }

        /**
         * Returns the text shown when the amount cannot be localised.
         *
         * @return the fallback value
         */
        public String fallbackValue() {
            return fallbackValue;
        }

        /**
         * Returns the ISO 4217 code.
         *
         * @return the code
         */
        public String code() {
            return code;
        }

        /**
         * Returns the amount multiplied by 1000.
         *
         * @return the amount
         */
        public long amount1000() {
            return amount1000;
        }
    }

    /**
     * A date and time, sent as text.
     */
    final class DateTime implements CloudTemplateParameter {
        /**
         * The text shown.
         */
        private final String fallbackValue;

        /**
         * Constructs a date-time value.
         *
         * @param fallbackValue the text shown
         * @throws NullPointerException if {@code fallbackValue} is {@code null}
         */
        public DateTime(String fallbackValue) {
            this.fallbackValue = Objects.requireNonNull(fallbackValue, "fallbackValue must not be null");
        }

        /**
         * Returns the text shown.
         *
         * @return the fallback value
         */
        public String fallbackValue() {
            return fallbackValue;
        }
    }

    /**
     * The image, video or document of a media header.
     */
    final class Media implements CloudTemplateParameter {
        /**
         * The kind of media.
         */
        private final CloudTemplateMediaType type;

        /**
         * A public link to the file, or {@code null} when {@link #id} is set.
         */
        private final String link;

        /**
         * The id of an uploaded file, or {@code null} when {@link #link} is set.
         */
        private final String id;

        /**
         * The name a document is shown with, or {@code null}.
         */
        private final String fileName;

        private Media(CloudTemplateMediaType type, String link, String id, String fileName) {
            this.type = Objects.requireNonNull(type, "type must not be null");
            this.link = link;
            this.id = id;
            this.fileName = fileName;
        }

        /**
         * Returns a media value WhatsApp downloads from a public link.
         *
         * @param type     the kind of media
         * @param link     the link
         * @param fileName the name a document is shown with, or {@code null}
         * @return the media value
         * @throws NullPointerException if {@code type} or {@code link} is {@code null}
         */
        public static Media ofLink(CloudTemplateMediaType type, String link, String fileName) {
            return new Media(type, Objects.requireNonNull(link, "link must not be null"), null, fileName);
        }

        /**
         * Returns a media value referencing an uploaded file.
         *
         * @param type     the kind of media
         * @param id       the media id
         * @param fileName the name a document is shown with, or {@code null}
         * @return the media value
         * @throws NullPointerException if {@code type} or {@code id} is {@code null}
         */
        public static Media ofId(CloudTemplateMediaType type, String id, String fileName) {
            return new Media(type, null, Objects.requireNonNull(id, "id must not be null"), fileName);
        }

        /**
         * Returns the kind of media.
         *
         * @return the type
         */
        public CloudTemplateMediaType type() {
            return type;
        }

        /**
         * Returns the public link to the file.
         *
         * @return the link, or empty when the file is referenced by id
         */
        public Optional<String> link() {
            return Optional.ofNullable(link);
        }

        /**
         * Returns the id of the uploaded file.
         *
         * @return the id, or empty when the file is referenced by link
         */
        public Optional<String> id() {
            return Optional.ofNullable(id);
        }

        /**
         * Returns the name a document is shown with.
         *
         * @return the file name, or empty when none
         */
        public Optional<String> fileName() {
            return Optional.ofNullable(fileName);
        }
    }

    /**
     * The place of a location header.
     */
    final class Location implements CloudTemplateParameter {
        private final double latitude;
        private final double longitude;
        private final String name;
        private final String address;

        /**
         * Constructs a location value.
         *
         * @param latitude  degrees
         * @param longitude degrees
         * @param name      the place's name, or {@code null}
         * @param address   the place's address, or {@code null}
         */
        public Location(double latitude, double longitude, String name, String address) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.name = name;
            this.address = address;
        }

        /**
         * @return the latitude in degrees
         */
        public double latitude() {
            return latitude;
        }

        /**
         * @return the longitude in degrees
         */
        public double longitude() {
            return longitude;
        }

        /**
         * @return the place's name, or empty when none
         */
        public Optional<String> name() {
            return Optional.ofNullable(name);
        }

        /**
         * @return the place's address, or empty when none
         */
        public Optional<String> address() {
            return Optional.ofNullable(address);
        }
    }

    /**
     * The payload a quick-reply button returns when pressed.
     */
    final class Payload implements CloudTemplateParameter {
        private final String payload;

        /**
         * Constructs a payload value.
         *
         * @param payload the payload
         * @throws NullPointerException if {@code payload} is {@code null}
         */
        public Payload(String payload) {
            this.payload = Objects.requireNonNull(payload, "payload must not be null");
        }

        /**
         * @return the payload
         */
        public String payload() {
            return payload;
        }
    }

    /**
     * The code a copy-code button copies.
     */
    final class CouponCode implements CloudTemplateParameter {
        private final String code;

        /**
         * Constructs a coupon-code value.
         *
         * @param code the code, at most 15 characters
         * @throws NullPointerException if {@code code} is {@code null}
         */
        public CouponCode(String code) {
            this.code = Objects.requireNonNull(code, "code must not be null");
        }

        /**
         * @return the code
         */
        public String code() {
            return code;
        }
    }

    /**
     * The action of a flow button.
     */
    final class FlowAction implements CloudTemplateParameter {
        private final String flowToken;

        /**
         * Constructs a flow-action value.
         *
         * @param flowToken the token identifying this flow session, or {@code null} for the default
         */
        public FlowAction(String flowToken) {
            this.flowToken = flowToken;
        }

        /**
         * @return the token identifying this flow session, or empty for the default
         */
        public Optional<String> flowToken() {
            return Optional.ofNullable(flowToken);
        }
    }
}
