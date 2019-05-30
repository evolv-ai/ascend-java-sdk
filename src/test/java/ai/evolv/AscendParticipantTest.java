package ai.evolv;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class AscendParticipantTest {

    @Test
    public void testBuildDefaultParticipant() {
        AscendParticipant participant = AscendParticipant.builder().build();
        Assert.assertNotNull(participant.getUserId());
        Assert.assertNotNull(participant.getSessionId());
        Assert.assertNotNull(participant.getUserAttributes());
    }

    @Test
    public void testSetCustomParticipantAttributes() {
        String userId = "Testy";
        String sessionId = "McTestTest";

        Map<String, String> userAttributes = new HashMap<>();
        userAttributes.put("country", "us");

        AscendParticipant participant = AscendParticipant.builder()
                .setUserId(userId)
                .setSessionId(sessionId)
                .setUserAttributes(userAttributes)
                .build();

        Assert.assertEquals(userId, participant.getUserId());
        Assert.assertEquals(sessionId, participant.getSessionId());

        Map<String, String> expectedUserAttributes = new HashMap<>();
        expectedUserAttributes.put("country", "us");
        expectedUserAttributes.put("uid", userId);
        expectedUserAttributes.put("sid", sessionId);

        Assert.assertEquals(expectedUserAttributes, participant.getUserAttributes());
    }

    @Test
    public void testSetUserIdAfterParticipantCreated() {
        String newUserId = "Testy";
        AscendParticipant participant = AscendParticipant.builder().build();
        String oldUserId = participant.getUserId();
        participant.setUserId(newUserId);
        Assert.assertNotEquals(oldUserId, participant.getUserId());
        Assert.assertEquals(newUserId, participant.getUserId());
    }

}
