package ai.evolv;

import com.google.gson.JsonArray;

class LruCache {

    private MaxSizeHashMap<String, JsonArray> cache;

    LruCache(int cacheSize) {
        this.cache = new MaxSizeHashMap<>(cacheSize);
    }

    JsonArray getEntry(String key) {
        if (cache.containsKey(key)) {
            JsonArray entry = cache.get(key);
            cache.remove(key);
            cache.put(key, entry);
            return entry;
        }
        return new JsonArray();
    }

    void putEntry(String key, JsonArray value) {
        if (cache.containsKey(key)) {
            cache.remove(key);
            cache.put(key, value);
        } else {
            cache.put(key, value);
        }
    }

}
