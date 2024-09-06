package com.example.paymentservice.web;

import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PaymentController {
    @GetMapping(value = "createPayment", produces = "application/json")
    public ResponseEntity<String> createOrder(){
        try {
            System.out.println("Welcome to Payment service ");
            return ResponseEntity.status(HttpStatus.OK).body("Payment Created");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
}
