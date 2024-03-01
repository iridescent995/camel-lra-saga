package com.example.trackingservice.routes;

import com.example.trackingservice.service.CancelTrackingService;
import com.example.trackingservice.service.CreateTrackingService;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.SagaPropagation;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.saga.InMemorySagaService;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class TrackingRoute extends RouteBuilder {

    private final CreateTrackingService createTrackingService;
    private final CancelTrackingService cancelTrackingService;

    public TrackingRoute(CreateTrackingService createTrackingService, CancelTrackingService cancelTrackingService) {
        this.createTrackingService = createTrackingService;
        this.cancelTrackingService = cancelTrackingService;
    }

    @Override
    public void configure() throws Exception {

        org.apache.camel.service.lra.LRASagaService sagaService = new org.apache.camel.service.lra.LRASagaService();
        sagaService.setCoordinatorUrl("http://localhost:8080");
        sagaService.setLocalParticipantUrl("http://0.0.0.0:8005");


        getContext().addService(sagaService);

//        restConfiguration().component("servlet")
//                .bindingMode(RestBindingMode.json);

        from("direct:createTracking")
                        .saga().timeout(10, TimeUnit.SECONDS).to("direct:newTracking");

        from("direct:newTracking").saga()
                .propagation(SagaPropagation.MANDATORY)
                .timeout(10, TimeUnit.SECONDS)
                .option("id", header("id"))
                .compensation("direct:cancelTracking")
                .completion("direct:completeTracking")
                .transform().header(Exchange.SAGA_LONG_RUNNING_ACTION)
                .bean(createTrackingService, "createTracking")
                .end();

        from("direct:completeTracking")
                .transform().header(Exchange.SAGA_LONG_RUNNING_ACTION)
                .bean(createTrackingService, "completeTracking");

        from("direct:cancelTracking")
                .transform().header(Exchange.SAGA_LONG_RUNNING_ACTION)
                .bean(cancelTrackingService, "cancelTracking");

    }
}