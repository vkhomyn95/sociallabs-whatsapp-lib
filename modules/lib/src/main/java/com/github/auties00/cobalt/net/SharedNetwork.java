package com.github.auties00.cobalt.net;

import com.github.auties00.vigil.ConnectivityMonitor;

import java.net.http.HttpClient;
import java.time.Duration;

/**
 * Network resources every client of the process shares, created on first use.
 *
 * @apiNote
 * A {@link HttpClient} owns a selector thread and a connection pool, and a {@link ConnectivityMonitor}
 * owns two platform threads watching the host's network; neither depends on the session. Shared, a
 * process with many linked sessions pays for them once instead of once per client. The monitor is
 * never closed: it lives as long as the process, and clients only add and remove their listeners.
 */
public final class SharedNetwork {

    private SharedNetwork() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Returns the process-wide connectivity monitor, started on first use.
     *
     * @return the shared monitor
     */
    public static ConnectivityMonitor connectivityMonitor() {
        return Monitor.INSTANCE;
    }

    /**
     * Returns the process-wide HTTP client with default settings.
     *
     * @return the shared client
     */
    public static HttpClient httpClient() {
        return Http.INSTANCE;
    }

    /**
     * Returns the process-wide HTTP client that follows redirects and gives up connecting after ten
     * seconds, as link previews need.
     *
     * @return the shared client
     */
    public static HttpClient redirectingHttpClient() {
        return RedirectingHttp.INSTANCE;
    }

    private static final class Monitor {
        private static final ConnectivityMonitor INSTANCE = ConnectivityMonitor.create().start();
    }

    private static final class Http {
        private static final HttpClient INSTANCE = HttpClient.newHttpClient();
    }

    private static final class RedirectingHttp {
        private static final HttpClient INSTANCE = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .build();
    }
}
