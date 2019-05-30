package ai.evolv;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class AudienceTest {

    private String rawAllocation = "{\"uid\":\"test\",\"eid\":\"6b27857a80\",\"cid\":\"54e5d3dacb108d1c052906ca97f726b4:6b27857a80\",\"genome\":{\"navigation\":[{\"page\":\"CONTACT_INFO\",\"flow_sequence\":1},{\"page\":\"ACCOUNT_INFO\",\"flow_sequence\":2},{\"page\":\"DELIVERY\",\"flow_sequence\":3},{\"page\":\"PAYMENT\",\"flow_sequence\":4,\"credit_check\":true},{\"page\":\"REVIEW\",\"flow_sequence\":5}]},\"excluded\":false}";
    private JsonObject allocation = new JsonParser().parse(rawAllocation).getAsJsonObject();

    @Test
    public void testFilterExcluded() {
        JsonObject allocationExcluded = allocation.deepCopy();
        allocationExcluded.addProperty("excluded", true);

        Map<String, String> userAttributes = new HashMap<>();
        userAttributes.put("country", "us");
        userAttributes.put("post_code", "94110");

        Audience audience = new Audience();
        boolean filter = audience.filter(userAttributes, allocationExcluded);
        Assert.assertTrue(filter);
    }

    @Test
    public void testFilterNoAudienceQuery() {
        Map<String, String> userAttributes = new HashMap<>();
        userAttributes.put("country", "us");
        userAttributes.put("post_code", "94110");

        Audience audience = new Audience();
        boolean filter = audience.filter(userAttributes, allocation);
        Assert.assertFalse(filter);
    }

    @Test
    public void testFilterNullAudienceQuery() {
        JsonObject allocationNullAudienceQuery = allocation.deepCopy();
        allocationNullAudienceQuery.add("audience_query", null);

        Map<String, String> userAttributes = new HashMap<>();
        userAttributes.put("country", "us");
        userAttributes.put("post_code", "94110");

        Audience audience = new Audience();
        boolean filter = audience.filter(userAttributes, allocation);
        Assert.assertFalse(filter);
    }

    @Test
    public void testFilterNullUserAttributes() {
        Audience audience = new Audience();
        boolean filter = audience.filter(null, allocation);
        Assert.assertFalse(filter);
    }

    @Test
    public void testFilterEmptyUserAttributes() {
        Map<String, String> userAttributes = new HashMap<>();

        Audience audience = new Audience();
        boolean filter = audience.filter(userAttributes, allocation);
        Assert.assertFalse(filter);
    }

    @Test
    public void testFilterOneGroupKeyValueEqualAnd() {
        String rawAudienceQuery = "{\"combinator\": \"and\", \"rules\": [{\"value\": [\"country\", \"us\"], \"operator\": \"kv_equal\", \"field\": \"user_attributes\", \"index\": 0, \"id\": \"r-c4cfdaca-b3f1-45ae-81a9-910ebb507e99\"}], \"id\": \"g-2a3335a3-b470-4b88-bf3c-23d8a06eb27d\"}";
        JsonObject audienceQuery = new JsonParser().parse(rawAudienceQuery).getAsJsonObject();

        JsonObject allocationWithAudienceQuery = allocation.deepCopy();
        allocationWithAudienceQuery.add("audience_query", audienceQuery);

        Audience audience = new Audience();
        boolean filter;
        Map<String, String> userAttributes;

        // if participant does not have a user attribute matching the audience query they should be filtered
        userAttributes = new HashMap<>();
        userAttributes.put("post_code", "94110");

        filter = audience.filter(userAttributes, allocationWithAudienceQuery);
        Assert.assertTrue(filter);

        // if participant does not have a key value matching the audience query they should be filtered
        userAttributes = new HashMap<>();
        userAttributes.put("country", "uk");
        userAttributes.put("post_code", "94110");

        filter = audience.filter(userAttributes, allocationWithAudienceQuery);
        Assert.assertTrue(filter);

        // if participant has a key value matching the audience query they should not be filtered
        userAttributes = new HashMap<>();
        userAttributes.put("country", "us");
        userAttributes.put("post_code", "94110");

        filter = audience.filter(userAttributes, allocationWithAudienceQuery);
        Assert.assertFalse(filter);
    }

    @Test
    public void testFilterOneGroupKeyValueEqualOr() {
        String rawAudienceQuery = "{\"id\": \"g-2a3335a3-b470-4b88-bf3c-23d8a06eb27d\", \"rules\": [{\"operator\": \"kv_equal\", \"id\": \"r-c4cfdaca-b3f1-45ae-81a9-910ebb507e99\", \"value\": [\"country\", \"us\"], \"field\": \"user_attributes\", \"index\": 0}], \"combinator\": \"or\"}";
        JsonObject audienceQuery = new JsonParser().parse(rawAudienceQuery).getAsJsonObject();

        JsonObject allocationWithAudienceQuery = allocation.deepCopy();
        allocationWithAudienceQuery.add("audience_query", audienceQuery);

        Audience audience = new Audience();
        boolean filter;
        Map<String, String> userAttributes;

        // if participant does not have a user attribute matching the audience query they should be filtered
        userAttributes = new HashMap<>();
        userAttributes.put("post_code", "94110");

        filter = audience.filter(userAttributes, allocationWithAudienceQuery);
        Assert.assertTrue(filter);

        // if participant does not have a key value matching the audience query they should be filtered
        userAttributes = new HashMap<>();
        userAttributes.put("country", "uk");
        userAttributes.put("post_code", "94110");

        filter = audience.filter(userAttributes, allocationWithAudienceQuery);
        Assert.assertTrue(filter);

        // if participant has a key value matching the audience query they should not be filtered
        userAttributes = new HashMap<>();
        userAttributes.put("country", "us");
        userAttributes.put("post_code", "94110");

        filter = audience.filter(userAttributes, allocationWithAudienceQuery);
        Assert.assertFalse(filter);
    }

    @Test
    public void testFilterOneGroupKeyValueNotEqualAnd() {
        String rawAudienceQuery = "{\"combinator\": \"and\", \"rules\": [{\"value\": [\"country\", \"us\"], \"operator\": \"kv_not_equal\", \"field\": \"user_attributes\", \"index\": 0, \"id\": \"r-c4cfdaca-b3f1-45ae-81a9-910ebb507e99\"}], \"id\": \"g-2a3335a3-b470-4b88-bf3c-23d8a06eb27d\"}";
        JsonObject audienceQuery = new JsonParser().parse(rawAudienceQuery).getAsJsonObject();

        JsonObject allocationWithAudienceQuery = allocation.deepCopy();
        allocationWithAudienceQuery.add("audience_query", audienceQuery);

        Audience audience = new Audience();
        boolean filter;
        Map<String, String> userAttributes;

        // if participant does not have a user attribute matching the audience query they should be filtered
        userAttributes = new HashMap<>();
        userAttributes.put("post_code", "94110");

        filter = audience.filter(userAttributes, allocationWithAudienceQuery);
        Assert.assertTrue(filter);

        // if participant does not have a key value matching the audience query they should not be filtered
        userAttributes = new HashMap<>();
        userAttributes.put("country", "uk");
        userAttributes.put("post_code", "94110");

        filter = audience.filter(userAttributes, allocationWithAudienceQuery);
        Assert.assertFalse(filter);

        // if participant has a key value matching the audience query they should be filtered
        userAttributes = new HashMap<>();
        userAttributes.put("country", "us");
        userAttributes.put("post_code", "94110");

        filter = audience.filter(userAttributes, allocationWithAudienceQuery);
        Assert.assertTrue(filter);
    }

    @Test
    public void testFilterOneGroupKeyValueNotEqualOr() {
        String rawAudienceQuery = "{\"id\": \"g-2a3335a3-b470-4b88-bf3c-23d8a06eb27d\", \"rules\": [{\"operator\": \"kv_not_equal\", \"id\": \"r-c4cfdaca-b3f1-45ae-81a9-910ebb507e99\", \"value\": [\"country\", \"us\"], \"field\": \"user_attributes\", \"index\": 0}], \"combinator\": \"or\"}";
        JsonObject audienceQuery = new JsonParser().parse(rawAudienceQuery).getAsJsonObject();

        JsonObject allocationWithAudienceQuery = allocation.deepCopy();
        allocationWithAudienceQuery.add("audience_query", audienceQuery);

        Audience audience = new Audience();
        boolean filter;
        Map<String, String> userAttributes;

        // if participant does not have a user attribute matching the audience query they should be filtered
        userAttributes = new HashMap<>();
        userAttributes.put("post_code", "94110");

        filter = audience.filter(userAttributes, allocationWithAudienceQuery);
        Assert.assertTrue(filter);

        // if participant does not have a key value matching the audience query they should not be filtered
        userAttributes = new HashMap<>();
        userAttributes.put("country", "uk");
        userAttributes.put("post_code", "94110");

        filter = audience.filter(userAttributes, allocationWithAudienceQuery);
        Assert.assertFalse(filter);

        // if participant has a key value matching the audience query they should be filtered
        userAttributes = new HashMap<>();
        userAttributes.put("country", "us");
        userAttributes.put("post_code", "94110");

        filter = audience.filter(userAttributes, allocationWithAudienceQuery);
        Assert.assertTrue(filter);
    }

    @Test
    public void testFilterOneGroupKeyValueContainsAnd() {
        String rawAudienceQuery = "{\"combinator\": \"and\", \"rules\": [{\"value\": [\"post_code\", \"941\"], \"operator\": \"kv_contains\", \"field\": \"user_attributes\", \"index\": 0, \"id\": \"r-c4cfdaca-b3f1-45ae-81a9-910ebb507e99\"}], \"id\": \"g-2a3335a3-b470-4b88-bf3c-23d8a06eb27d\"}";
        JsonObject audienceQuery = new JsonParser().parse(rawAudienceQuery).getAsJsonObject();

        JsonObject allocationWithAudienceQuery = allocation.deepCopy();
        allocationWithAudienceQuery.add("audience_query", audienceQuery);

        Audience audience = new Audience();
        boolean filter;
        Map<String, String> userAttributes;

        // if participant does not have a user attribute matching the audience query they should be filtered
        userAttributes = new HashMap<>();
        userAttributes.put("country", "us");

        filter = audience.filter(userAttributes, allocationWithAudienceQuery);
        Assert.assertTrue(filter);

        // if participant does not have a key value matching the audience query they should be filtered
        userAttributes = new HashMap<>();
        userAttributes.put("country", "us");
        userAttributes.put("post_code", "80011");

        filter = audience.filter(userAttributes, allocationWithAudienceQuery);
        Assert.assertTrue(filter);

        // if participant has a key value matching the audience query they should not be filtered
        userAttributes = new HashMap<>();
        userAttributes.put("country", "us");
        userAttributes.put("post_code", "94110");

        filter = audience.filter(userAttributes, allocationWithAudienceQuery);
        Assert.assertFalse(filter);
    }

    @Test
    public void testFilterOneGroupKeyValueContainsOr() {
        String rawAudienceQuery = "{\"id\": \"g-2a3335a3-b470-4b88-bf3c-23d8a06eb27d\", \"rules\": [{\"operator\": \"kv_contains\", \"id\": \"r-c4cfdaca-b3f1-45ae-81a9-910ebb507e99\", \"value\": [\"post_code\", \"941\"], \"field\": \"user_attributes\", \"index\": 0}], \"combinator\": \"or\"}";
        JsonObject audienceQuery = new JsonParser().parse(rawAudienceQuery).getAsJsonObject();

        JsonObject allocationWithAudienceQuery = allocation.deepCopy();
        allocationWithAudienceQuery.add("audience_query", audienceQuery);

        Audience audience = new Audience();
        boolean filter;
        Map<String, String> userAttributes;

        // if participant does not have a user attribute matching the audience query they should be filtered
        userAttributes = new HashMap<>();
        userAttributes.put("country", "us");

        filter = audience.filter(userAttributes, allocationWithAudienceQuery);
        Assert.assertTrue(filter);

        // if participant does not have a key value matching the audience query they should be filtered
        userAttributes = new HashMap<>();
        userAttributes.put("country", "us");
        userAttributes.put("post_code", "80011");

        filter = audience.filter(userAttributes, allocationWithAudienceQuery);
        Assert.assertTrue(filter);

        // if participant has a key value matching the audience query they should not be filtered
        userAttributes = new HashMap<>();
        userAttributes.put("country", "us");
        userAttributes.put("post_code", "94110");

        filter = audience.filter(userAttributes, allocationWithAudienceQuery);
        Assert.assertFalse(filter);
    }

    @Test
    public void testFilterOneGroupKeyValueNotContainsAnd() {
        String rawAudienceQuery = "{\"combinator\": \"and\", \"rules\": [{\"value\": [\"post_code\", \"941\"], \"operator\": \"kv_not_contains\", \"field\": \"user_attributes\", \"index\": 0, \"id\": \"r-c4cfdaca-b3f1-45ae-81a9-910ebb507e99\"}], \"id\": \"g-2a3335a3-b470-4b88-bf3c-23d8a06eb27d\"}";
        JsonObject audienceQuery = new JsonParser().parse(rawAudienceQuery).getAsJsonObject();

        JsonObject allocationWithAudienceQuery = allocation.deepCopy();
        allocationWithAudienceQuery.add("audience_query", audienceQuery);

        Audience audience = new Audience();
        boolean filter;
        Map<String, String> userAttributes;

        // if participant does not have a user attribute matching the audience query they should be filtered
        userAttributes = new HashMap<>();
        userAttributes.put("country", "us");

        filter = audience.filter(userAttributes, allocationWithAudienceQuery);
        Assert.assertTrue(filter);

        // if participant does not have a key value matching the audience query they should not  be filtered
        userAttributes = new HashMap<>();
        userAttributes.put("country", "us");
        userAttributes.put("post_code", "80011");

        filter = audience.filter(userAttributes, allocationWithAudienceQuery);
        Assert.assertFalse(filter);

        // if participant has a key value matching the audience query they should be filtered
        userAttributes = new HashMap<>();
        userAttributes.put("country", "us");
        userAttributes.put("post_code", "94110");

        filter = audience.filter(userAttributes, allocationWithAudienceQuery);
        Assert.assertTrue(filter);
    }

    @Test
    public void testFilterOneGroupKeyValueNotContainsOr() {
        String rawAudienceQuery = "{\"id\": \"g-2a3335a3-b470-4b88-bf3c-23d8a06eb27d\", \"rules\": [{\"operator\": \"kv_not_contains\", \"id\": \"r-c4cfdaca-b3f1-45ae-81a9-910ebb507e99\", \"value\": [\"post_code\", \"941\"], \"field\": \"user_attributes\", \"index\": 0}], \"combinator\": \"or\"}";
        JsonObject audienceQuery = new JsonParser().parse(rawAudienceQuery).getAsJsonObject();

        JsonObject allocationWithAudienceQuery = allocation.deepCopy();
        allocationWithAudienceQuery.add("audience_query", audienceQuery);

        Audience audience = new Audience();
        boolean filter;
        Map<String, String> userAttributes;

        // if participant does not have a user attribute matching the audience query they should be filtered
        userAttributes = new HashMap<>();
        userAttributes.put("country", "us");

        filter = audience.filter(userAttributes, allocationWithAudienceQuery);
        Assert.assertTrue(filter);

        // if participant does not have a key value matching the audience query they should not be filtered
        userAttributes = new HashMap<>();
        userAttributes.put("country", "us");
        userAttributes.put("post_code", "80011");

        filter = audience.filter(userAttributes, allocationWithAudienceQuery);
        Assert.assertFalse(filter);

        // if participant has a key value matching the audience query they should be filtered
        userAttributes = new HashMap<>();
        userAttributes.put("country", "us");
        userAttributes.put("post_code", "94110");

        filter = audience.filter(userAttributes, allocationWithAudienceQuery);
        Assert.assertTrue(filter);
    }

    @Test
    public void testFilterOneGroupKeyExistsAnd() {
        String rawAudienceQuery = "{\"id\": \"g-2a3335a3-b470-4b88-bf3c-23d8a06eb27d\", \"rules\": [{\"operator\": \"exists\", \"id\": \"r-c4cfdaca-b3f1-45ae-81a9-910ebb507e99\", \"value\": \"post_code\", \"field\": \"user_attributes\", \"index\": 0}], \"combinator\": \"and\"}";
        JsonObject audienceQuery = new JsonParser().parse(rawAudienceQuery).getAsJsonObject();

        JsonObject allocationWithAudienceQuery = allocation.deepCopy();
        allocationWithAudienceQuery.add("audience_query", audienceQuery);

        Audience audience = new Audience();
        boolean filter;
        Map<String, String> userAttributes;

        // if participant does not have a user attribute matching the audience query they should be filtered
        userAttributes = new HashMap<>();
        userAttributes.put("country", "us");

        filter = audience.filter(userAttributes, allocationWithAudienceQuery);
        Assert.assertTrue(filter);

        // if participant has a key matching the audience query they should not be filtered
        userAttributes = new HashMap<>();
        userAttributes.put("country", "us");
        userAttributes.put("post_code", "80011");

        filter = audience.filter(userAttributes, allocationWithAudienceQuery);
        Assert.assertFalse(filter);
    }

    @Test
    public void testFilterOneGroupKeyExistsOr() {
        String rawAudienceQuery = "{\"id\": \"g-2a3335a3-b470-4b88-bf3c-23d8a06eb27d\", \"rules\": [{\"operator\": \"exists\", \"id\": \"r-c4cfdaca-b3f1-45ae-81a9-910ebb507e99\", \"value\": \"post_code\", \"field\": \"user_attributes\", \"index\": 0}], \"combinator\": \"or\"}";
        JsonObject audienceQuery = new JsonParser().parse(rawAudienceQuery).getAsJsonObject();

        JsonObject allocationWithAudienceQuery = allocation.deepCopy();
        allocationWithAudienceQuery.add("audience_query", audienceQuery);

        Audience audience = new Audience();
        boolean filter;
        Map<String, String> userAttributes;

        // if participant does not have a user attribute matching the audience query they should be filtered
        userAttributes = new HashMap<>();
        userAttributes.put("country", "us");

        filter = audience.filter(userAttributes, allocationWithAudienceQuery);
        Assert.assertTrue(filter);

        // if participant has a key matching the audience query they should not be filtered
        userAttributes = new HashMap<>();
        userAttributes.put("country", "us");
        userAttributes.put("post_code", "80011");

        filter = audience.filter(userAttributes, allocationWithAudienceQuery);
        Assert.assertFalse(filter);
    }

    @Test
    public void testFilterOneGroupKeyValueEqualsAndKeyValueContains() {
        String rawAudienceQuery = "{\"id\": \"g-2a3335a3-b470-4b88-bf3c-23d8a06eb27d\", \"rules\": [{\"operator\": \"kv_equal\", \"id\": \"r-c4cfdaca-b3f1-45ae-81a9-910ebb507e99\", \"value\": [\"country\", \"us\"], \"field\": \"user_attributes\", \"index\": 0}, {\"operator\": \"kv_contains\", \"id\": \"r-05010b14-eaf5-4407-8d69-c56caf4082cf\", \"field\": \"user_attributes\", \"value\": [\"post_code\", \"941\"], \"index\": 1}], \"combinator\": \"and\"}";
        JsonObject audienceQuery = new JsonParser().parse(rawAudienceQuery).getAsJsonObject();

        JsonObject allocationWithAudienceQuery = allocation.deepCopy();
        allocationWithAudienceQuery.add("audience_query", audienceQuery);

        Audience audience = new Audience();
        boolean filter;
        Map<String, String> userAttributes;

        // if participant has only one user attribute matching the audience query they should be filtered
        userAttributes = new HashMap<>();
        userAttributes.put("country", "us");

        filter = audience.filter(userAttributes, allocationWithAudienceQuery);
        Assert.assertTrue(filter);

        // if participant has one key that does not match the audience query they should be filtered
        userAttributes = new HashMap<>();
        userAttributes.put("country", "us");
        userAttributes.put("post_code", "80110");

        filter = audience.filter(userAttributes, allocationWithAudienceQuery);
        Assert.assertTrue(filter);

        // if participant has a key matching the audience query they should not be filtered
        userAttributes = new HashMap<>();
        userAttributes.put("country", "us");
        userAttributes.put("post_code", "94110");

        filter = audience.filter(userAttributes, allocationWithAudienceQuery);
        Assert.assertFalse(filter);
    }

    @Test
    public void testFilterOneGroupKeyValueEqualsOrKeyValueContains() {
        String rawAudienceQuery = "{\"id\": \"g-2a3335a3-b470-4b88-bf3c-23d8a06eb27d\", \"rules\": [{\"operator\": \"kv_equal\", \"id\": \"r-c4cfdaca-b3f1-45ae-81a9-910ebb507e99\", \"value\": [\"country\", \"us\"], \"field\": \"user_attributes\", \"index\": 0}, {\"operator\": \"kv_contains\", \"id\": \"r-05010b14-eaf5-4407-8d69-c56caf4082cf\", \"field\": \"user_attributes\", \"value\": [\"post_code\", \"941\"], \"index\": 1}], \"combinator\": \"or\"}";
        JsonObject audienceQuery = new JsonParser().parse(rawAudienceQuery).getAsJsonObject();

        JsonObject allocationWithAudienceQuery = allocation.deepCopy();
        allocationWithAudienceQuery.add("audience_query", audienceQuery);

        Audience audience = new Audience();
        boolean filter;
        Map<String, String> userAttributes;

        // if participant has only one user attribute matching the audience query they should not be filtered
        userAttributes = new HashMap<>();
        userAttributes.put("country", "us");

        filter = audience.filter(userAttributes, allocationWithAudienceQuery);
        Assert.assertFalse(filter);

        // if participant has one key that does not match the audience query they should be filtered
        userAttributes = new HashMap<>();
        userAttributes.put("country", "us");
        userAttributes.put("post_code", "80110");

        filter = audience.filter(userAttributes, allocationWithAudienceQuery);
        Assert.assertFalse(filter);

        // if participant has a key matching the audience query they should not be filtered
        userAttributes = new HashMap<>();
        userAttributes.put("country", "us");
        userAttributes.put("post_code", "94110");

        filter = audience.filter(userAttributes, allocationWithAudienceQuery);
        Assert.assertFalse(filter);
    }

    @Test
    public void testFilterTwoGroupsKeyValueEqualsAndKeyValueContainsAndKeyValueEquals() {
        String rawAudienceQuery = "{\"combinator\": \"and\", \"rules\": [{\"value\": [\"country\", \"us\"], \"operator\": \"kv_equal\", \"field\": \"user_attributes\", \"index\": 0, \"id\": \"r-c4cfdaca-b3f1-45ae-81a9-910ebb507e99\"}, {\"operator\": \"kv_contains\", \"field\": \"user_attributes\", \"value\": [\"post_code\", \"941\"], \"index\": 1, \"id\": \"r-05010b14-eaf5-4407-8d69-c56caf4082cf\"}, {\"combinator\": \"or\", \"rules\": [{\"field\": \"user_attributes\", \"value\": [\"target\", \"true\"], \"index\": 2, \"id\": \"r-9ddb730a-2641-4084-ad78-eb726a6d07de\", \"operator\": \"kv_equal\"}], \"id\": \"g-27323a6e-54ac-4cb7-b4a7-ffae5f5a6ba6\"}], \"id\": \"g-2a3335a3-b470-4b88-bf3c-23d8a06eb27d\"}";
        JsonObject audienceQuery = new JsonParser().parse(rawAudienceQuery).getAsJsonObject();

        JsonObject allocationWithAudienceQuery = allocation.deepCopy();
        allocationWithAudienceQuery.add("audience_query", audienceQuery);

        Audience audience = new Audience();
        boolean filter;
        Map<String, String> userAttributes;

        // if participant has one group attribute matching the audience query they should be filtered
        userAttributes = new HashMap<>();
        userAttributes.put("country", "us");
        userAttributes.put("post_code", "94110");

        filter = audience.filter(userAttributes, allocationWithAudienceQuery);
        Assert.assertTrue(filter);

        // if participant has one group attribute matching the audience query they should be filtered
        userAttributes = new HashMap<>();
        userAttributes.put("target", "true");

        filter = audience.filter(userAttributes, allocationWithAudienceQuery);
        Assert.assertTrue(filter);

        // if participant has a key matching the audience query they should not be filtered
        userAttributes = new HashMap<>();
        userAttributes.put("country", "us");
        userAttributes.put("post_code", "94110");
        userAttributes.put("target", "true");

        filter = audience.filter(userAttributes, allocationWithAudienceQuery);
        Assert.assertFalse(filter);
    }

    @Test
    public void testFilterTwoGroupsKeyValueEqualsAndKeyValueContainsOrKeyValueEquals() {
        String rawAudienceQuery = "{\"combinator\": \"or\", \"rules\": [{\"value\": [\"country\", \"us\"], \"operator\": \"kv_equal\", \"field\": \"user_attributes\", \"index\": 0, \"id\": \"r-c4cfdaca-b3f1-45ae-81a9-910ebb507e99\"}, {\"operator\": \"kv_contains\", \"field\": \"user_attributes\", \"value\": [\"post_code\", \"941\"], \"index\": 1, \"id\": \"r-05010b14-eaf5-4407-8d69-c56caf4082cf\"}, {\"combinator\": \"or\", \"rules\": [{\"field\": \"user_attributes\", \"value\": [\"target\", \"true\"], \"index\": 2, \"id\": \"r-9ddb730a-2641-4084-ad78-eb726a6d07de\", \"operator\": \"kv_equal\"}], \"id\": \"g-27323a6e-54ac-4cb7-b4a7-ffae5f5a6ba6\"}], \"id\": \"g-2a3335a3-b470-4b88-bf3c-23d8a06eb27d\"}";
        JsonObject audienceQuery = new JsonParser().parse(rawAudienceQuery).getAsJsonObject();

        JsonObject allocationWithAudienceQuery = allocation.deepCopy();
        allocationWithAudienceQuery.add("audience_query", audienceQuery);

        Audience audience = new Audience();
        boolean filter;
        Map<String, String> userAttributes;

        // if participant has one group attribute matching the audience query they should be filtered
        userAttributes = new HashMap<>();
        userAttributes.put("country", "us");
        userAttributes.put("post_code", "94110");

        filter = audience.filter(userAttributes, allocationWithAudienceQuery);
        Assert.assertFalse(filter);

        // if participant has one group attribute matching the audience query they should be filtered
        userAttributes = new HashMap<>();
        userAttributes.put("target", "true");

        filter = audience.filter(userAttributes, allocationWithAudienceQuery);
        Assert.assertFalse(filter);

        // if participant has a key matching the audience query they should not be filtered
        userAttributes = new HashMap<>();
        userAttributes.put("country", "us");
        userAttributes.put("post_code", "94110");
        userAttributes.put("target", "true");

        filter = audience.filter(userAttributes, allocationWithAudienceQuery);
        Assert.assertFalse(filter);
    }

}
