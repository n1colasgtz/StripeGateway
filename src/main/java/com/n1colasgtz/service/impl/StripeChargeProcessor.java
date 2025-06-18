package com.n1colasgtz.service.impl;

import com.n1colasgtz.model.PaymentRequest;
import com.n1colasgtz.model.response.ChargeResponse;
import com.n1colasgtz.service.ChargeProcessor;
import com.stripe.StripeClient;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.param.ChargeCreateParams;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StripeChargeProcessor implements ChargeProcessor {
    private static final Logger logger = LogManager.getLogger(StripeChargeProcessor.class);
    private final StripeClient stripeClient;

    public StripeChargeProcessor(StripeClient stripeClient) {
        this.stripeClient = stripeClient;
    }

    @Override
    public ChargeResponse processCharge(PaymentRequest request) {
        try {
            logger.info("Processing charge for store: {}", request.getStoreId());
            if (request.getPaymentToken() == null || request.getPaymentToken().trim().isEmpty()) {
                throw new IllegalArgumentException("Payment token is required for charge");
            }
            ChargeCreateParams params = ChargeCreateParams.builder()
                    .setAmount(request.getAmount())
                    .setCurrency(request.getCurrency())
                    .setSource(request.getPaymentToken())
                    .setDescription(request.getDescription())
                    .build();
            Charge charge = stripeClient.charges().create(params);
            return ChargeResponse.builder()
                    .status("success")
                    .chargeId(charge.getId())
                    .amount(charge.getAmount())
                    .currency(charge.getCurrency())
                    .statusCode(200)
                    .build();
        } catch (StripeException e) {
            logger.error("Stripe error: {}", e.getMessage(), e);
            return ChargeResponse.builder()
                    .status("error")
                    .message("Stripe error: " + e.getMessage())
                    .statusCode(400)
                    .build();
        } catch (Exception e) {
            logger.error("Unexpected error: {}", e.getMessage(), e);
            return ChargeResponse.builder()
                    .status("error")
                    .message("Unexpected error: " + e.getMessage())
                    .statusCode(500)
                    .build();
        }
    }
}