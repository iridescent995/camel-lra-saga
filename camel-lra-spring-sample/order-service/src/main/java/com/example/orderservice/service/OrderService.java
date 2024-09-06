package com.example.orderservice.service;

import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

@Component
public class OrderService {
    public void createOrder(Exchange exchange) throws Exception {
        System.out.println("\nStarting Order");
//        throw new Exception();
        System.out.println(exchange.getMessage().getHeader("Long-Running-Action"));
    }

    public void completeOrder(Exchange exchange) throws Exception {
        System.out.println("Completing Orders");
    }

    public void cancelOrder(Exchange exchange) throws Exception {
        System.out.println("\n\n\nCancel Orders\n\n\n");
    }
}
