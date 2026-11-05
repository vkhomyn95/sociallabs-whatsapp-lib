package com.github.auties00.cobalt.wire.cloud.template;

import java.util.List;
import java.util.Objects;

/**
 * The values of the variables of one component of a template being sent.
 *
 * <p>The header and the body take their values in the order of their variables (or by name, for a template with
 * named variables); a button is addressed by its zero-based position among the template's buttons.
 */
public sealed interface CloudTemplateMessageComponent permits CloudTemplateMessageComponent.Header,
        CloudTemplateMessageComponent.Body, CloudTemplateMessageComponent.Button {

    /**
     * Returns the values of the component's variables.
     *
     * @return the parameters
     */
    List<CloudTemplateParameter> parameters();

    /**
     * The value of the header's variable: a text, a media file or a location.
     */
    final class Header implements CloudTemplateMessageComponent {
        /**
         * The header's parameters.
         */
        private final List<CloudTemplateParameter> parameters;

        /**
         * Constructs a new header component.
         *
         * @param parameters the header's parameters
         */
        public Header(List<CloudTemplateParameter> parameters) {
            this.parameters = parameters == null ? List.of() : List.copyOf(parameters);
        }

        @Override
        public List<CloudTemplateParameter> parameters() {
            return parameters;
        }
    }

    /**
     * The values of the body's variables.
     */
    final class Body implements CloudTemplateMessageComponent {
        /**
         * The body's parameters.
         */
        private final List<CloudTemplateParameter> parameters;

        /**
         * Constructs a new body component.
         *
         * @param parameters the body's parameters
         */
        public Body(List<CloudTemplateParameter> parameters) {
            this.parameters = parameters == null ? List.of() : List.copyOf(parameters);
        }

        @Override
        public List<CloudTemplateParameter> parameters() {
            return parameters;
        }
    }

    /**
     * The value of one button's variable: the suffix of a dynamic URL, the payload of a quick reply, a coupon code
     * or the token of a flow.
     */
    final class Button implements CloudTemplateMessageComponent {
        /**
         * The kind of button.
         */
        private final CloudTemplateButtonSubType subType;

        /**
         * The button's zero-based position among the template's buttons.
         */
        private final int index;

        /**
         * The button's parameters.
         */
        private final List<CloudTemplateParameter> parameters;

        /**
         * Constructs a new button component.
         *
         * @param subType    the kind of button
         * @param index      the button's zero-based position among the template's buttons
         * @param parameters the button's parameters
         * @throws NullPointerException     if {@code subType} is {@code null}
         * @throws IllegalArgumentException if {@code index} is negative
         */
        public Button(CloudTemplateButtonSubType subType, int index, List<CloudTemplateParameter> parameters) {
            this.subType = Objects.requireNonNull(subType, "subType must not be null");
            if (index < 0) {
                throw new IllegalArgumentException("index must not be negative: " + index);
            }
            this.index = index;
            this.parameters = parameters == null ? List.of() : List.copyOf(parameters);
        }

        /**
         * Returns the kind of button.
         *
         * @return the sub-type
         */
        public CloudTemplateButtonSubType subType() {
            return subType;
        }

        /**
         * Returns the button's zero-based position among the template's buttons.
         *
         * @return the index
         */
        public int index() {
            return index;
        }

        @Override
        public List<CloudTemplateParameter> parameters() {
            return parameters;
        }
    }
}
