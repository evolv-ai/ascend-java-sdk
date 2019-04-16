package ai.evolv;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Deprecated
public class OkHttpClientImpl extends ai.evolv.httpclients.OkHttpClient implements HttpClient {

    /**
     * Initializes the OhHttp# client.
     *
     * @param timeout specify a request timeout for the client.
     *
     * @deprecated use {@link ai.evolv.httpclients.OkHttpClient} instead.
     */
    public OkHttpClientImpl(long timeout) {
        super(TimeUnit.MILLISECONDS, Math.toIntExact(timeout));
    }

    /**
     * Performs a GET request with the given url using the client from
     * okhttp3.
     *
     * @param url a valid url representing a call to the Participant API.
     * @return a Completable future instance containing a response from
     *     the API
     */
    public CompletableFuture<String> get(String url) {
        return getStringCompletableFuture(url, httpClient);
    }
}
