package com.n1colasgtz.service.impl;

import com.n1colasgtz.model.PaymentRequest;
import com.n1colasgtz.model.PaymentResponse;
import com.n1colasgtz.service.PaymentProcessor;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class StripePaymentProcessor implements PaymentProcessor {
    private static final Logger logger = LogManager.getLogger(StripePaymentProcessor.class);

    @Override
    public PaymentResponse processPayment(PaymentRequest request, String apiKey) {
        try {
            logger.info("Processing payment for store: {}", request.getStoreId());
            Stripe.apiKey = apiKey;
            Map<String, Object> chargeParams = new HashMap<>();
            chargeParams.put("amount", request.getAmount());
            chargeParams.put("currency", request.getCurrency());
            chargeParams.put("source", request.getPaymentToken());
            chargeParams.put("description", request.getDescription());
            Charge charge = Charge.create(chargeParams);
            return PaymentResponse.builder()
                    .status("success")
                    .chargeId(charge.getId())
                    .amount(charge.getAmount())
                    .currency(charge.getCurrency())
                    .statusCode(200)
                    .build();
        } catch (StripeException e) {
            logger.error("Stripe error: {}", e.getMessage(), e);
            return PaymentResponse.builder()
                    .status("error")
                    .message("Stripe error: " + e.getMessage())
                    .statusCode(400)
                    .build();
        }
    }
}