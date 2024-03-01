package com.example.trackingservice.web;
import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TrackingController {
    @Autowired
    ProducerTemplate producerTemplate;

    @GetMapping(value = "createTracking", produces = "application/json")
    public ResponseEntity<String> createOrder(){
        try {
            System.out.println("Welcome to Tracking service");
            return ResponseEntity.status(HttpStatus.OK).body(producerTemplate.requestBody("direct:createTracking","").toString());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

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
}
