package com.example.orderservice.service;

import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Component
public class PaymentService {
    @Autowired
    ProducerTemplate producerTemplate;

    @Autowired
    RestTemplate restTemplate;


    public void createPayment(Exchange exchange) {
        System.out.println("\nStarting Payment");
        System.out.println(exchange.getMessage().getHeader("Long-Running-Action"));

        URI uri = UriComponentsBuilder.fromUri(URI.create("http://localhost:8002/createPayment"))
                .build()
                .toUri();



        try{
            ResponseEntity<String> responseEntity = restTemplate.getForEntity(uri, String.class);
            System.out.println("Save Response for Payment service: \n" + responseEntity.getBody());
        }
        catch (Exception e){
            System.out.println("Error in Save Response for Payment service: \n");
            producerTemplate.requestBodyAndHeader("seda:operationCanceled","", "Long-Running-Action", exchange.getMessage().getHeader("Long-Running-Action"));

        }


//        producerTemplate.requestBodyAndHeader("seda:operationCompleted","", "Long-Running-Action", exchange.getMessage().getHeader("Long-Running-Action"));
    }

    public void completePayment(Exchange exchange) throws Exception {
        System.out.println("Completing Payment");
    }

    public void cancelPayment(Exchange exchange) throws Exception {
        System.out.println("Cancel Payment");
    }
}
