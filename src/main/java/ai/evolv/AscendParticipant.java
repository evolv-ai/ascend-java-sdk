package ai.evolv;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AscendParticipant {

    private final String sessionId;
    private String userId;
    private Map<String, String> userAttributes;

    private AscendParticipant(String userId, String sessionId, Map<String, String> userAttributes) {
        this.userId = userId;
        this.sessionId = sessionId;
        this.userAttributes = userAttributes;
    }

    public static Builder builder() {
        return new Builder();
    }

    String getUserId() {
        return userId;
    }

    String getSessionId() {
        return sessionId;
    }

    Map<String, String> getUserAttributes() {
        return userAttributes;
    }

    void setUserId(String userId) {
        this.userId = userId;
    }

    public static class Builder {

        private String userId = UUID.randomUUID().toString();
        private String sessionId = UUID.randomUUID().toString();
        private Map<String, String> userAttributes = new HashMap<>();

        /**
         * A unique key representing the participant.
         * @param userId a unique key
         * @return this instance of the participant
         */
        public Builder setUserId(String userId) {
            this.userId = userId;
            return this;
        }

        /**
         * A unique key representing the participant's session.
         * @param sessionId a unique key
         * @return this instance of the participant
         */
        public Builder setSessionId(String sessionId) {
            this.sessionId = sessionId;
            return this;
        }

        /**
         * Sets the users attributes which can be used to filter users upon.
         * @param userAttributes a map representing specific attributes that
         *                      describe the participant
         * @return this instance of the participant
         */
        public Builder setUserAttributes(Map<String, String> userAttributes) {
            this.userAttributes = userAttributes;
            return this;
        }

        /**
         * Builds the AscendParticipant instance.
         * @return an AscendParticipant instance.
         */
        public AscendParticipant build() {
            userAttributes.put("uid", userId);
            userAttributes.put("sid", sessionId);
            return new AscendParticipant(userId, sessionId, userAttributes);
        }

    }

}
