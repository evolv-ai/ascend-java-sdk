package ai.evolv;

import com.google.gson.JsonArray;

public class DefaultAllocationStore implements AscendAllocationStore {

    private LruCache cache;

    public DefaultAllocationStore(int size) {
        this.cache = new LruCache(size);
    }

    @Override
    public JsonArray get(String uid) {
        return cache.getEntry(uid);
    }

    @Override
    public void put(String uid, JsonArray allocations) {
        cache.putEntry(uid, allocations);
    }
}
