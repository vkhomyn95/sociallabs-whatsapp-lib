package com.github.auties00.cobalt.client.linked;

import com.github.auties00.cobalt.client.WhatsAppClient;
import com.github.auties00.cobalt.net.SharedNetwork;
import com.github.auties00.cobalt.store.linked.LinkedWhatsAppStoreFactory;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

/** Clients share the process's network resources instead of starting their own. */
class LinkedSharedNetworkTest {

    private static final String MONITOR_THREADS = "vigil-";
    private static final String HTTP_THREADS = "HttpClient-";

    @Test
    void newClientsStartNoMonitorOrHttpThreadsOfTheirOwn() throws IOException {
        newClient();
        long monitorThreads = threadsNamed(MONITOR_THREADS);
        long httpThreads = threadsNamed(HTTP_THREADS);

        for (var i = 0; i < 5; i++) {
            newClient();
        }

        assertEquals(monitorThreads, threadsNamed(MONITOR_THREADS));
        assertEquals(httpThreads, threadsNamed(HTTP_THREADS));
    }

    @Test
    void theSharedResourcesAreCreatedOnce() {
        assertSame(SharedNetwork.connectivityMonitor(), SharedNetwork.connectivityMonitor());
        assertSame(SharedNetwork.httpClient(), SharedNetwork.httpClient());
        assertSame(SharedNetwork.redirectingHttpClient(), SharedNetwork.redirectingHttpClient());
    }

    private static void newClient() throws IOException {
        WhatsAppClient.builder()
                .linkedApi()
                .webClient(LinkedWhatsAppStoreFactory.temporary())
                .createConnection()
                .unregistered(LinkedWhatsAppClientVerificationHandler.Web.QrCode.toTerminal(request -> {
                    throw new UnsupportedOperationException("No passkey in tests");
                }));
    }

    private static long threadsNamed(String prefix) {
        return Thread.getAllStackTraces()
                .keySet()
                .stream()
                .filter(thread -> thread.getName().startsWith(prefix))
                .count();
    }
}
