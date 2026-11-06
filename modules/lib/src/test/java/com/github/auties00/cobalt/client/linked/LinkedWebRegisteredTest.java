package com.github.auties00.cobalt.client.linked;

import com.github.auties00.cobalt.client.WhatsAppClient;
import com.github.auties00.cobalt.store.linked.LinkedWhatsAppStoreFactory;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

/** A registered web session can be built again, as a process restoring its sessions does. */
class LinkedWebRegisteredTest {

    @Test
    void aRegisteredWebSessionBuildsAClient() throws IOException {
        var options = (LinkedWhatsAppClientBuilder.Options.Web) WhatsAppClient.builder()
                .linkedApi()
                .webClient(LinkedWhatsAppStoreFactory.temporary())
                .loadOrCreateConnection(UUID.randomUUID());
        var registered = options.unregistered(LinkedWhatsAppClientVerificationHandler.Web.QrCode.toTerminal(request -> {
            throw new UnsupportedOperationException("No passkey in tests");
        }));
        registered.store().accountStore().setRegistered(true);

        assertTrue(options.registered().isPresent());
        assertTrue(options.registered(LinkedWhatsAppClientVerificationHandler.Web.QrCode.toTerminal(request -> {
            throw new UnsupportedOperationException("No passkey in tests");
        })).isPresent());
    }
}
