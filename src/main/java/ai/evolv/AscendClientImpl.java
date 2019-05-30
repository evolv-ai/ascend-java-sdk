package ai.evolv;

import ai.evolv.exceptions.AscendKeyError;
import ai.evolv.generics.GenericClass;

import com.google.gson.JsonArray;

import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class AscendClientImpl implements AscendClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(AscendClientImpl.class);

    private final EventEmitter eventEmitter;
    private final CompletableFuture<JsonArray> futureAllocations;
    private final ExecutionQueue executionQueue;
    private final Allocator allocator;
    private final AscendAllocationStore store;
    private final boolean previousAllocations;
    private final AscendParticipant participant;

    AscendClientImpl(AscendConfig config,
                     EventEmitter emitter,
                     CompletableFuture<JsonArray> futureAllocations,
                     Allocator allocator,
                     boolean previousAllocations,
                     AscendParticipant participant) {
        this.store = config.getAscendAllocationStore();
        this.executionQueue = config.getExecutionQueue();
        this.eventEmitter = emitter;
        this.futureAllocations = futureAllocations;
        this.allocator = allocator;
        this.previousAllocations = previousAllocations;
        this.participant = participant;
    }

    @Override
    public <T> T get(String key, T defaultValue) {
        try {
            if (futureAllocations == null) {
                return defaultValue;
            }

            // this is blocking
            JsonArray allocations = futureAllocations.get();
            if (!Allocator.allocationsNotEmpty(allocations)) {
                return defaultValue;
            }

            GenericClass<T> cls = new GenericClass(defaultValue.getClass());
            T value = new Allocations(allocations).getValueFromAllocations(key, cls.getMyType(),
                    participant);

            if (value == null) {
                throw new AscendKeyError("Got null when retrieving key from allocations.");
            }

            return value;
        } catch (AscendKeyError e) {
            LOGGER.debug("Unable to retrieve the treatment. Returning " +
                    "the default.", e);
            return defaultValue;
        } catch (Exception e) {
            LOGGER.error("An error occurred while retrieving the treatment. Returning " +
                    "the default.", e);
            return defaultValue;
        }
    }

    @Override
    public <T> void subscribe(String key, T defaultValue, AscendAction<T> function) {
        Execution execution = new Execution<>(key, defaultValue, function, participant);
        if (previousAllocations) {
            try {
                JsonArray allocations = store.get(participant.getUserId());
                execution.executeWithAllocation(allocations);
            } catch (AscendKeyError e) {
                LOGGER.debug("Unable to retrieve the value of %s from the allocation.",
                        execution.getKey());
                execution.executeWithDefault();
            } catch (Exception e) {
                LOGGER.error("There was an error when applying the stored treatment.", e);
            }
        }

        Allocator.AllocationStatus allocationStatus = allocator.getAllocationStatus();
        if (allocationStatus == Allocator.AllocationStatus.FETCHING) {
            executionQueue.enqueue(execution);
            return;
        } else if (allocationStatus == Allocator.AllocationStatus.RETRIEVED) {
            try {
                JsonArray allocations = store.get(participant.getUserId());
                execution.executeWithAllocation(allocations);
                return;
            } catch (AscendKeyError e) {
                LOGGER.debug(String.format("Unable to retrieve" +
                        " the value of %s from the allocation.",  execution.getKey()), e);
            } catch (Exception e) {
                LOGGER.error("There was an error applying the subscribed method.", e);
            }
        }

        execution.executeWithDefault();
    }

    @Override
    public void emitEvent(String key, Double score) {
        this.eventEmitter.emit(key, score);
    }

    @Override
    public void emitEvent(String key) {
        this.eventEmitter.emit(key);
    }

    @Override
    public void confirm() {
        Allocator.AllocationStatus allocationStatus = allocator.getAllocationStatus();
        if (allocationStatus == Allocator.AllocationStatus.FETCHING) {
            allocator.sandBagConfirmation();
        } else if (allocationStatus == Allocator.AllocationStatus.RETRIEVED) {
            eventEmitter.confirm(store.get(participant.getUserId()));
        }
    }

    @Override
    public void contaminate() {
        Allocator.AllocationStatus allocationStatus = allocator.getAllocationStatus();
        if (allocationStatus == Allocator.AllocationStatus.FETCHING) {
            allocator.sandBagContamination();
        } else if (allocationStatus == Allocator.AllocationStatus.RETRIEVED) {
            eventEmitter.contaminate(store.get(participant.getUserId()));
        }
    }
}
