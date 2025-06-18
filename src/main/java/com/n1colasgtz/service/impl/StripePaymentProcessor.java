package com.n1colasgtz.service.impl;

import com.n1colasgtz.model.PaymentRequest;
import com.n1colasgtz.model.PaymentResponse;
import com.n1colasgtz.service.PaymentProcessor;
import com.stripe.StripeClient;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.param.ChargeCreateParams;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StripePaymentProcessor implements PaymentProcessor {
    private static final Logger logger = LogManager.getLogger(StripePaymentProcessor.class);

    @Override
    public PaymentResponse processPayment(PaymentRequest request, String apiKey) {
        try {
            logger.info("Processing payment for store: {}", request.getStoreId());
            StripeClient stripeClient = new StripeClient(apiKey);

            ChargeCreateParams params = ChargeCreateParams.builder()
                    .setAmount(request.getAmount())
                    .setCurrency(request.getCurrency())
                    .setSource(request.getPaymentToken())
                    .setDescription(request.getDescription())
                    .build();

            Charge charge = stripeClient.charges().create(params);
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
        } catch (Exception e) {
            logger.error("Unexpected error: {}", e.getMessage(), e);
            return PaymentResponse.builder()
                    .status("error")
                    .message("Unexpected error: " + e.getMessage())
                    .statusCode(500)
                    .build();
        }
    }
}