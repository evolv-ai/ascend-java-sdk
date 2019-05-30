package ai.evolv;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import org.junit.Assert;
import org.junit.Test;

public class LruCacheTest {

    private final String rawAllocation = "[{\"uid\":\"test_key\",\"sid\":\"test_sid\",\"eid\":\"test_eid\",\"cid\":\"test_cid\",\"genome\":{\"search\":{\"weighting\":{\"distance\":2.5,\"dealer_score\":2.5}},\"pages\":{\"all_pages\":{\"header_footer\":[\"blue\",\"white\"]},\"testing_page\":{\"megatron\":\"none\",\"header\":\"white\"}},\"algorithms\":{\"feature_importance\":false}},\"excluded\":false}]";

    @Test
    public void testGetEntryEmptyCache() {
        int testCacheSize = 10;
        String testKey = "test_key";

        LruCache cache = new LruCache(testCacheSize);
        JsonArray entry = cache.getEntry(testKey);

        Assert.assertNotNull(entry);
        Assert.assertEquals(0, entry.size());
    }

    @Test
    public void testGetEntry() {
        int testCacheSize = 10;
        String testKey = "test_key";
        JsonArray testEntry = new JsonParser().parse(rawAllocation).getAsJsonArray();

        LruCache cache = new LruCache(testCacheSize);
        cache.putEntry(testKey, testEntry);
        JsonArray entry = cache.getEntry(testKey);

        Assert.assertNotNull(entry);
        Assert.assertNotEquals(0, entry.size());
        Assert.assertEquals(testEntry, entry);
    }

    @Test
    public void testEvictEntry() {
        int testCacheSize = 3;
        String keyOne = "key_one";
        String keyTwo = "key_two";
        String keyThree = "Key_three";
        String keyFour = "key_four";
        JsonArray testEntry = new JsonParser().parse(rawAllocation).getAsJsonArray();

        LruCache cache = new LruCache(testCacheSize);

        cache.putEntry(keyOne, testEntry);
        cache.putEntry(keyTwo, testEntry);
        cache.putEntry(keyThree, testEntry);

        JsonArray entryOne = cache.getEntry(keyOne);
        JsonArray entryTwo = cache.getEntry(keyTwo);
        JsonArray entryThree = cache.getEntry(keyThree);

        cache.putEntry(keyFour, testEntry);
        JsonArray entryFour = cache.getEntry(keyFour);

        JsonArray evictedEntry = cache.getEntry(keyOne);

        Assert.assertEquals(testEntry, entryOne);
        Assert.assertEquals(testEntry, entryTwo);
        Assert.assertEquals(testEntry, entryThree);
        Assert.assertEquals(testEntry, entryFour);
        Assert.assertEquals(0, evictedEntry.size());
    }

    @Test
    public void testPutEntryTwice() {
        int testCacheSize = 10;
        String testKey = "test_key";
        JsonArray testEntry = new JsonParser().parse(rawAllocation).getAsJsonArray();

        LruCache cache = new LruCache(testCacheSize);
        cache.putEntry(testKey, testEntry);
        cache.putEntry(testKey, testEntry);
        JsonArray entry = cache.getEntry(testKey);

        Assert.assertNotNull(entry);
        Assert.assertNotEquals(0, entry.size());
        Assert.assertEquals(testEntry, entry);
    }
}
