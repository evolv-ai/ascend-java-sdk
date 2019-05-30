package ai.evolv;

public class AscendConfig {

    static final String DEFAULT_HTTP_SCHEME = "https";
    static final String DEFAULT_DOMAIN = "participants.evolv.ai";
    static final String DEFAULT_API_VERSION = "v1";

    private static final int DEFAULT_ALLOCATION_STORE_SIZE = 1000;

    private final String httpScheme;
    private final String domain;
    private final String version;
    private final String environmentId;
    private final AscendAllocationStore ascendAllocationStore;
    private final AscendParticipant participant;
    private final HttpClient httpClient;
    private final ExecutionQueue executionQueue;

    private AscendConfig(String httpScheme, String domain, String version,
                         String environmentId,
                         AscendAllocationStore ascendAllocationStore,
                         AscendParticipant participant,
                         HttpClient httpClient) {
        this.httpScheme = httpScheme;
        this.domain = domain;
        this.version = version;
        this.environmentId = environmentId;
        this.ascendAllocationStore = ascendAllocationStore;
        this.participant = participant;
        this.httpClient = httpClient;
        this.executionQueue = new ExecutionQueue();
    }

    public static Builder builder(String environmentId, HttpClient httpClient) {
        return new Builder(environmentId, httpClient);
    }

    String getHttpScheme() {
        return httpScheme;
    }

    String getDomain() {
        return domain;
    }

    String getVersion() {
        return version;
    }

    String getEnvironmentId() {
        return environmentId;
    }

    AscendAllocationStore getAscendAllocationStore() {
        return ascendAllocationStore;
    }

    AscendParticipant getAscendParticipant() {
        return participant;
    }

    HttpClient getHttpClient() {
        return this.httpClient;
    }

    ExecutionQueue getExecutionQueue() {
        return this.executionQueue;
    }

    public static class Builder {

        private int allocationStoreSize = DEFAULT_ALLOCATION_STORE_SIZE;
        private String httpScheme = DEFAULT_HTTP_SCHEME;
        private String domain = DEFAULT_DOMAIN;
        private String version = DEFAULT_API_VERSION;
        private AscendAllocationStore allocationStore;
        private AscendParticipant participant;

        private String environmentId;
        private HttpClient httpClient;

        /**
         * Responsible for creating an instance of AscendClientImpl.
         * <p>
         *     Builds an instance of the AscendClientImpl. The only required parameter is the
         *     customer's environment id.
         * </p>
         * @param environmentId unique id representing a customer's environment
         */
        Builder(String environmentId, HttpClient httpClient) {
            this.environmentId = environmentId;
            this.httpClient = httpClient;
        }

        /**
         * Sets the domain of the underlying ascendParticipant api.
         * @param domain the domain of the ascendParticipant api
         * @return AscendClientBuilder class
         */
        public Builder setDomain(String domain) {
            this.domain = domain;
            return this;
        }

        /**
         * Version of the underlying ascendParticipant api.
         * @param version representation of the required ascendParticipant api version
         * @return AscendClientBuilder class
         */
        public Builder setVersion(String version) {
            this.version = version;
            return this;
        }

        /**
         * Sets up a custom AscendAllocationStore. Store needs to implement the
         * AscendAllocationStore interface.
         * @param allocationStore a custom built allocation store
         * @return AscendClientBuilder class
         */
        public Builder setAscendAllocationStore(AscendAllocationStore allocationStore) {
            this.allocationStore = allocationStore;
            return this;
        }

        /**
         * Sets up a custom AscendParticipant.
         *
         * @deprecated use {@link ai.evolv.AscendClientFactory} init method with AscendConfig
         *      and AscendParticipant params instead.
         *
         * @param participant a custom build ascendParticipant
         * @return AscendClientBuilder class
         */
        @Deprecated
        public Builder setAscendParticipant(AscendParticipant participant) {
            this.participant = participant;
            return this;
        }

        /**
         * Tells the SDK to use either http or https.
         * @param scheme either http or https
         * @return AscendClientBuilder class
         */
        public Builder setHttpScheme(String scheme) {
            this.httpScheme = scheme;
            return this;
        }

        /**
         * Sets the DefaultAllocationStores size.
         * @param size number of entries allowed in the default allocation store
         * @return AscendClientBuilder class
         */
        public Builder setDefaultAllocationStoreSize(int size) {
            this.allocationStoreSize = size;
            return this;
        }

        /**
         * Builds an instance of AscendClientImpl.
         * @return an AscendClientImpl instance
         */
        public AscendConfig build() {
            if (allocationStore == null) {
                allocationStore = new DefaultAllocationStore(allocationStoreSize);
            }

            return new AscendConfig(httpScheme, domain, version, environmentId,
                    allocationStore,
                    participant,
                    httpClient);
        }

    }

}
