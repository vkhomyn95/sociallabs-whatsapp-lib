package com.github.auties00.cobalt.wire.cloud.template;

/**
 * The kind of a template button whose variable is filled when sending.
 */
public enum CloudTemplateButtonSubType {
    /**
     * A quick-reply button; its parameter is the payload returned when pressed.
     */
    QUICK_REPLY("quick_reply"),

    /**
     * A link button with a dynamic suffix; its parameter is the suffix.
     */
    URL("url"),

    /**
     * A copy-code button; its parameter is the coupon code.
     */
    COPY_CODE("copy_code"),

    /**
     * A flow button; its parameter is the flow's action.
     */
    FLOW("flow");

    /**
     * The value of the {@code sub_type} field.
     */
    private final String token;

    CloudTemplateButtonSubType(String token) {
        this.token = token;
    }

    /**
     * Returns the value of the {@code sub_type} field.
     *
     * @return the token
     */
    public String token() {
        return token;
    }
}
