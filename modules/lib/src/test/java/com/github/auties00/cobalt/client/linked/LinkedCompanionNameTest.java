package com.github.auties00.cobalt.client.linked;

import com.github.auties00.cobalt.client.WhatsAppClient;
import com.github.auties00.cobalt.store.linked.LinkedWhatsAppStoreFactory;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

/** Options of a web companion: its push name and the name it is linked with. */
class LinkedCompanionNameTest {

    @Test
    void aWebCompanionKeepsItsOptionsTypeWhenNamed() throws IOException {
        var options = WhatsAppClient.builder()
                .linkedApi()
                .webClient(LinkedWhatsAppStoreFactory.temporary())
                .createConnection();

        var named = options.name("Shop");

        assertInstanceOf(LinkedWhatsAppClientBuilder.Options.Web.class, named);
    }

    @Test
    void theCompanionNameIsKeptForPairing() throws IOException {
        var client = WhatsAppClient.builder()
                .linkedApi()
                .webClient(LinkedWhatsAppStoreFactory.temporary())
                .createConnection()
                .name("Shop")
                .companionName("SocialLabs")
                .unregistered(LinkedWhatsAppClientVerificationHandler.Web.QrCode.toTerminal(request -> {
                    throw new UnsupportedOperationException("No passkey in tests");
                }));

        assertEquals(Optional.of("SocialLabs"), client.store().accountStore().companionName());
        assertEquals(Optional.of("Shop"), client.store().accountStore().name());
    }

    @Test
    void withoutACompanionNameTheDeviceOperatingSystemIsAnnounced() throws IOException {
        var options = WhatsAppClient.builder()
                .linkedApi()
                .webClient(LinkedWhatsAppStoreFactory.temporary())
                .createConnection();
        var client = options.unregistered(LinkedWhatsAppClientVerificationHandler.Web.QrCode.toTerminal(request -> {
            throw new UnsupportedOperationException("No passkey in tests");
        }));

        assertEquals(Optional.empty(), client.store().accountStore().companionName());
    }
}
