package ai.evolv;

import com.google.gson.JsonArray;

import org.junit.Assert;
import org.junit.Test;

public class DefaultAllocationStoreTest {

    private static final String rawAllocation = "[{\"uid\":\"test_uid\",\"sid\":\"test_sid\",\"eid\":\"test_eid\",\"cid\":\"test_cid\",\"genome\":{\"search\":{\"weighting\":{\"distance\":2.5,\"dealer_score\":2.5}},\"pages\":{\"all_pages\":{\"header_footer\":[\"blue\",\"white\"]},\"testing_page\":{\"megatron\":\"none\",\"header\":\"white\"}},\"algorithms\":{\"feature_importance\":false}},\"excluded\":false}]";

    @Test
    public void testEmptyStoreRGetsEmptyJsonArray() {
        AscendAllocationStore store = new DefaultAllocationStore(10);
        Assert.assertNotNull(store.get("test_user"));
        Assert.assertEquals(0, store.get("test_user").size());
        Assert.assertEquals(new JsonArray(), store.get("test_user"));
    }

    @Test
    public void testPutAndGetOnStore() {
        AscendAllocationStore store = new DefaultAllocationStore(10);
        JsonArray allocations = new AllocationsTest().parseRawAllocations(rawAllocation);
        store.put("test_user", allocations);
        JsonArray storedAllocations = store.get("test_user");
        Assert.assertNotNull(storedAllocations);
        Assert.assertNotEquals(new JsonArray(), storedAllocations);
        Assert.assertEquals(allocations, storedAllocations);
    }
}
