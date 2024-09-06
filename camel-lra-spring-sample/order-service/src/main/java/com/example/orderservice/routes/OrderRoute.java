package com.example.orderservice.routes;

import com.example.orderservice.service.OrderService;
import com.example.orderservice.service.PaymentService;
import com.example.orderservice.service.ShippingService;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.SagaCompletionMode;
import org.apache.camel.model.SagaPropagation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class OrderRoute extends RouteBuilder {

    @Autowired
    private Environment env;

    private final OrderService orderService;
    private final PaymentService paymentService;
    private final ShippingService shippingService;

    public OrderRoute(OrderService orderService, PaymentService paymentService, ShippingService shippingService) {
        this.orderService = orderService;
        this.paymentService = paymentService;
        this.shippingService = shippingService;
    }


    @Override
    public void configure() throws Exception {

        rest().consumes("application/json")
                .produces("application/json").get("/createOrder")
                .to("direct:startSaga");

        rest().consumes("application/json")
                .produces("application/json").get("/completeOrder")
                .to("seda:operationCompleted");

        rest().consumes("application/json")
                .produces("application/json").get("/cancelOrder")
                .to("seda:operationCanceled");

        from("direct:startSaga")
                .saga()
                .completionMode(SagaCompletionMode.MANUAL)
                .completion("direct:finalize")
                .timeout(2, TimeUnit.HOURS)
                .to("seda:newOrder")
                .to("seda:newPayment")
                .to("seda:newShipping");


//        from("direct:startSaga")
//                .saga()
//                .to("direct:newOrder")
//                .to("direct:newPayment")
//                .to("direct:newShipping");

        from("seda:newOrder").saga()
                .propagation(SagaPropagation.MANDATORY)
                .option("id", header("id"))
                .compensation("direct:cancelOrder")
                .completion("direct:completeOrder")
                .transform().header(Exchange.SAGA_LONG_RUNNING_ACTION)
                .log("newOrder")
                .bean(orderService, "createOrder")
                .end();

        from("direct:completeOrder")
                .transform().header(Exchange.SAGA_LONG_RUNNING_ACTION)
                .bean(orderService, "completeOrder");

        from("direct:cancelOrder")
                .transform().header(Exchange.SAGA_LONG_RUNNING_ACTION)
                .bean(orderService, "cancelOrder");

        from("seda:newPayment").saga()
                .propagation(SagaPropagation.MANDATORY)
                .option("id", header("id"))
                .compensation("direct:cancelPayment")
                .completion("direct:completePayment")
                .transform().header(Exchange.SAGA_LONG_RUNNING_ACTION)
                .setHeader(Exchange.HTTP_METHOD, simple("GET"))
                .bean(paymentService, "createPayment")
                .to("seda:checkInventory")
                .end();

        from("direct:completePayment")
                .transform().header(Exchange.SAGA_LONG_RUNNING_ACTION)
                .bean(paymentService, "completePayment");

        from("direct:cancelPayment")
                .transform().header(Exchange.SAGA_LONG_RUNNING_ACTION)
                .bean(paymentService, "cancelPayment");

        from("seda:newShipping").saga()
                .propagation(SagaPropagation.MANDATORY)
                .option("id", header("id"))
                .compensation("direct:cancelShipping")
                .completion("direct:completeShipping")
                .transform().header(Exchange.SAGA_LONG_RUNNING_ACTION)
                .setHeader(Exchange.HTTP_METHOD, simple("GET"))
                .bean(shippingService, "createShipping")
                .end();

        from("direct:completeShipping")
                .transform().header(Exchange.SAGA_LONG_RUNNING_ACTION)
                .bean(shippingService, "completeShipping");

        from("direct:cancelShipping")
                .transform().header(Exchange.SAGA_LONG_RUNNING_ACTION)
                .bean(shippingService, "cancelShipping");


        //SAGA Routes and Operations
        from("seda:operationCompleted") // an asynchronous callback
                .saga()
                .propagation(SagaPropagation.MANDATORY)
                .to("saga:complete") // complete the current saga manually (saga component)
                .end();

        from("seda:operationCanceled") // an asynchronous callback
                .saga()
                .propagation(SagaPropagation.MANDATORY)
                .to("saga:compensate") //  compensate the current saga manually (saga component)
                .end();


        from("direct:finalize")
                .transform().header(Exchange.SAGA_LONG_RUNNING_ACTION)
                .log("finalize");

    }
}