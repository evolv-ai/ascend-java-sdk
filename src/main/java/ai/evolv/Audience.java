package ai.evolv;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;


public class Audience {

    @FunctionalInterface
    interface Function<A, B> {
        boolean apply(A one, B two);
    }

    private Map<String, Function> operators = createOperatorsMap();

    private Map<String, Function> createOperatorsMap() {
        Map<String, Function> operatorsMap = new HashMap<>();
        operatorsMap.put("exists", (object, a) -> ((Map<String, String>) object)
                .containsKey(((JsonElement) a).getAsString()));
        operatorsMap.put("kv_contains", (object, params) -> {
            String storedValue = ((Map<String, String>) object).get(
                    ((JsonArray) params).get(0).getAsString());
            if (storedValue == null) {
                return false;
            }
            return storedValue.contains(((JsonArray) params).get(1).getAsString());
        });
        operatorsMap.put("kv_not_contains", (object, params) -> {
            String storedValue = ((Map<String, String>) object).get(
                    ((JsonArray) params).get(0).getAsString());
            if (storedValue == null) {
                return false;
            }
            return !storedValue.contains(((JsonArray) params).get(1).getAsString());
        });
        operatorsMap.put("kv_equal", (object, params) -> {
            String storedValue = ((Map<String, String>) object).get(
                    ((JsonArray) params).get(0).getAsString());
            if (storedValue == null) {
                return false;
            }
            return storedValue.equals(((JsonArray) params).get(1).getAsString());
        });
        operatorsMap.put("kv_not_equal", (object, params) -> {
            String storedValue = ((Map<String, String>) object).get(
                    ((JsonArray) params).get(0).getAsString());
            if (storedValue == null) {
                return false;
            }
            return !storedValue.equals(((JsonArray) params).get(1).getAsString());
        });

        return operatorsMap;
    }


    private boolean evaluateAudienceFilter(Map<String, String> userAttributes, JsonObject rule) {
        return operators.get(rule.get("operator").getAsString()).apply(userAttributes,
                rule.get("value"));
    }

    private boolean evaluateAudienceRule(Map<String, String> userAttributes,
                                         JsonObject audienceQuery, JsonObject rule) {
        if (rule.has("combinator")) {
            return evaluateAudienceQuery(userAttributes, rule);
        }
        return evaluateAudienceFilter(userAttributes, rule);
    }

    private boolean evaluateAudienceQuery(Map<String, String> userAttributes,
                                          JsonObject audienceQuery) {
        JsonElement rules = audienceQuery.get("rules");

        if (rules == null) {
            return true;
        }

        for (JsonElement r : rules.getAsJsonArray()) {
            boolean passed = evaluateAudienceRule(userAttributes, audienceQuery,
                    r.getAsJsonObject());

            String combinator = audienceQuery.get("combinator").getAsString();
            if (passed && combinator.equals("or")) {
                return true;
            }

            if (!passed && combinator.equals("and")) {
                return false;
            }
        }

        return audienceQuery.get("combinator").getAsString().equals("and");
    }

    /**
     * Determines whether on not to filter the user based upon the supplied user
     * attributes and allocation.
     * @param userAttributes map representing attributes that represent the participant
     * @param allocation allocation containing the participant's treatment(s)
     * @return true if participant should be filters, false if not
     */
    public boolean filter(Map<String, String> userAttributes, JsonObject allocation) {
        JsonElement excluded = allocation.get("excluded");
        if (excluded != null && excluded.getAsBoolean()) {
            return true;
        }

        JsonElement audienceQuery = allocation.get("audience_query");
        if (userAttributes == null || userAttributes.isEmpty()
                || audienceQuery == null || audienceQuery.isJsonNull()) {
            return false;
        }

        return !evaluateAudienceQuery(userAttributes, audienceQuery.getAsJsonObject());
    }


}
