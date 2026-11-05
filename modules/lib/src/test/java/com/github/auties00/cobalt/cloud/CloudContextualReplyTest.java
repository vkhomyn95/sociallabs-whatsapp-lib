package com.github.auties00.cobalt.cloud;

import com.alibaba.fastjson2.JSONObject;
import com.github.auties00.cobalt.wire.core.jid.Jid;
import com.github.auties00.cobalt.wire.core.message.MessageKeyBuilder;
import com.github.auties00.cobalt.wire.linked.message.LinkedMessageContainer;
import com.github.auties00.cobalt.wire.linked.message.context.ContextInfoBuilder;
import com.github.auties00.cobalt.wire.linked.message.media.DocumentMessageBuilder;
import com.github.auties00.cobalt.wire.linked.message.media.ImageMessageBuilder;
import com.github.auties00.cobalt.wire.linked.message.text.ExtendedTextMessageBuilder;
import com.github.auties00.cobalt.wire.linked.message.text.ReactionMessageBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Contextual replies ({@code context.message_id}) and document file names in the Cloud {@code /messages} body.
 */
@DisplayName("CloudMessageEncoder contextual replies and document file names")
class CloudContextualReplyTest {
    private static final Jid RECIPIENT = Jid.of("15551234567");
    private static final String QUOTED = "wamid.HBgLMTY1MDM4Nzk0MzkVAgARGBJDQjZCMzlEQUE4OTJBMTE4RTUA";

    private static JSONObject encode(LinkedMessageContainer container) {
        return CloudMessageEncoder.encode(RECIPIENT, container);
    }

    @Test
    @DisplayName("a text quoting a message carries its id as context.message_id")
    void textReply() {
        var text = new ExtendedTextMessageBuilder()
                .text("Yes, it is in stock")
                .contextInfo(new ContextInfoBuilder().quotedMessageId(QUOTED).build())
                .build();

        var body = encode(LinkedMessageContainer.of(text));

        assertEquals(QUOTED, body.getJSONObject("context").getString("message_id"));
        assertEquals("Yes, it is in stock", body.getJSONObject("text").getString("body"));
    }

    @Test
    @DisplayName("a media message quoting a message carries the context too")
    void imageReply() {
        var image = new ImageMessageBuilder()
                .mediaUrl("https://cdn.example/a.jpg")
                .contextInfo(new ContextInfoBuilder().quotedMessageId(QUOTED).build())
                .build();

        assertEquals(QUOTED, encode(LinkedMessageContainer.of(image)).getJSONObject("context").getString("message_id"));
    }

    @Test
    @DisplayName("a message quoting nothing has no context")
    void noReply() {
        var text = new ExtendedTextMessageBuilder().text("Hi").build();

        assertFalse(encode(LinkedMessageContainer.of(text)).containsKey("context"));
    }

    @Test
    @DisplayName("a reaction never carries a context, which the Cloud API refuses")
    void reactionHasNoContext() {
        var reaction = new ReactionMessageBuilder()
                .key(new MessageKeyBuilder().id(QUOTED).build())
                .text("👍")
                .build();

        var body = encode(LinkedMessageContainer.of(reaction));

        assertFalse(body.containsKey("context"));
        assertEquals(QUOTED, body.getJSONObject("reaction").getString("message_id"));
    }

    @Test
    @DisplayName("a document is sent with its file name")
    void documentFileName() {
        var document = new DocumentMessageBuilder()
                .mediaUrl("1376223850470843")
                .fileName("order_abc123.pdf")
                .caption("Your order confirmation")
                .build();

        var node = encode(LinkedMessageContainer.of(document)).getJSONObject("document");

        assertEquals("1376223850470843", node.getString("id"));
        assertEquals("order_abc123.pdf", node.getString("filename"));
        assertEquals("Your order confirmation", node.getString("caption"));
    }

    @Test
    @DisplayName("a document without a file name has none")
    void documentWithoutFileName() {
        var document = new DocumentMessageBuilder().mediaUrl("https://cdn.example/a.pdf").build();

        assertFalse(encode(LinkedMessageContainer.of(document)).getJSONObject("document").containsKey("filename"));
    }
}
