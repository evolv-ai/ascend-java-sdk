package ai.evolv;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.CompletableFuture;

public class AllocatorTest {

    private static final String environmentId = "test_12345";
    private static final String rawAllocation = "[{\"uid\":\"test_uid\",\"sid\":\"test_sid\",\"eid\":\"test_eid\",\"cid\":\"test_cid\",\"genome\":{\"search\":{\"weighting\":{\"distance\":2.5,\"dealer_score\":2.5}},\"pages\":{\"all_pages\":{\"header_footer\":[\"blue\",\"white\"]},\"testing_page\":{\"megatron\":\"none\",\"header\":\"white\"}},\"algorithms\":{\"feature_importance\":false}},\"excluded\":false}]";

    @Mock
    private AscendConfig mockConfig;

    @Mock
    private ExecutionQueue mockExecutionQueue;

    @Mock
    private HttpClient mockHttpClient;

    @Mock
    private AscendAllocationStore mockAllocationStore;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void tearDown() {
        if (mockConfig != null) {
            mockConfig = null;
        }

        if (mockExecutionQueue != null) {
            mockExecutionQueue = null;
        }

        if (mockHttpClient != null) {
            mockHttpClient = null;
        }

        if (mockAllocationStore != null) {
            mockAllocationStore = null;
        }
    }

    AscendConfig setUpMockedAscendConfigWithMockedClient(AscendConfig mockedConfig, AscendConfig actualConfig,
                                                         ExecutionQueue mockExecutionQueue, HttpClient mockHttpClient,
                                                         AscendAllocationStore mockAllocationStore) {
        when(mockedConfig.getHttpClient()).thenReturn(mockHttpClient);
        when(mockedConfig.getAscendParticipant()).thenReturn(actualConfig.getAscendParticipant());
        when(mockedConfig.getHttpScheme()).thenReturn(actualConfig.getHttpScheme());
        when(mockedConfig.getDomain()).thenReturn(actualConfig.getDomain());
        when(mockedConfig.getVersion()).thenReturn(actualConfig.getVersion());
        when(mockedConfig.getEnvironmentId()).thenReturn(actualConfig.getEnvironmentId());
        when(mockedConfig.getAscendAllocationStore())
                .thenReturn(mockAllocationStore);

        when(mockedConfig.getExecutionQueue()).thenReturn(mockExecutionQueue);
        when(mockedConfig.getHttpClient()).thenReturn(mockHttpClient);

        return mockedConfig;
    }

    static String createAllocationsUrl(AscendConfig config, AscendParticipant participant) {
        return String.format("%s://%s/%s/%s/allocations?uid=%s&sid=%s",
                config.getHttpScheme(),
                config.getDomain(),
                config.getVersion(),
                config.getEnvironmentId(),
                participant.getUserId(),
                participant.getSessionId());
    }

    static String createConfirmationUrl(AscendConfig config, JsonObject allocation, AscendParticipant participant) {
        return String.format("%s://%s/%s/%s/events?uid=%s&sid=%s&eid=%s&cid=%s&type=%s",
                config.getHttpScheme(),
                config.getDomain(),
                config.getVersion(),
                config.getEnvironmentId(),
                participant.getUserId(),
                participant.getSessionId(),
                allocation.get("eid").getAsString(),
                allocation.get("cid").getAsString(),
                "confirmation");
    }

    static String createContaminationUrl(AscendConfig config, JsonObject allocation, AscendParticipant participant) {
        return String.format("%s://%s/%s/%s/events?uid=%s&sid=%s&eid=%s&cid=%s&type=%s",
                config.getHttpScheme(),
                config.getDomain(),
                config.getVersion(),
                config.getEnvironmentId(),
                participant.getUserId(),
                participant.getSessionId(),
                allocation.get("eid").getAsString(),
                allocation.get("cid").getAsString(),
                "contamination");
    }

