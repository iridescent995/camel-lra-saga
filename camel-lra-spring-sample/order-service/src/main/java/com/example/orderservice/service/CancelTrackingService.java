package com.example.orderservice.service;

import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class CancelTrackingService {
    public void cancelTracking(Exchange exchange){
        System.out.println("Canceling Tracking");
        Map<String, Object> map = new HashMap<>();
        map.put("message", "oh No! Tracking canceled");
        exchange.getMessage().setBody(map);
    }
}
