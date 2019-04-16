package ai.evolv.httpclients;

import ai.evolv.HttpClient;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.ConnectionPool;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;


public class OkHttpClient implements HttpClient {

    protected final okhttp3.OkHttpClient httpClient;

    /**
     * Initializes the OhHttp# httpClient.
     * <p>
     * Note: Default timeout is 1 second
     * </p>
     */
    public OkHttpClient() {
        this.httpClient = new okhttp3.OkHttpClient.Builder()
            .callTimeout(1, TimeUnit.SECONDS)
            .connectionPool(new ConnectionPool(3, 1000, TimeUnit.MILLISECONDS))
            .build();
    }

    /**
     * Initializes the OhHttp# httpClient.
     *
     * @param timeUnit Specify the unit of the timeout value.
     * @param timeout Specify a request timeout for the httpClient.
     */
    public OkHttpClient(TimeUnit timeUnit, long timeout) {
        this.httpClient = new okhttp3.OkHttpClient.Builder()
            .callTimeout(timeout, timeUnit)
            .connectionPool(new ConnectionPool(3, 1000, TimeUnit.MILLISECONDS))
            .build();
    }

    /**
     * Initializes the OhHttp# httpClient.
     *
     * @param httpClient An instance of okhttp3.OkHttpClient for Ascend to use.
     */
    public OkHttpClient(okhttp3.OkHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    /**
     * Performs a GET request with the given url using the httpClient from
     * okhttp3.
     * @param url a valid url representing a call to the Participant API.
     * @return a Completable future instance containing a response from
     *     the API
     */
    public CompletableFuture<String> get(String url) {
        return getStringCompletableFuture(url, httpClient);
    }

    protected static CompletableFuture<String> getStringCompletableFuture(
        String url, okhttp3.OkHttpClient httpClient) {
        CompletableFuture<String> responseFuture = new CompletableFuture<>();
        final Request request = new Request.Builder()
            .url(url)
            .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                responseFuture.completeExceptionally(e);
            }

            @Override
            public void onResponse(Call call, Response response) {
                String body = "";
                try (ResponseBody responseBody = response.body()) {
                    if (responseBody != null) {
                        body = responseBody.string();
                    }

                    if (!response.isSuccessful()) {
                        throw new IOException(String.format("Unexpected response "
                                + "when making GET request: %s using url: %s with body: %s",
                            response, request.url(), body));
                    }

                    responseFuture.complete(body);
                } catch (Exception e) {
                    responseFuture.completeExceptionally(e);
                }
            }
        });

        return responseFuture;
    }
}
