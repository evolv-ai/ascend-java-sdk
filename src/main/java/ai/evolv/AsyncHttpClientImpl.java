package ai.evolv;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Deprecated
public class AsyncHttpClientImpl
    extends ai.evolv.httpclients.AsyncHttpClient implements HttpClient {

    /**
     *
     * @param timeout The milliseconds before the client should timeout and return the default
     *                value.
     *
     * @deprecated use {@link ai.evolv.httpclients.AsyncHttpClient} instead.
     */
    public AsyncHttpClientImpl(long timeout) {
        super(TimeUnit.MILLISECONDS, Math.toIntExact(timeout));
    }

    /**
     * Performs a GET request with the given url using the client from
     * org.asynchttpclient.
     * @param url a valid url representing a call to the Participant API.
     * @return a Completable future instance containing a response from
     *     the API
     */
    public CompletableFuture<String> get(String url) {
        return getStringCompletableFuture(url, httpClient);
    }

}
