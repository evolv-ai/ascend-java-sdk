package ai.evolv.httpclients;

import static org.asynchttpclient.Dsl.asyncHttpClient;
import static org.asynchttpclient.Dsl.config;

import ai.evolv.HttpClient;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.asynchttpclient.AsyncHandler;
import org.asynchttpclient.AsyncHttpClientConfig;
import org.asynchttpclient.HttpResponseBodyPart;
import org.asynchttpclient.HttpResponseHeaders;
import org.asynchttpclient.HttpResponseStatus;

public class AsyncHttpClient implements HttpClient {

    protected final org.asynchttpclient.AsyncHttpClient httpClient;

    /**
     * Create an HttpClient based on the org.asynchttpclient library.
     *
     * <p>Note: Default timeout is 1 second</p>
     */
    public AsyncHttpClient() {
        this.httpClient = asyncHttpClient(config()
            .setRequestTimeout(1000)
            .build());
    }

    /**
     * Create an HttpClient based on the org.asynchttpclient library.
     *
     * @param timeUnit The time unit of the timeout.
     * @param timeout The timeout to use.
     */
    public AsyncHttpClient(TimeUnit timeUnit, int timeout) {
        this.httpClient = asyncHttpClient(config()
            .setRequestTimeout(Math.toIntExact(timeUnit.toMillis(timeout)))
            .build());
    }

    /**
     * Create an HttpClient based on the org.asynchttpclient library.
     *
     * @param config An instance of an AsyncHttpClientConfig configuration for use by Ascend
     */
    public AsyncHttpClient(AsyncHttpClientConfig config) {
        this.httpClient = asyncHttpClient(config);
    }

    /**
     * Create an HttpClient based on the org.asynchttpclient library.
     *
     * @param client An instance of an AsyncHttpClient for use by Ascend
     */
    public AsyncHttpClient(org.asynchttpclient.AsyncHttpClient client) {
        this.httpClient = client;
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

    protected static CompletableFuture<String> getStringCompletableFuture(
        String url, org.asynchttpclient.AsyncHttpClient httpClient) {
        final CompletableFuture<String> responseFuture = new CompletableFuture<>();

        StringBuilder chunks = new StringBuilder();

        httpClient.prepareGet(url)
            .execute(new AsyncHandler<String>() {
                @Override
                public State onStatusReceived(HttpResponseStatus responseStatus)
                    throws Exception {
                    int code = responseStatus.getStatusCode();
                    if (code >= 200 && code < 300) {
                        return State.CONTINUE;
                    }
                    throw new IOException("The request returned a bad status code.");
                }

                @Override
                public State onHeadersReceived(HttpResponseHeaders headers) {
                    return State.CONTINUE;
                }

                @Override
                public State onBodyPartReceived(HttpResponseBodyPart bodyPart) {
                    String chunk = new String(bodyPart.getBodyPartBytes()).trim();
                    if (chunk.length() != 0) {
                        chunks.append(chunk);
                    }
                    return State.CONTINUE;
                }

                @Override
                public String onCompleted() {
                    String response = chunks.toString();
                    responseFuture.complete(response);
                    return response;
                }

                @Override
                public void onThrowable(Throwable t) {
                    responseFuture.completeExceptionally(t);
                }
            });

        return responseFuture;
    }

}
