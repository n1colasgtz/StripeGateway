package com.n1colasgtz.service.impl;

import com.n1colasgtz.model.PaymentRequest;
import com.n1colasgtz.service.ChargeProcessor;
import com.n1colasgtz.service.PaymentLinkProcessor;
import com.n1colasgtz.service.RefundProcessor;
import com.n1colasgtz.service.StatusProcessor;
import com.n1colasgtz.service.WebhookProcessor;
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

        switch (requestType.toUpperCase()) {
            case "CHARGE":
                ChargeProcessor chargeProcessor = new StripeChargeProcessor(stripeClient);
                return chargeProcessor.processCharge(request);
            case "PAYMENT_LINK":
                PaymentLinkProcessor linkProcessor = new StripePaymentLinkProcessor(stripeClient);
                return linkProcessor.processPaymentLink(request);
            case "REFUND":
                RefundProcessor refundProcessor = new StripeRefundProcessor(stripeClient);
                return refundProcessor.processRefund(request);
            case "STATUS":
                StatusProcessor statusProcessor = new StripeStatusProcessor(stripeClient);
                return statusProcessor.processStatus(request);
            case "WEBHOOK":
                WebhookProcessor webhookProcessor = new StripeWebhookProcessor(stripeClient);
                return webhookProcessor.processWebhook(request);
            default:
                throw new IllegalArgumentException("Invalid request type: " + requestType);
        }
    }
}