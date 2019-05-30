package ai.evolv;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.net.URI;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class EventEmitter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExecutionQueue.class);

    static final String CONFIRM_KEY = "confirmation";
    static final String CONTAMINATE_KEY = "contamination";

    private final HttpClient httpClient;
    private final AscendConfig config;
    private final AscendParticipant participant;

    private final Audience audience = new Audience();

    EventEmitter(AscendConfig config, AscendParticipant participant) {
        this.httpClient = config.getHttpClient();
        this.config = config;
        this.participant = participant;
    }

    void emit(String key) {
        String url = getEventUrl(key, 1.0);
        makeEventRequest(url);
    }

    void emit(String key, Double score) {
        String url = getEventUrl(key, score);
        makeEventRequest(url);
    }

    void confirm(JsonArray allocations) {
        sendAllocationEvents(CONFIRM_KEY, allocations);
    }

    void contaminate(JsonArray allocations)  {
        sendAllocationEvents(CONTAMINATE_KEY, allocations);
    }

    void sendAllocationEvents(String key, JsonArray allocations) {
        for (JsonElement a : allocations) {
            if (!audience.filter(participant.getUserAttributes(), a.getAsJsonObject())) {
                JsonObject allocation = a.getAsJsonObject();
                String experimentId = allocation.get("eid").getAsString();
                String candidateId = allocation.get("cid").getAsString();

                String url = getEventUrl(key, experimentId, candidateId);
                makeEventRequest(url);
                continue;
            }
            LOGGER.debug(String.format("%s event filtered.", key));
        }
    }

    String getEventUrl(String type, Double score) {
        try {
            String path = String.format("//%s/%s/%s/events", config.getDomain(),
                    config.getVersion(),
                    config.getEnvironmentId());
            String queryString = String.format("uid=%s&sid=%s&type=%s&score=%s",
                    participant.getUserId(),
                    participant.getSessionId(), type, score.toString());

            URI uri = new URI(config.getHttpScheme(), null, path, queryString,
                    null);

            URL url = uri.toURL();

            return url.toString();
        } catch (Exception e) {
            LOGGER.error("There was an error while creating the events url.", e);
            return null;
        }
    }

    String getEventUrl(String type, String experimentId, String candidateId) {
        try {
            String path = String.format("//%s/%s/%s/events", config.getDomain(),
                    config.getVersion(),
                    config.getEnvironmentId());
            String queryString = String.format("uid=%s&sid=%s&eid=%s&cid=%s&type=%s",
                    participant.getUserId(),
                    participant.getSessionId(), experimentId, candidateId, type);

            URI uri = new URI(config.getHttpScheme(), null, path, queryString,
                    null);

            URL url = uri.toURL();

            return url.toString();
        } catch (Exception e) {
            LOGGER.error("There was an error while creating the events url.", e);
            return null;
        }
    }

    private void makeEventRequest(String url) {
        if (url != null) {
            try {
                httpClient.get(url);
            } catch (Exception e) {
                LOGGER.error(String.format("There was an exception while making" +
                        " an event request with %s", url), e);
            }
        } else {
            LOGGER.debug("The event url was null, skipping event request.");
        }
    }

}
