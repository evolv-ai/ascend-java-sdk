package ai.evolv;

import com.google.gson.JsonArray;

import com.google.gson.JsonParser;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.*;

public class AscendClientImplTest {

    private static final String environmentId = "test_12345";
    private static final String rawAllocation = "[{\"uid\":\"test_uid\",\"sid\":\"test_sid\",\"eid\":\"test_eid\",\"cid\":\"test_cid\",\"genome\":{\"search\":{\"weighting\":{\"distance\":2.5,\"dealer_score\":2.5}},\"pages\":{\"all_pages\":{\"header_footer\":[\"blue\",\"white\"]},\"testing_page\":{\"megatron\":\"none\",\"header\":\"white\"}},\"algorithms\":{\"feature_importance\":false}},\"excluded\":false}]";

    private static Double testValue = 0.0;

    @Mock
    private AscendConfig mockConfig;

    @Mock
    private ExecutionQueue mockExecutionQueue;

    @Mock
    private HttpClient mockHttpClient;

    @Mock
    private AscendAllocationStore mockAllocationStore;

    @Mock
    private EventEmitter mockEventEmitter;

    @Mock
    private Allocator mockAllocator;

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

        if (mockEventEmitter != null) {
            mockEventEmitter = null;
        }

        if (mockAllocator != null) {
            mockAllocator = null;
        }
    }

    @Test
    public void testGetReturnsDefaultsUponNullFuture() {
        AscendConfig actualConfig = AscendConfig.builder(environmentId, mockHttpClient).build();
        mockConfig = new AllocatorTest().setUpMockedAscendConfigWithMockedClient(mockConfig, actualConfig,
                mockExecutionQueue, mockHttpClient, mockAllocationStore);

        AscendParticipant participant = AscendParticipant.builder().build();

        AscendClient client = new AscendClientImpl(mockConfig, mockEventEmitter, null, mockAllocator,
                false, participant);
        Double expectedValue = .001;
        Double result = client.get("search.weighting.distance", expectedValue);
        Assert.assertEquals(expectedValue, result);
    }

    @Test
    public void testGetReturnsDefaultsUponEmptyAllocations() {
        AscendConfig actualConfig = AscendConfig.builder(environmentId, mockHttpClient).build();
        mockConfig = new AllocatorTest().setUpMockedAscendConfigWithMockedClient(mockConfig, actualConfig,
                mockExecutionQueue, mockHttpClient, mockAllocationStore);

        AscendParticipant participant = AscendParticipant.builder().build();

        CompletableFuture<JsonArray> allocationsFuture = new CompletableFuture<>();
        JsonArray allocations = new JsonArray();
        allocationsFuture.complete(allocations);

        AscendClient client = new AscendClientImpl(mockConfig, mockEventEmitter, allocationsFuture, mockAllocator,
                false, participant);
        Double expectedValue = .001;
        Double result = client.get("search.weighting.distance", expectedValue);
        Assert.assertEquals(expectedValue, result);

    }

    @Test
    public void testGetReturnsDefaultsUponException() {
        AscendConfig actualConfig = AscendConfig.builder(environmentId, mockHttpClient).build();
        mockConfig = new AllocatorTest().setUpMockedAscendConfigWithMockedClient(mockConfig, actualConfig,
                mockExecutionQueue, mockHttpClient, mockAllocationStore);

        AscendParticipant participant = AscendParticipant.builder().build();

        CompletableFuture<JsonArray> allocationsFuture = new CompletableFuture<>();
        JsonArray allocations = new JsonParser().parse(rawAllocation).getAsJsonArray();
        allocationsFuture.complete(allocations);

        AscendClient client = new AscendClientImpl(mockConfig, mockEventEmitter, allocationsFuture, mockAllocator,
                false, participant);
        Double expectedValue = .001;
        Double result = client.get("not.a.real.key", expectedValue);
        Assert.assertEquals(expectedValue, result);
    }

    @Test
    public void testGetValueSuccess() {
        AscendConfig actualConfig = AscendConfig.builder(environmentId, mockHttpClient).build();
        mockConfig = new AllocatorTest().setUpMockedAscendConfigWithMockedClient(mockConfig, actualConfig,
                mockExecutionQueue, mockHttpClient, mockAllocationStore);

        AscendParticipant participant = AscendParticipant.builder().build();

        CompletableFuture<JsonArray> allocationsFuture = new CompletableFuture<>();
        JsonArray allocations = new JsonParser().parse(rawAllocation).getAsJsonArray();
        allocationsFuture.complete(allocations);

        AscendClient client = new AscendClientImpl(mockConfig, mockEventEmitter, allocationsFuture, mockAllocator,
                false, participant);
        Double result = client.get("search.weighting.distance", .001);
        Double expected = 2.5;
        Assert.assertEquals(expected, result);
    }

    @Test
    public void testEmitEventWithScore() {
        AscendConfig actualConfig = AscendConfig.builder(environmentId, mockHttpClient).build();
        mockConfig = new AllocatorTest().setUpMockedAscendConfigWithMockedClient(mockConfig, actualConfig,
                mockExecutionQueue, mockHttpClient, mockAllocationStore);

        AscendParticipant participant = AscendParticipant.builder().build();

        CompletableFuture<JsonArray> allocationsFuture = new CompletableFuture<>();
        JsonArray allocations = new JsonParser().parse(rawAllocation).getAsJsonArray();
        allocationsFuture.complete(allocations);

        AscendClient client = new AscendClientImpl(mockConfig, mockEventEmitter, allocationsFuture, mockAllocator,
                false, participant);
        String key = "testKey";
        Double score = 1.3;
        client.emitEvent(key, score);

        verify(mockEventEmitter, times(1)).emit(key, score);
    }

    @Test
    public void testEmitEvent() {
        AscendConfig actualConfig = AscendConfig.builder(environmentId, mockHttpClient).build();
        mockConfig = new AllocatorTest().setUpMockedAscendConfigWithMockedClient(mockConfig, actualConfig,
                mockExecutionQueue, mockHttpClient, mockAllocationStore);

        AscendParticipant participant = AscendParticipant.builder().build();

        CompletableFuture<JsonArray> allocationsFuture = new CompletableFuture<>();
        JsonArray allocations = new JsonParser().parse(rawAllocation).getAsJsonArray();
        allocationsFuture.complete(allocations);

        AscendClient client = new AscendClientImpl(mockConfig, mockEventEmitter, allocationsFuture, mockAllocator,
                false, participant);
        String key = "testKey";
        client.emitEvent(key);

        verify(mockEventEmitter, times(1)).emit(key);
    }

    @Test
    public void testConfirmEventSandBagged() {
        AscendConfig actualConfig = AscendConfig.builder(environmentId, mockHttpClient).build();
        mockConfig = new AllocatorTest().setUpMockedAscendConfigWithMockedClient(mockConfig, actualConfig,
                mockExecutionQueue, mockHttpClient, mockAllocationStore);
        when(mockAllocator.getAllocationStatus()).thenReturn(Allocator.AllocationStatus.FETCHING);

        AscendParticipant participant = AscendParticipant.builder().build();

        CompletableFuture<JsonArray> allocationsFuture = new CompletableFuture<>();
        JsonArray allocations = new JsonParser().parse(rawAllocation).getAsJsonArray();
        allocationsFuture.complete(allocations);

        AscendClient client = new AscendClientImpl(mockConfig, mockEventEmitter, allocationsFuture, mockAllocator,
                false, participant);
        client.confirm();

        verify(mockAllocator, times(1)).sandBagConfirmation();
    }

    @Test
    public void testConfirmEvent() {
        AscendParticipant participant = AscendParticipant.builder().build();
        AscendConfig actualConfig = AscendConfig.builder(environmentId, mockHttpClient).build();
        mockConfig = new AllocatorTest().setUpMockedAscendConfigWithMockedClient(mockConfig, actualConfig,
                mockExecutionQueue, mockHttpClient, mockAllocationStore);
        JsonArray allocations = new JsonParser().parse(rawAllocation).getAsJsonArray();
        when(mockAllocator.getAllocationStatus()).thenReturn(Allocator.AllocationStatus.RETRIEVED);
        when(mockAllocationStore.get(participant.getUserId())).thenReturn(allocations);

        CompletableFuture<JsonArray> allocationsFuture = new CompletableFuture<>();
        allocationsFuture.complete(allocations);

        AscendClient client = new AscendClientImpl(mockConfig, mockEventEmitter, allocationsFuture, mockAllocator,
                false, participant);
        client.confirm();

        verify(mockEventEmitter, times(1)).confirm(allocations);
    }

    @Test
    public void testContaminateEventSandBagged() {
        AscendConfig actualConfig = AscendConfig.builder(environmentId, mockHttpClient).build();
        mockConfig = new AllocatorTest().setUpMockedAscendConfigWithMockedClient(mockConfig, actualConfig,
                mockExecutionQueue, mockHttpClient, mockAllocationStore);
        when(mockAllocator.getAllocationStatus()).thenReturn(Allocator.AllocationStatus.FETCHING);

        AscendParticipant participant = AscendParticipant.builder().build();

        CompletableFuture<JsonArray> allocationsFuture = new CompletableFuture<>();
        JsonArray allocations = new JsonParser().parse(rawAllocation).getAsJsonArray();
        allocationsFuture.complete(allocations);

        AscendClient client = new AscendClientImpl(mockConfig, mockEventEmitter, allocationsFuture, mockAllocator,
                false, participant);
        client.contaminate();

        verify(mockAllocator, times(1)).sandBagContamination();
    }

    @Test
    public void testContaminateEvent() {
        AscendConfig actualConfig = AscendConfig.builder(environmentId, mockHttpClient).build();
        mockConfig = new AllocatorTest().setUpMockedAscendConfigWithMockedClient(mockConfig, actualConfig,
                mockExecutionQueue, mockHttpClient, mockAllocationStore);

        AscendParticipant participant = AscendParticipant.builder().build();

        JsonArray allocations = new JsonParser().parse(rawAllocation).getAsJsonArray();
        when(mockAllocator.getAllocationStatus()).thenReturn(Allocator.AllocationStatus.RETRIEVED);
        when(mockAllocationStore.get(participant.getUserId())).thenReturn(allocations);

        CompletableFuture<JsonArray> allocationsFuture = new CompletableFuture<>();
        allocationsFuture.complete(allocations);

        AscendClient client = new AscendClientImpl(mockConfig, mockEventEmitter, allocationsFuture, mockAllocator,
                false, participant);
        client.contaminate();

        verify(mockEventEmitter, times(1)).contaminate(allocations);
    }

    @Test
    public void testSubscribeNoPreviousAllocationsWithFetchingState() {
        AscendConfig actualConfig = AscendConfig.builder(environmentId, mockHttpClient).build();
        mockConfig = new AllocatorTest().setUpMockedAscendConfigWithMockedClient(mockConfig, actualConfig,
                mockExecutionQueue, mockHttpClient, mockAllocationStore);

        AscendParticipant participant = AscendParticipant.builder().build();

        JsonArray allocations = new JsonParser().parse(rawAllocation).getAsJsonArray();
        when(mockAllocator.getAllocationStatus()).thenReturn(Allocator.AllocationStatus.FETCHING);
        when(mockAllocationStore.get(participant.getUserId())).thenReturn(allocations);

        CompletableFuture<JsonArray> allocationsFuture = new CompletableFuture<>();
        allocationsFuture.complete(allocations);

        AscendClient client = new AscendClientImpl(mockConfig, mockEventEmitter, allocationsFuture, mockAllocator,
                false, participant);

        Double expectedTestValue = 0.0;
        Assert.assertEquals(expectedTestValue, testValue);

        client.subscribe("search.weighting.distance", .01, value -> {
            testValue = value;
        });


        verify(mockExecutionQueue, times(1)).enqueue(any());

        testValue = 0.0;
    }

    @Test
    public void testSubscribeNoPreviousAllocationsWithRetrievedState() {
        AscendConfig actualConfig = AscendConfig.builder(environmentId, mockHttpClient).build();
        mockConfig = new AllocatorTest().setUpMockedAscendConfigWithMockedClient(mockConfig, actualConfig,
                mockExecutionQueue, mockHttpClient, mockAllocationStore);

        AscendParticipant participant = AscendParticipant.builder().build();

        JsonArray allocations = new JsonParser().parse(rawAllocation).getAsJsonArray();
        when(mockAllocator.getAllocationStatus()).thenReturn(Allocator.AllocationStatus.RETRIEVED);
        when(mockAllocationStore.get(participant.getUserId())).thenReturn(allocations);

        CompletableFuture<JsonArray> allocationsFuture = new CompletableFuture<>();
        allocationsFuture.complete(allocations);

        AscendClient client = new AscendClientImpl(mockConfig, mockEventEmitter, allocationsFuture, mockAllocator,
                false, participant);

        Double expectedTestValue = 0.0;
        Assert.assertEquals(expectedTestValue, testValue);

        client.subscribe("search.weighting.distance", .01, value -> {
            testValue = value;
        });

        verify(mockAllocationStore, times(1)).get(participant.getUserId());

        Double expected = 2.5;
        Assert.assertEquals(expected, testValue);

        testValue = 0.0;
    }

    @Test
    public void testSubscribeNoPreviousAllocationsWithFailedState() {
        AscendConfig actualConfig = AscendConfig.builder(environmentId, mockHttpClient).build();
        mockConfig = new AllocatorTest().setUpMockedAscendConfigWithMockedClient(mockConfig, actualConfig,
                mockExecutionQueue, mockHttpClient, mockAllocationStore);

        AscendParticipant participant = AscendParticipant.builder().build();

        JsonArray allocations = new JsonParser().parse(rawAllocation).getAsJsonArray();
        when(mockAllocator.getAllocationStatus()).thenReturn(Allocator.AllocationStatus.FAILED);
        when(mockAllocationStore.get(participant.getUserId())).thenReturn(allocations);

        CompletableFuture<JsonArray> allocationsFuture = new CompletableFuture<>();
        allocationsFuture.complete(allocations);

        AscendClient client = new AscendClientImpl(mockConfig, mockEventEmitter, allocationsFuture, mockAllocator,
                false, participant);

        Double expectedTestValue = 0.0;
        Assert.assertEquals(expectedTestValue, testValue);

        client.subscribe("search.weighting.distance", .01, value -> {
            testValue = value;
        });

        Double expected = .01;
        Assert.assertEquals(expected, testValue);

        testValue = 0.0;
    }

    @Test
    public void testSubscribeNoPreviousAllocationsWithRetrievedStateThrowsError() {
        AscendConfig actualConfig = AscendConfig.builder(environmentId, mockHttpClient).build();
        mockConfig = new AllocatorTest().setUpMockedAscendConfigWithMockedClient(mockConfig, actualConfig,
                mockExecutionQueue, mockHttpClient, mockAllocationStore);

        AscendParticipant participant = AscendParticipant.builder().build();

        JsonArray allocations = new JsonParser().parse(rawAllocation).getAsJsonArray();
        when(mockAllocator.getAllocationStatus()).thenReturn(Allocator.AllocationStatus.RETRIEVED);
        when(mockAllocationStore.get(participant.getUserId())).thenReturn(allocations);

        CompletableFuture<JsonArray> allocationsFuture = new CompletableFuture<>();
        allocationsFuture.complete(allocations);

        AscendClient client = new AscendClientImpl(mockConfig, mockEventEmitter, allocationsFuture, mockAllocator,
                false, participant);

        Double expectedTestValue = 0.0;
        Assert.assertEquals(expectedTestValue, testValue);

        client.subscribe("not.a.valid.key", .01, value -> {
            testValue = value;
        });

        Double expected = .01;
        Assert.assertEquals(expected, testValue);

        testValue = 0.0;
    }

}