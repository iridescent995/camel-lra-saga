# camel-lra-saga
This code example shows how to configure LRA SAGA SERVICE using Apache Camel

### Apache Camel Documentation 
https://camel.apache.org/components/4.4.x/eips/saga-eip.html#_using_the_lra_saga_service

### Pre requisite
We need a running LRA Coordinator, to which our application can connect. 

> There is a lack of documentation from both apache and narayana on how to run LRA Coordinator, which is very dissapointing. But I figured it out through many searches.

To run LRA you need an execuatable JAR. One is provided by quarkus. Clone this repo: 

https://github.com/jbosstm/lra-coordinator-quarkus

Run: 
```
mvn clean package
```
You can run the application by running
```
java -jar target/quarkus-app/quarkus-run.jar
```

By default app runs on port **8080** .You will get something like this: 
```
C:\Users\...\Desktop\Saga\LRA\lra-coordinator-quarkus-main>java -jar target/quarkus-app/quarkus-run.jar
__  ____  __  _____   ___  __ ____  ______
 --/ __ \/ / / / _ | / _ \/ //_/ / / / __/
 -/ /_/ / /_/ / __ |/ , _/ ,< / /_/ /\ \
--\___\_\____/_/ |_/_/|_/_/|_|\____/___/
2024-03-01 09:32:33,746 INFO  [io.quarkus] (main) lra-coordinator-quarkus 1.0.0-SNAPSHOT on JVM (powered by Quarkus 3.8.0) started in 1.085s. Listening on: http://0.0.0.0:8080
2024-03-01 09:32:33,752 INFO  [io.quarkus] (main) Profile prod activated.
2024-03-01 09:32:33,752 INFO  [io.quarkus] (main) Installed features: [cdi, rest-client-reactive, resteasy-reactive, resteasy-reactive-jackson, smallrye-context-propagation, smallrye-fault-tolerance, smallrye-openapi, swagger-ui, vertx]
```

#### Verify LRA Coordination status
If you are able to access this link, Everything is fine and you can proceed to coding. 

http://localhost:8080/lra-coordinator




### Code Walkthrough 
In this example we will create an tracking service, using Spring Boot and Apache Camel DSL. 
We will create 1 route `direct:createTracking` with 2 saga routes for `compensation` and `completion`

But before that while creating route definition, we need to configure **LRASagaService** 

```
        org.apache.camel.service.lra.LRASagaService sagaService = new org.apache.camel.service.lra.LRASagaService();
        sagaService.setCoordinatorUrl("http://localhost:8080");
        sagaService.setLocalParticipantUrl("http://0.0.0.0:8005");
        getContext().addService(sagaService);
```
Few things to keep in mind:
1. `setCoordinatorUrl` corresponds to host and port where LRA Coordinator is running.
2. `setLocalParticipantUrl` corresponds to how LRA Coordination looks at the host. As you can see below while starting, LRA is listnening on host **0.0.0.0** and our service is running on port **8005**. Note: there is no context part for our endpoints, otherwise we need to include that too. 
```
__  ____  __  _____   ___  __ ____  ______
 --/ __ \/ / / / _ | / _ \/ //_/ / / / __/
 -/ /_/ / /_/ / __ |/ , _/ ,< / /_/ /\ \
--\___\_\____/_/ |_/_/|_/_/|_|\____/___/
2024-03-01 09:32:33,746 INFO  [io.quarkus] (main) lra-coordinator-quarkus 1.0.0-SNAPSHOT on JVM (powered by Quarkus 3.8.0) started in 1.085s. Listening on: http://0.0.0.0:8080
2024-03-01 09:32:33,752 INFO  [io.quarkus] (main) Profile prod activated.
```

### Route Definition 
```
        from("direct:createTracking")
                        .saga().timeout(10, TimeUnit.SECONDS).to("direct:newTracking");

        from("direct:newTracking").saga()
                .propagation(SagaPropagation.MANDATORY)
                .timeout(10, TimeUnit.SECONDS)
                .option("id", header("id"))
                .compensation("direct:cancelTracking")
                .completion("direct:completeTracking")
                .transform().header(Exchange.SAGA_LONG_RUNNING_ACTION)
                .bean(createTrackingService, "createTracking")
                .end();

        from("direct:completeTracking")
                .transform().header(Exchange.SAGA_LONG_RUNNING_ACTION)
                .bean(createTrackingService, "completeTracking");

        from("direct:cancelTracking")
                .transform().header(Exchange.SAGA_LONG_RUNNING_ACTION)
                .bean(cancelTrackingService, "cancelTracking");
```

### Participant Definition
```
    @PutMapping(value = "lra-participant/complete", produces = "application/json")
    public ResponseEntity<String> completeTracking(){
        try {
            System.out.println("Completing Tracking");
            return ResponseEntity.status(HttpStatus.OK).body(producerTemplate.requestBody("direct:completeTracking","").toString());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
    @PutMapping(value = "lra-participant/compensate", produces = "application/json")
    public ResponseEntity<String> compensateTracking(){
        try {
            System.out.println("compensate Tracking");
            return ResponseEntity.status(HttpStatus.OK).body(producerTemplate.requestBody("direct:cancelTracking","").toString());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
```

Logic of execution remains same as in memory saga service, on fail `compensate` is called, while on completion `complete` is called. 

# How to Run this Application 
This is a simple maven application, just clone the repo and open cmd and run:

To Compile: 
```
mvn clean install
```

To run the spring application
```
mvn clean compile exec:java
```

# How to Test the Application 
Open postman and hit this **GET** endpoint:
```
localhost:8006/createTracking
```
Note: By Default application run on **8005** port. Which can be changed under `application.yml` file

```
server:
  port: 8005
```

You can also check the running LRA client status in this URL

http://localhost:8080/lra-coordinator


