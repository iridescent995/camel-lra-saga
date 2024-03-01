# camel-lra-saga
This code example shows how to configure LRA SAGA SERVICE using Apache Camel

### Apache Camel Documentation 
https://camel.apache.org/components/4.4.x/eips/saga-eip.html#_using_the_lra_saga_service

### Code Walkthrough 




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
localhost:8006/createInventory
```
Note: By Default application run on **8006** port. Which can be changed under `application.yml` file

```
server:
  port: 8006
```