    @Test
    public void testCreateAllocationsUrl() {
        AscendConfig actualConfig = AscendConfig.builder(environmentId, mockHttpClient).build();
        mockConfig = setUpMockedAscendConfigWithMockedClient(mockConfig, actualConfig, mockExecutionQueue,
                mockHttpClient, mockAllocationStore);

        AscendParticipant participant = AscendParticipant.builder().build();
        Allocator allocator = new Allocator(mockConfig, participant);
        String actualUrl = allocator.createAllocationsUrl();
        String expectedUrl = createAllocationsUrl(actualConfig, participant);
        Assert.assertEquals(expectedUrl, actualUrl);
    }

    @Test
    public void testAllocationsNotEmpty() {
        JsonArray nullAllocations = null;
        Assert.assertFalse(Allocator.allocationsNotEmpty(nullAllocations));

        JsonArray emptyAllocations = new JsonArray();
        Assert.assertFalse(Allocator.allocationsNotEmpty(emptyAllocations));

        JsonArray allocations = new JsonParser().parse(rawAllocation).getAsJsonArray();
        Assert.assertTrue(Allocator.allocationsNotEmpty(allocations));
    }

    @Test
    public void testResolveAllocationFailureWithAllocationsInStore() {
        AscendParticipant participant = AscendParticipant.builder().build();
        AscendConfig actualConfig = AscendConfig.builder(environmentId, mockHttpClient).build();
        JsonArray allocations = new JsonParser().parse(rawAllocation).getAsJsonArray();
        when(mockAllocationStore.get(participant.getUserId())).thenReturn(allocations);
        mockConfig = setUpMockedAscendConfigWithMockedClient(mockConfig, actualConfig, mockExecutionQueue,
                mockHttpClient, mockAllocationStore);

        Allocator allocator = new Allocator(mockConfig, participant);
        JsonArray actualAllocations = allocator.resolveAllocationFailure();

        verify(mockExecutionQueue, times(1)).executeAllWithValuesFromAllocations(allocations);
        Assert.assertEquals(Allocator.AllocationStatus.RETRIEVED, allocator.getAllocationStatus());
        Assert.assertEquals(allocations, actualAllocations);
    }

    @Test
    public void testResolveAllocationFailureWithAllocationsInStoreWithSandbaggedConfirmation() {
        AscendParticipant participant = AscendParticipant.builder().build();
        AscendConfig actualConfig = AscendConfig.builder(environmentId, mockHttpClient).build();
        JsonArray allocations = new JsonParser().parse(rawAllocation).getAsJsonArray();
        when(mockAllocationStore.get(participant.getUserId())).thenReturn(allocations);
        mockConfig = setUpMockedAscendConfigWithMockedClient(mockConfig, actualConfig, mockExecutionQueue,
                mockHttpClient, mockAllocationStore);

        Allocator allocator = new Allocator(mockConfig, participant);
        allocator.sandBagConfirmation();
        JsonArray actualAllocations = allocator.resolveAllocationFailure();

        verify(mockHttpClient, times(1))
                .get(createConfirmationUrl(actualConfig, allocations.get(0).getAsJsonObject(), participant));
        verify(mockExecutionQueue, times(1))
                .executeAllWithValuesFromAllocations(allocations);
        Assert.assertEquals(Allocator.AllocationStatus.RETRIEVED, allocator.getAllocationStatus());
        Assert.assertEquals(allocations, actualAllocations);
    }

    @Test
    public void testResolveAllocationFailureWithAllocationsInStoreWithSandbaggedContamination() {
        AscendParticipant participant = AscendParticipant.builder().build();
        AscendConfig actualConfig = AscendConfig.builder(environmentId, mockHttpClient).build();
        JsonArray allocations = new JsonParser().parse(rawAllocation).getAsJsonArray();
        when(mockAllocationStore.get(participant.getUserId())).thenReturn(allocations);
        mockConfig = setUpMockedAscendConfigWithMockedClient(mockConfig, actualConfig, mockExecutionQueue,
                mockHttpClient, mockAllocationStore);

        Allocator allocator = new Allocator(mockConfig, participant);
        allocator.sandBagContamination();
        JsonArray actualAllocations = allocator.resolveAllocationFailure();

        verify(mockHttpClient, times(1))
                .get(createContaminationUrl(actualConfig, allocations.get(0).getAsJsonObject(), participant));
        verify(mockExecutionQueue, times(1))
                .executeAllWithValuesFromAllocations(allocations);
        Assert.assertEquals(Allocator.AllocationStatus.RETRIEVED, allocator.getAllocationStatus());
        Assert.assertEquals(allocations, actualAllocations);
    }

