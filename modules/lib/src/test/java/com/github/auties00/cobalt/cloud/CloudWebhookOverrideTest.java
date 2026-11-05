package com.github.auties00.cobalt.cloud;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.github.auties00.cobalt.client.cloud.CloudWhatsAppClient;
import com.github.auties00.cobalt.store.cloud.CloudWhatsAppStoreFactory;
import com.github.auties00.cobalt.wire.cloud.CloudApiVersion;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Exercises the alternate webhook callback operations (WhatsApp Business Account and phone-number
 * overrides) through a {@link RecordingHttpClient}.
 */
@DisplayName("Cloud webhook callback overrides")
class CloudWebhookOverrideTest {
    private static final String PHONE_ID = "1234567890";
    private static final String WABA_ID = "123456789012345";
    private static final String CALLBACK = "https://example.com/api/v1/public/whatsapp/bots/hook-1";

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

    @Nested
    @DisplayName("business account override")
    class BusinessAccount {
        @Test
        @DisplayName("subscribeApp posts the alternate callback and verify token")
        void subscribeWithOverride() throws Exception {
            var http = new RecordingHttpClient();
            http.respondWith("{\"success\":true}");
            client(http).subscribeApp(CALLBACK, "verify-1");
            assertEquals("POST", http.lastMethod());
            assertTrue(http.lastUri().getPath().endsWith(WABA_ID + "/subscribed_apps"));
            assertEquals(CALLBACK, body(http).getString("override_callback_uri"));
            assertEquals("verify-1", body(http).getString("verify_token"));
        }

        @Test
        @DisplayName("subscribeApp without arguments posts an empty body, removing the override")
        void subscribeWithoutOverride() throws Exception {
            var http = new RecordingHttpClient();
            http.respondWith("{\"success\":true}");
            client(http).subscribeApp();
            assertTrue(http.lastUri().getPath().endsWith(WABA_ID + "/subscribed_apps"));
            assertFalse(body(http).containsKey("override_callback_uri"));
        }

        @Test
        @DisplayName("querySubscribedApps reads the override beside the app data")
        void queryOverride() throws Exception {
            var http = new RecordingHttpClient();
            http.respondWith("{\"data\":[{\"whatsapp_business_api_data\":{\"id\":\"670843887433847\","
                    + "\"name\":\"Lucky Shrub\"},\"override_callback_uri\":\"" + CALLBACK + "\"}]}");
            var apps = client(http).querySubscribedApps();
            assertEquals(1, apps.size());
            assertEquals("670843887433847", apps.getFirst().id());
            assertEquals(CALLBACK, apps.getFirst().overrideCallbackUrl().orElseThrow());
        }

        @Test
        @DisplayName("querySubscribedApps leaves the override empty when the app uses its default")
        void queryWithoutOverride() throws Exception {
            var http = new RecordingHttpClient();
            http.respondWith("{\"data\":[{\"whatsapp_business_api_data\":{\"id\":\"1\",\"name\":\"App\"}}]}");
            var apps = client(http).querySubscribedApps();
            assertTrue(apps.getFirst().overrideCallbackUrl().isEmpty());
        }
    }

    @Nested
    @DisplayName("phone number override")
    class PhoneNumber {
        @Test
        @DisplayName("overridePhoneNumberWebhook posts the webhook configuration to the phone number")
        void override() throws Exception {
            var http = new RecordingHttpClient();
            http.respondWith("{\"success\":true}");
            client(http).overridePhoneNumberWebhook(CALLBACK, "verify-2");
            assertEquals("POST", http.lastMethod());
            assertTrue(http.lastUri().getPath().endsWith("/" + PHONE_ID));
            var configuration = body(http).getJSONObject("webhook_configuration");
            assertEquals(CALLBACK, configuration.getString("override_callback_uri"));
            assertEquals("verify-2", configuration.getString("verify_token"));
        }

        @Test
        @DisplayName("removePhoneNumberWebhookOverride posts an empty callback")
        void remove() throws Exception {
            var http = new RecordingHttpClient();
            http.respondWith("{\"success\":true}");
            client(http).removePhoneNumberWebhookOverride();
            assertTrue(http.lastUri().getPath().endsWith("/" + PHONE_ID));
            var configuration = body(http).getJSONObject("webhook_configuration");
            assertEquals("", configuration.getString("override_callback_uri"));
            assertFalse(configuration.containsKey("verify_token"));
        }

        @Test
        @DisplayName("queryWebhookConfiguration reads every level and resolves the effective callback")
        void query() throws Exception {
            var http = new RecordingHttpClient();
            http.respondWith("{\"webhook_configuration\":{\"phone_number\":\"https://phone.example/hook\","
                    + "\"whatsapp_business_account\":\"https://waba.example/hook\","
                    + "\"application\":\"https://app.example/hook\"},\"id\":\"" + PHONE_ID + "\"}");
            var configuration = client(http).queryWebhookConfiguration();
            assertTrue(http.lastUri().toString().contains("fields=webhook_configuration"));
            assertEquals("https://phone.example/hook", configuration.phoneNumber().orElseThrow());
            assertEquals("https://waba.example/hook", configuration.whatsappBusinessAccount().orElseThrow());
            assertEquals("https://app.example/hook", configuration.application().orElseThrow());
            assertEquals("https://phone.example/hook", configuration.effectiveCallbackUrl().orElseThrow());
        }

        @Test
        @DisplayName("queryWebhookConfiguration falls back to the application callback")
        void queryApplicationOnly() throws Exception {
            var http = new RecordingHttpClient();
            http.respondWith("{\"webhook_configuration\":{\"application\":\"https://app.example/hook\"}}");
            var configuration = client(http).queryWebhookConfiguration();
            assertTrue(configuration.phoneNumber().isEmpty());
            assertTrue(configuration.whatsappBusinessAccount().isEmpty());
            assertEquals("https://app.example/hook", configuration.effectiveCallbackUrl().orElseThrow());
        }

        @Test
        @DisplayName("a blank or over-long callback is rejected before any request")
        void invalidCallback() throws Exception {
            var http = new RecordingHttpClient();
            var client = client(http);
            assertThrows(IllegalArgumentException.class,
                    () -> client.overridePhoneNumberWebhook(" ", "verify"));
            assertThrows(IllegalArgumentException.class,
                    () -> client.overridePhoneNumberWebhook("https://example.com/" + "a".repeat(200), "verify"));
            assertThrows(NullPointerException.class,
                    () -> client.subscribeApp(CALLBACK, null));
            assertEquals(null, http.lastUri());
        }
    }
}
