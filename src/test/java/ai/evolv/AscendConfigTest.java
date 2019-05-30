package ai.evolv;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class AscendConfigTest {

    private static final String ENVIRONMENT_ID = "test_12345";

    @Mock
    private HttpClient mockHttpClient;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void tearDown() {
        if (mockHttpClient != null) {
            mockHttpClient = null;
        }
    }


    @Test
    public void testBuildDefaultConfig() {
        AscendConfig config = AscendConfig.builder(ENVIRONMENT_ID, mockHttpClient).build();

        Assert.assertEquals(ENVIRONMENT_ID, config.getEnvironmentId());
        Assert.assertEquals(AscendConfig.DEFAULT_HTTP_SCHEME, config.getHttpScheme());
        Assert.assertEquals(AscendConfig.DEFAULT_DOMAIN, config.getDomain());
        Assert.assertEquals(AscendConfig.DEFAULT_API_VERSION, config.getVersion());
        Assert.assertEquals(AscendConfig.DEFAULT_HTTP_SCHEME, config.getHttpScheme());
        Assert.assertEquals(mockHttpClient, config.getHttpClient());
        Assert.assertNotNull(config.getExecutionQueue());
    }

    @Test
    public void testBuildConfig() {
        long timeout = 1;
        String domain = "test.evolv.ai";
        String version = "test";
        AscendAllocationStore allocationStore = new DefaultAllocationStore(10);
        AscendParticipant participant = AscendParticipant.builder().build();
        String httpScheme = "test";

        AscendConfig config = AscendConfig.builder(ENVIRONMENT_ID, mockHttpClient)
                .setDomain(domain)
                .setVersion(version)
                .setAscendAllocationStore(allocationStore)
                .setAscendParticipant(participant)
                .setHttpScheme(httpScheme)
                .build();

        Assert.assertEquals(ENVIRONMENT_ID, config.getEnvironmentId());
        Assert.assertEquals(domain, config.getDomain());
        Assert.assertEquals(version, config.getVersion());
        Assert.assertEquals(allocationStore, config.getAscendAllocationStore());
        Assert.assertEquals(participant, config.getAscendParticipant());
        Assert.assertEquals(httpScheme, config.getHttpScheme());
    }

}
