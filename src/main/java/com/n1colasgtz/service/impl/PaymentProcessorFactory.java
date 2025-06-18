package com.n1colasgtz.service.impl;

import com.n1colasgtz.model.PaymentRequest;
import com.n1colasgtz.service.ChargeProcessor;
import com.n1colasgtz.service.PaymentLinkProcessor;
import com.stripe.StripeClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PaymentProcessorFactory {
    private static final Logger logger = LogManager.getLogger(PaymentProcessorFactory.class);
    private final StripeClient stripeClient;

    public PaymentProcessorFactory(StripeClient stripeClient) {
        this.stripeClient = stripeClient;
    }

    public Object processPayment(PaymentRequest request) {
        String requestType = request.getRequestType();
        logger.debug("Selecting processor for request type: {}", requestType);

        if ("CHARGE".equalsIgnoreCase(requestType)) {
            ChargeProcessor processor = new StripeChargeProcessor(stripeClient);
            return processor.processCharge(request);
        } else if ("PAYMENT_LINK".equalsIgnoreCase(requestType)) {
            PaymentLinkProcessor processor = new StripePaymentLinkProcessor(stripeClient);
            return processor.processPaymentLink(request);
        } else {
            throw new IllegalArgumentException("Invalid request type: " + requestType);
        }
    }
}