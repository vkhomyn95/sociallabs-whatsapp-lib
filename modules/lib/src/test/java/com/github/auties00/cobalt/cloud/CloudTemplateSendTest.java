package com.github.auties00.cobalt.cloud;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.github.auties00.cobalt.client.cloud.CloudWhatsAppClient;
import com.github.auties00.cobalt.store.cloud.CloudWhatsAppStoreFactory;
import com.github.auties00.cobalt.wire.cloud.CloudApiVersion;
import com.github.auties00.cobalt.wire.cloud.template.CloudMessageTemplate;
import com.github.auties00.cobalt.wire.cloud.template.CloudTemplateButtonSubType;
import com.github.auties00.cobalt.wire.cloud.template.CloudTemplateButtonUrlBuilder;
import com.github.auties00.cobalt.wire.cloud.template.CloudTemplateCategory;
import com.github.auties00.cobalt.wire.cloud.template.CloudTemplateComponent;
import com.github.auties00.cobalt.wire.cloud.template.CloudTemplateComponentBodyBuilder;
import com.github.auties00.cobalt.wire.cloud.template.CloudTemplateMediaType;
import com.github.auties00.cobalt.wire.cloud.template.CloudTemplateMessage;
import com.github.auties00.cobalt.wire.cloud.template.CloudTemplateMessageComponent;
import com.github.auties00.cobalt.wire.cloud.template.CloudTemplateParameter;
import com.github.auties00.cobalt.wire.cloud.template.CloudTemplateParameterFormat;
import com.github.auties00.cobalt.wire.core.jid.Jid;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Sends message templates natively and creates templates whose examples and variable format Cloud requires,
 * checked against the JSON sent through a {@link RecordingHttpClient}.
 */
@DisplayName("Cloud template sending")
class CloudTemplateSendTest {
    private static final String PHONE_ID = "1234567890";
    private static final String WABA_ID = "123456789012345";
    private static final String SENT = "{\"messages\":[{\"id\":\"wamid.T1\"}]}";

    private static CloudWhatsAppClient client(RecordingHttpClient http) throws Exception {
        return CloudWhatsAppClient.builder(CloudWhatsAppStoreFactory.temporary())
                .loadConnection("token", PHONE_ID)
                .apiVersion(CloudApiVersion.V23_0)
                .whatsappBusinessAccountId(WABA_ID)
                .httpClient(http)
                .build();
    }

    private static JSONObject body(RecordingHttpClient http) {
        return JSON.parseObject(http.lastBody());
    }

    @Test
    @DisplayName("a template without variables is sent with its name and language only")
    void withoutVariables() throws Exception {
        var http = new RecordingHttpClient();
        http.respondWith(SENT);
        var key = client(http).sendTemplate(Jid.of("380501111111"),
                new CloudTemplateMessage("hello_world", "en_US", null));
        assertEquals("wamid.T1", key.id().orElseThrow());
        assertTrue(http.lastUri().getPath().endsWith(PHONE_ID + "/messages"));
        var body = body(http);
        assertEquals("380501111111", body.getString("to"));
        assertEquals("template", body.getString("type"));
        var template = body.getJSONObject("template");
        assertEquals("hello_world", template.getString("name"));
        assertEquals("en_US", template.getJSONObject("language").getString("code"));
        assertFalse(template.containsKey("components"));
    }

    @Test
    @DisplayName("header media, named body values and button values keep every field")
    void everyKindOfValue() throws Exception {
        var http = new RecordingHttpClient();
        http.respondWith(SENT);
        var template = new CloudTemplateMessage("order_shipped", "uk", List.of(
                new CloudTemplateMessageComponent.Header(List.of(CloudTemplateParameter.Media.ofLink(
                        CloudTemplateMediaType.DOCUMENT, "https://example.com/invoice.pdf", "invoice.pdf"))),
                new CloudTemplateMessageComponent.Body(List.of(
                        new CloudTemplateParameter.Text("Anna", "first_name"),
                        new CloudTemplateParameter.Currency("$12.50", "USD", 12500),
                        new CloudTemplateParameter.DateTime("5 November"))),
                new CloudTemplateMessageComponent.Button(CloudTemplateButtonSubType.URL, 0, List.of(
                        new CloudTemplateParameter.Text("track/42"))),
                new CloudTemplateMessageComponent.Button(CloudTemplateButtonSubType.QUICK_REPLY, 1, List.of(
                        new CloudTemplateParameter.Payload("STOP"))),
                new CloudTemplateMessageComponent.Button(CloudTemplateButtonSubType.COPY_CODE, 2, List.of(
                        new CloudTemplateParameter.CouponCode("SAVE10")))));
        client(http).sendTemplate(Jid.of("380501111111"), template);

        var components = body(http).getJSONObject("template").getJSONArray("components");
        assertEquals(5, components.size());

        var header = components.getJSONObject(0);
        assertEquals("header", header.getString("type"));
        var document = header.getJSONArray("parameters").getJSONObject(0);
        assertEquals("document", document.getString("type"));
        assertEquals("https://example.com/invoice.pdf", document.getJSONObject("document").getString("link"));
        assertEquals("invoice.pdf", document.getJSONObject("document").getString("filename"));

        var body = components.getJSONObject(1).getJSONArray("parameters");
        assertEquals("first_name", body.getJSONObject(0).getString("parameter_name"));
        assertEquals("Anna", body.getJSONObject(0).getString("text"));
        assertEquals(12500, body.getJSONObject(1).getJSONObject("currency").getLongValue("amount_1000"));
        assertEquals("USD", body.getJSONObject(1).getJSONObject("currency").getString("code"));
        assertEquals("5 November", body.getJSONObject(2).getJSONObject("date_time").getString("fallback_value"));

        var url = components.getJSONObject(2);
        assertEquals("button", url.getString("type"));
        assertEquals("url", url.getString("sub_type"));
        assertEquals("0", url.getString("index"));
        assertEquals("track/42", url.getJSONArray("parameters").getJSONObject(0).getString("text"));
        assertFalse(url.getJSONArray("parameters").getJSONObject(0).containsKey("parameter_name"));

        var quickReply = components.getJSONObject(3);
        assertEquals("quick_reply", quickReply.getString("sub_type"));
        assertEquals("STOP", quickReply.getJSONArray("parameters").getJSONObject(0).getString("payload"));

        var coupon = components.getJSONObject(4).getJSONArray("parameters").getJSONObject(0);
        assertEquals("coupon_code", coupon.getString("type"));
        assertEquals("SAVE10", coupon.getString("coupon_code"));
    }

