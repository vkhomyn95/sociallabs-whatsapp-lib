package com.github.auties00.cobalt.cloud;

import com.alibaba.fastjson2.JSON;
import com.github.auties00.cobalt.client.cloud.CloudWhatsAppClient;
import com.github.auties00.cobalt.store.cloud.CloudWhatsAppStoreFactory;
import com.github.auties00.cobalt.wire.cloud.CloudApiVersion;
import com.github.auties00.cobalt.wire.cloud.phone.CloudBusinessProfileEdit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Reads and changes every field of the Cloud business profile through a {@link RecordingHttpClient}.
 */
@DisplayName("Cloud business profile")
class CloudBusinessProfileTest {
    private static final String PHONE_ID = "1234567890";

    private static CloudWhatsAppClient client(RecordingHttpClient http) throws Exception {
        return CloudWhatsAppClient.builder(CloudWhatsAppStoreFactory.temporary())
                .loadConnection("token", PHONE_ID)
                .apiVersion(CloudApiVersion.V23_0)
                .httpClient(http)
                .build();
    }

    @Test
    @DisplayName("the query returns about, vertical and the profile picture beside the shared fields")
    void query() throws Exception {
        var http = new RecordingHttpClient();
        http.respondWith("""
                {"data":[{"about":"Open 9-18","address":"Kyiv","description":"Plants","email":"a@b.c",
                  "websites":["https://a.example","https://b.example"],"vertical":"RETAIL",
                  "profile_picture_url":"https://pps.whatsapp.net/p.jpg","messaging_product":"whatsapp"}]}
                """);
        var profile = client(http).queryCloudBusinessProfile().orElseThrow();
        assertTrue(http.lastUri().getPath().endsWith(PHONE_ID + "/whatsapp_business_profile"));
        assertTrue(http.lastUri().getQuery().contains("profile_picture_url"));
        assertEquals("Open 9-18", profile.about().orElseThrow());
        assertEquals("RETAIL", profile.vertical().orElseThrow());
        assertEquals("https://pps.whatsapp.net/p.jpg", profile.profilePictureUrl().orElseThrow());
        assertEquals(List.of("https://a.example", "https://b.example"), profile.websites());
        assertEquals("Plants", profile.description().orElseThrow());
    }

    @Test
    @DisplayName("the query is empty when the server reports no profile")
    void queryEmpty() throws Exception {
        var http = new RecordingHttpClient();
        http.respondWith("{\"data\":[]}");
        assertTrue(client(http).queryCloudBusinessProfile().isEmpty());
    }

    @Test
    @DisplayName("an edit sends only the fields it changes")
    void edit() throws Exception {
        var http = new RecordingHttpClient();
        http.respondWith("{\"success\":true}");
        client(http).editCloudBusinessProfile(new CloudBusinessProfileEdit(
                "Back on Monday", null, null, null, List.of("https://a.example"), "RETAIL", "4::aW1hZ2U="));
        assertEquals("POST", http.lastMethod());
        var body = JSON.parseObject(http.lastBody());
        assertEquals("whatsapp", body.getString("messaging_product"));
        assertEquals("Back on Monday", body.getString("about"));
        assertEquals("RETAIL", body.getString("vertical"));
        assertEquals("4::aW1hZ2U=", body.getString("profile_picture_handle"));
        assertEquals("https://a.example", body.getJSONArray("websites").getString(0));
        assertFalse(body.containsKey("address"));
        assertFalse(body.containsKey("email"));
    }
}
