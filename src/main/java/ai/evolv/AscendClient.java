package ai.evolv;

public interface AscendClient {

    /**
     * Retrieves a value from the participant's allocation, returns a default upon error.
     * <p>
     *     Given a unique key this method will retrieve the key's associated value. A default value
     *     can also be specified in case any errors occur during the values retrieval. If the allocation
     *     call times out or fails the default value is always returned. This method is blocking, it will wait
     *     till the allocation is available and then return.
     * </p>
     * @param key a unique key identifying a specific value in the participants allocation
     * @param defaultValue a default value to return upon error
     * @return a value associated with the given key
     */
    <T> T get(String key, T defaultValue);

    /**
     * Retrieves a value from Ascend asynchronously and applies some custom action.
     * <p>
     *     This method is non blocking. It will preform the programmed action once the allocation
     *     is available. If there is already of stored allocation it will immediately apply the
     *     value retrieved and then when the new allocation returns it will reapply the new changes
     *     if the experiment has changed.
     * </p>
     * @param key a unique key identifying a specific value in the participants allocation
     * @param defaultValue a default value to return upon error
     * @param function a handler that is invoked when the allocation is updated
     */
    <T> void subscribe(String key, T defaultValue, AscendAction<T> function);

    /**
     * Emits a generic event to be recorded by Ascend.
     * <p>
     *     Sends an event to Ascend to be recorded and reported upon. Also records a generic score
     *     value to be associated with the event.
     * </p>
     * @param key the identifier of the event
     * @param score a score to be associated with the event
     */
    void emitEvent(String key, Double score);

    /**
     * Emits a generic event to be recorded by Ascend.
     * <p>
     *     Sends an event to Ascend to be recorded and reported upon.
     * </p>
     * @param key the identifier of the event
     */
    void emitEvent(String key);

    /**
     * Sends a confirmed event to Ascend.
     * <p>
     *     Method produces a confirmed event which confirms the participant's allocation. Method will not do anything
     *     in the event that the allocation timed out or failed.
     * </p>
     */
    void confirm();

    /**
     * Sends a contamination event to Ascend.
     * <p>
     *     Method produces a contamination event which will contaminate the participant's
     *     allocation. Method will not do anything in the event that the allocation timed out or failed.
     * </p>
     */
    void contaminate();

}
