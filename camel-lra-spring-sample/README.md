# Sample Application
This code sample demos the LRA lifecycle. 

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

Open postman and hit this **GET** endpoint:
```
localhost:8001/camel/createOrder
```
Note: By Default application run on **8001** port. Which can be changed under `application.yml` file

```
server:
  port: 8001
```