    @Test
    public void testResolveAllocationFailureWithNoAllocationsInStore() {
        AscendParticipant participant = AscendParticipant.builder().build();
        AscendConfig actualConfig = AscendConfig.builder(environmentId, mockHttpClient).build();
        when(mockAllocationStore.get(participant.getUserId())).thenReturn(new JsonArray());
        mockConfig = setUpMockedAscendConfigWithMockedClient(mockConfig, actualConfig, mockExecutionQueue,
                mockHttpClient, mockAllocationStore);

        Allocator allocator = new Allocator(mockConfig, participant);
        allocator.sandBagContamination();
        JsonArray actualAllocations = allocator.resolveAllocationFailure();

        verify(mockExecutionQueue, times(1))
                .executeAllWithValuesFromDefaults();
        Assert.assertEquals(Allocator.AllocationStatus.FAILED, allocator.getAllocationStatus());
        Assert.assertEquals(new JsonArray(), actualAllocations);
    }

    @Test
    public void testFetchAllocationsWithNoAllocationsInStore() {
        CompletableFuture<String> allocationsResponseFuture = new CompletableFuture<>();
        allocationsResponseFuture.complete(rawAllocation);
        JsonArray allocations = new JsonParser().parse(rawAllocation).getAsJsonArray();
        AscendParticipant participant = AscendParticipant.builder().build();
        AscendConfig actualConfig = AscendConfig.builder(environmentId, mockHttpClient).build();
        when(mockHttpClient.get(createAllocationsUrl(actualConfig, participant))).thenReturn(allocationsResponseFuture);
        when(mockAllocationStore.get(participant.getUserId())).thenReturn(new JsonArray());
        mockConfig = setUpMockedAscendConfigWithMockedClient(mockConfig, actualConfig, mockExecutionQueue,
                mockHttpClient, mockAllocationStore);

        Allocator allocator = new Allocator(mockConfig, participant);
        CompletableFuture<JsonArray> allocationsFuture = allocator.fetchAllocations();

        verify(mockAllocationStore, times(1)).get(participant.getUserId());
        verify(mockAllocationStore, times(1)).put(participant.getUserId(), allocations);
        Assert.assertEquals(Allocator.AllocationStatus.RETRIEVED, allocator.getAllocationStatus());
        verify(mockExecutionQueue, times(1)).executeAllWithValuesFromAllocations(allocations);
    }

    @Test
    public void testFetchAllocationsWithAReconciliation() {
        CompletableFuture<String> allocationsResponseFuture = new CompletableFuture<>();
        allocationsResponseFuture.complete(rawAllocation);
        JsonArray allocations = new JsonParser().parse(rawAllocation).getAsJsonArray();
        AscendParticipant participant = AscendParticipant.builder().build();
        AscendConfig actualConfig = AscendConfig.builder(environmentId, mockHttpClient).build();
        when(mockHttpClient.get(createAllocationsUrl(actualConfig, participant))).thenReturn(allocationsResponseFuture);
        when(mockAllocationStore.get(participant.getUserId())).thenReturn(allocations);
        mockConfig = setUpMockedAscendConfigWithMockedClient(mockConfig, actualConfig, mockExecutionQueue,
                mockHttpClient, mockAllocationStore);

        Allocator allocator = new Allocator(mockConfig, participant);
        CompletableFuture<JsonArray> allocationsFuture = allocator.fetchAllocations();

        verify(mockAllocationStore, times(1)).get(participant.getUserId());
        verify(mockAllocationStore, times(1)).put(participant.getUserId(), allocations);
        Assert.assertEquals(Allocator.AllocationStatus.RETRIEVED, allocator.getAllocationStatus());
        verify(mockExecutionQueue, times(1)).executeAllWithValuesFromAllocations(allocations);
    }

}
