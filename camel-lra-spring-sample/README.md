# Sample Application
This code sample demos the LRA lifecycle. 

<img width="707" alt="image" src="https://github.com/user-attachments/assets/c78e502f-36f6-4257-8104-a34e56c670e3">

### Pre requisite
We need a running LRA Coordinator, to which our application can connect. To know more refer the root folder readme file. 

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

## Starting LRA
Open postman and hit this **GET** endpoint:
```
localhost:8001/camel/createOrder
```
Note: By Default application run on **8001** port. Which can be changed under `application.yml` file

```
server:
  port: 8001
```
on hitting this url in response you will get unique LRA id for this transaction, same needs to be passed to complete/cancel the LRA
```
Sample response: 
Long-Running-Action:http://localhost:8080/lra-coordinator/0_ffffc0a80109_eae9_66d97349_167
```

## Completing LRA
```
localhost:8001/camel/completeOrder
```
in headers pass 
```
Long-Running-Action:http://localhost:8080/lra-coordinator/...someid...
```

## Cancel LRA

```
localhost:8001/camel/cancelOrder
```
in headers pass 
```
Long-Running-Action:http://localhost:8080/lra-coordinator/...someid...
```
