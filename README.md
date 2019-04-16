# Ascend Java SDK

#### Vocabulary

   **Participant:** The end user of the application, the individual who's actions are being recorded in the experiment.
   **Allocation:** The set of configurations that have been given to the participant, the values that are being
   experimented against.
   
   
### Import the SDK

1. Import the Ascend SDK.

    ```xml
        <dependency>
          <groupId>ai.evolv</groupId>
          <artifactId>ascend-sdk</artifactId>
          <version>0.5.1</version>
        </dependency>
    ```

### Client Initialization

1. Build an AscendConfig instance.
    ```java
       AscendConfig config = AscendConfig.builder(<environment_id>, <http_client>).build();
    ```
    
    *Note: The SDK uses a standard interface for the http backend see [HttpClient.](https://github.com/evolv-ai/ascend-java-sdk/blob/master/src/main/java/ai/evolv/HttpClient.java). 
    When configuring the AscendClient you must specify what implementation of HttpClient to use (or implement your own). See "Custom Http Backend" for more details.*

2. Initialize the AscendClient.
    ```java
       AscendClient ascendClient = AscendClientFactory.init(config);
    ```
    
### Confirm the Allocation
    
1. Once the AscendClient has been initialized, confirm the participant into the experiment.
    ```java
       ascendClient.confirm();
    ```
    *Note: After the AscendClient has initialized, it is important to confirm the participant into the experiment. This action
     records the participant's allocation and sends the info back to Ascend.*

### Value Retrieval

1. Retrieve values from Ascend.
    ```java
       T value = ascendClient.get(<key_for_value>, <default_value>);
    ```
    
   *Note: The return value's type is decided by the provided default value's type. If there is an issue retrieving the
   requested value, the default value will be returned in its place. This method is blocking, it will wait until the
   allocation has been received.*
   
### Value Subscription

You may want to use a value from your allocation without blocking the execution of your application. If this is true, you can
subscribe to a value and apply any actions as a result of it asynchronously.

1. Subscribe to a value from Ascend.
    ```java
        ascendClient.subscribe(<key_for_value>, <default_value>, value -> {
            Your code...
        });
    ```
    
    *Note: The return value's type is decided by the provided default value's type. If there is an issue retrieving the
    requested value, the default value will be returned in its place. If you have a previous allocation stored the 
    value will be retrieved and then your code will be executed. When the new allocation is retrieved if the value
    differs from the previously stored allocation then your code will be ran again with the new value. If your code 
    results in an Exception it will be logged but no runtime exception is thrown.*
    
### Custom Events (optional)

Sometimes you may want to record certain events that occurred during the participant's session. An example of an event
that is important to record is a "conversion" event. If you implemented the SDK in a shopping app, you could send the
"conversion" event when the participant presses the checkout button.

1. Emit a custom event.
    ```java
       ascendClient.emitEvent(<event_type>);
    ```
    
    AND / OR

2. Emit a custom event with an associated score.
    ```java
       ascendClient.emitEvent(<event_type>, <score>);
    ```
    
### Contaminate the Allocation (optional)

Sometimes it may be necessary to contaminate the participant's allocation. Meaning, that you may not want that participant's session to be recorded into the experiment.

1. Contaminate the participant's allocation.
    ```java
       ascendClient.contaminate();
    ```    
    

### Custom Http Backend    

The Ascend Client uses a standard Http interface for making Http requests to the Participant API, see [HttpClient.](https://github.com/evolv-ai/ascend-java-sdk/blob/master/src/main/java/ai/evolv/HttpClient.java)
We have built a couple of implementations that are available out of the box, you can also feel free to create your own. At initialization you will need to pass in the HttpClient that you want to use to the 
AscendConfig class.

1. Choose an HttpClient and pass it to the AscendConfig.

    ```java
       HttpClient httpClient = new AsyncHttpClient();
       AscendConfig config = AscendConfig.builder('sandbox', httpClient).build();
       AscendClient ascendClient = AscendClientFactory.init(config);
    ```
    *Note: Replace 'sandbox' with your own environment id.
    *Note: The above HttpClient implementation uses org.asynchttpclient:async-http-httpClient as its http httpClient. In order to use
    the implementation you will need to bring the package into your dependencies.*
    
### Custom Allocation Store (optional)

Once a participant has been allocated into an experiment you may want to retain the allocations they received. To do this, create a custom allocation store by implementing the AscendAllocationStore interface. You can supply the
custom allocation store to the AscendClient when you build the AscendConfig.

1. Supply the allocation store to the AscendClient.
    ```java
       AscendConfig config = AscendConfig.builder(<environment_id>, <http_client>)
           .setAscendAllocationStore(<custom_store>)
           .build();
       AscendClient ascendClient = AscendClientFactory.init(config);
   ```
   
### Optional Configurations

There are several optional configurations available through the AscendConfig builder, check out the AscendConfig
documentation to see what options are available.

### About Evolv and the Ascend Product

Evolv Delivers Autonomous Optimization Across Web & Mobile.

You can find out more by visiting: https://www.evolv.ai/
