package ai.evolv;

import ai.evolv.exceptions.AscendKeyError;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class Allocations {

    private static final String TOUCHED = "touched";
    private static final String CONFIRMED = "confirmed";
    private static final String CONTAMINATED = "contaminated";

    private static final Logger LOGGER = LoggerFactory.getLogger(Allocations.class);

    private final JsonArray allocations;
    private final AscendAllocationStore store;

    private final Audience audience = new Audience();

    Allocations(JsonArray allocations, AscendAllocationStore store) {
        this.allocations = allocations;
        this.store = store;
    }

    <T> T getValueFromAllocations(String key, Class<T> cls, AscendParticipant participant)
            throws AscendKeyError {
        ArrayList<String> keyParts = new ArrayList<>(Arrays.asList(key.split("\\.")));
        if (keyParts.isEmpty()) {
            throw new AscendKeyError("Key provided was empty.");
        }

        for (JsonElement a : allocations) {
            JsonObject allocation = a.getAsJsonObject();
            if (!audience.filter(participant.getUserAttributes(), allocation)) {
                try {
                    JsonElement element = getElementFromGenome(allocation.get("genome"), keyParts);
                    T value = new Gson().fromJson(element, cls);
                    if (value != null) {
                        LOGGER.debug(String.format("Found value for key '%s' in experiment %s",
                                key, allocation.get("eid").getAsString()));
                        markTouched(allocation);
                        store.put(participant.getUserId(), allocations);
                    }
                    return value;
                } catch (AscendKeyError e) {
                    LOGGER.debug(String.format("Unable to find key '%s' in experiment %s.",
                            key, allocation.get("eid").getAsString()));
                    continue;
                }
            }

            LOGGER.debug(String.format("Participant was filtered from experiment %s",
                    allocation.get("eid").getAsString()));
        }

        throw new AscendKeyError(String.format("No value was found in any allocations for key: %s",
                keyParts.toString()));
    }

    private JsonElement getElementFromGenome(JsonElement genome, List<String> keyParts)
            throws AscendKeyError {
        JsonElement element = genome;
        if (element == null) {
            throw new AscendKeyError("Allocation genome was empty.");
        }

        for (String part : keyParts) {
            JsonObject object = element.getAsJsonObject();
            element = object.get(part);
            if (element == null) {
                throw new AscendKeyError("Could not find value for key: " + keyParts.toString());
            }
        }

        return element;
    }

    /**
     * Reconciles the previous allocations with any new allocations.
     *
     * <p>
     *     Check the current allocations for any allocations that belong to experiments
     *     in the previous allocations. If there are, keep the previous allocations.
     *     If there are any live experiments that are not in the previous allocations
     *     add the new allocation to the allocations list.
     * </p>
     *
     * @param previousAllocations the stored allocations
     * @param currentAllocations the allocations recently fetched
     * @return the reconcile allocations
     */
    static JsonArray reconcileAllocations(JsonArray previousAllocations,
                                          JsonArray currentAllocations) {
        JsonArray allocations = new JsonArray();

        for (JsonElement ca : currentAllocations) {
            JsonObject currentAllocation = ca.getAsJsonObject();
            String currentEid = currentAllocation.get("eid").toString();
            boolean previousFound = false;

            for (JsonElement pa : previousAllocations) {
                JsonObject previousAllocation = pa.getAsJsonObject();
                String previousEid = previousAllocation.get("eid").toString();

                if (previousEid.equals(currentEid)) {
                    allocations.add(pa.getAsJsonObject());
                    previousFound = true;
                }
            }

            if (!previousFound) {
                allocations.add(ca.getAsJsonObject());
            }
        }

        return allocations;
    }

    Set<String> getActiveExperiments() {
        Set<String> activeExperiments = new HashSet<>();
        for (JsonElement a : allocations) {
            JsonObject allocation = a.getAsJsonObject();
            activeExperiments.add(allocation.get("eid").getAsString());
        }
        return activeExperiments;
    }

    static JsonObject markTouched(JsonObject allocation) {
        allocation.addProperty(TOUCHED, true);
        return allocation;
    }

    static boolean isTouched(JsonObject allocation) {
        return allocation.has(TOUCHED) &&
                allocation.get(TOUCHED).getAsBoolean();
    }

    static JsonObject markConfirmed(JsonObject allocation) {
        allocation.addProperty(CONFIRMED, true);
        return allocation;
    }

    static boolean isConfirmed(JsonObject allocation) {
        return allocation.has(CONFIRMED) &&
                allocation.get(CONFIRMED).getAsBoolean();
    }

    static JsonObject markContaminated(JsonObject allocation) {
        allocation.addProperty(CONTAMINATED, true);
        return allocation;
    }

    static boolean isContaminated(JsonObject allocation) {
        return allocation.has(CONTAMINATED) &&
                allocation.get(CONTAMINATED).getAsBoolean();
    }
}
