package ai.evolv;

import com.google.gson.JsonArray;

public interface AscendAllocationStore {

    /**
     * Retrieves a JsonArray.
     * <p>
     *     Retrieves a JsonArray that represents the participant's allocations.
     *     If there are no stored allocations, should return an empty JsonArray.
     * </p>
     * @param uid the participant's unique id
     * @return an allocation if one exists else an empty JsonArray
     */
    JsonArray get(String uid);

    /**
     * Stores a JsonArray.
     * <p>
     *     Stores the given JsonArray.
     * </p>
     * @param uid the participant's unique id
     * @param allocations the participant's allocations
     */
    void put(String uid, JsonArray allocations);

}
