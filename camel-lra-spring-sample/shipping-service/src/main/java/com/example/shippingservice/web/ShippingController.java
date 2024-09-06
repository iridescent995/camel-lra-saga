package com.example.shippingservice.web;

import org.apache.camel.Exchange;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ShippingController {
    @GetMapping(value = "createShipping", produces = "application/json")
    public ResponseEntity<String> createOrder(){
        try {
            System.out.println("Welcome to Shipping service " );
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Shipping Error");
            return ResponseEntity.status(HttpStatus.OK).body("Shipping Done");
//            throw new RuntimeException();
        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
}