    @Test
    @DisplayName("an uploaded header image is referenced by id, a location header by its coordinates")
    void imageByIdAndLocation() throws Exception {
        var http = new RecordingHttpClient();
        http.respondWith(SENT);
        var client = client(http);
        client.sendTemplate(Jid.of("380501111111"), new CloudTemplateMessage("promo", "en", List.of(
                new CloudTemplateMessageComponent.Header(List.of(CloudTemplateParameter.Media.ofId(
                        CloudTemplateMediaType.IMAGE, "1013859600285441", null))))));
        var image = body(http).getJSONObject("template").getJSONArray("components").getJSONObject(0)
                .getJSONArray("parameters").getJSONObject(0);
        assertEquals("image", image.getString("type"));
        assertEquals("1013859600285441", image.getJSONObject("image").getString("id"));
        assertFalse(image.getJSONObject("image").containsKey("link"));

        client.sendTemplate(Jid.of("380501111111"), new CloudTemplateMessage("store", "en", List.of(
                new CloudTemplateMessageComponent.Header(List.of(
                        new CloudTemplateParameter.Location(50.45, 30.52, "Store", "Khreshchatyk 1"))))));
        var location = body(http).getJSONObject("template").getJSONArray("components").getJSONObject(0)
                .getJSONArray("parameters").getJSONObject(0).getJSONObject("location");
        assertEquals(50.45, location.getDoubleValue("latitude"));
        assertEquals("Khreshchatyk 1", location.getString("address"));
    }

    @Test
    @DisplayName("creating a template sends its examples as JSON and its variable format")
    void createWithExamples() throws Exception {
        var http = new RecordingHttpClient();
        http.respondWith("{\"id\":\"594425479261596\",\"status\":\"PENDING\",\"category\":\"MARKETING\"}");
        var body = new CloudTemplateComponentBodyBuilder()
                .text("Hi {{first_name}}, your code is ready")
                .example("{\"body_text_named_params\":[{\"param_name\":\"first_name\",\"example\":\"Anna\"}]}")
                .build();
        var button = new CloudTemplateButtonUrlBuilder()
                .text("Open")
                .url("https://example.com/{{1}}")
                .example("[\"https://example.com/summer\"]")
                .build();
        var created = client(http).createMessageTemplate(new CloudMessageTemplate(null, "code_ready", "en_US",
                CloudTemplateCategory.MARKETING, null,
                List.of(body, new CloudTemplateComponent.Buttons(List.of(button))),
                CloudTemplateParameterFormat.NAMED));

        assertEquals(CloudTemplateParameterFormat.NAMED, created.parameterFormat().orElseThrow());
        var sent = body(http);
        assertEquals("NAMED", sent.getString("parameter_format"));
        var components = sent.getJSONArray("components");
        var example = components.getJSONObject(0).getJSONObject("example");
        assertEquals("Anna", example.getJSONArray("body_text_named_params").getJSONObject(0).getString("example"));
        var urlButton = components.getJSONObject(1).getJSONArray("buttons").getJSONObject(0);
        assertEquals("https://example.com/summer", urlButton.getJSONArray("example").getString(0));
    }

    @Test
    @DisplayName("a listed template keeps its variable format and examples as JSON text")
    void listKeepsFormatAndExamples() throws Exception {
        var http = new RecordingHttpClient();
        http.respondWith("""
                {"data":[{"id":"1","name":"code_ready","language":"en_US","category":"MARKETING",
                  "status":"APPROVED","parameter_format":"POSITIONAL","components":[
                    {"type":"BODY","text":"Hi {{1}}","example":{"body_text":[["Anna"]]}},
                    {"type":"BUTTONS","buttons":[{"type":"URL","text":"Open",
                      "url":"https://example.com/{{1}}","example":["https://example.com/summer"]}]}]}]}
                """);
        var template = client(http).queryMessageTemplates().getFirst();
        assertEquals(CloudTemplateParameterFormat.POSITIONAL, template.parameterFormat().orElseThrow());
        var body = (CloudTemplateComponent.Body) template.components().getFirst();
        assertEquals("Anna", JSON.parseObject(body.example().orElseThrow())
                .getJSONArray("body_text").getJSONArray(0).getString(0));
        var buttons = (CloudTemplateComponent.Buttons) template.components().get(1);
        var url = (com.github.auties00.cobalt.wire.cloud.template.CloudTemplateButton.Url) buttons.buttons().getFirst();
        assertEquals("https://example.com/summer", JSON.parseArray(url.example().orElseThrow()).getString(0));
    }
}
