package com.example.trackingservice.service;

import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
@Component
public class CreateTrackingService {
    public void createTracking(Exchange exchange) throws Exception {
        System.out.println("creating Tracking");
//        Map<String, Object> map = new HashMap<>();
//        map.put("message", "yoo! Tracking added successfully");
        throw new Exception();
//        exchange.getMessage().setBody(map);
    }

    public void completeTracking(Exchange exchange) throws Exception {
        System.out.println("Completing Tracking");
        Map<String, Object> map = new HashMap<>();
        map.put("message", "yoo! Tracking completed");
//        throw new Exception();
        exchange.getMessage().setBody(map);
    }
}
