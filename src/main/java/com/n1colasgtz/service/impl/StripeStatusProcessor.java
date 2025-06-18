package com.n1colasgtz.service.impl;

import com.n1colasgtz.model.PaymentRequest;
import com.n1colasgtz.model.response.PaymentStatusResponse;
import com.n1colasgtz.service.StatusProcessor;
import com.stripe.StripeClient;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.checkout.Session;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StripeStatusProcessor implements StatusProcessor {
    private static final Logger logger = LogManager.getLogger(StripeStatusProcessor.class);
    private final StripeClient stripeClient;

    public StripeStatusProcessor(StripeClient stripeClient) {
        this.stripeClient = stripeClient;
    }

    @Override
    public PaymentStatusResponse processStatus(PaymentRequest request) {
        try {
            logger.info("Processing status check for store: {}", request.getStoreId());
            if (request.getChargeId() != null && !request.getChargeId().trim().isEmpty()) {
                Charge charge = stripeClient.charges().retrieve(request.getChargeId());
                return PaymentStatusResponse.builder()
                        .status("success")
                        .paymentId(charge.getId())
                        .paymentStatus(charge.getStatus())
                        .amount(charge.getAmount())
                        .currency(charge.getCurrency())
                        .statusCode(200)
                        .build();
            } else if (request.getSessionId() != null && !request.getSessionId().trim().isEmpty()) {
                Session session = stripeClient.checkout().sessions().retrieve(request.getSessionId());
                return PaymentStatusResponse.builder()
                        .status("success")
                        .paymentId(session.getId())
                        .paymentStatus(session.getStatus())
                        .amount(session.getAmountTotal())
                        .currency(session.getCurrency())
                        .statusCode(200)
                        .build();
            } else {
                throw new IllegalArgumentException("Charge ID or Session ID is required for status check");
            }
        } catch (StripeException e) {
            logger.error("Stripe error: {}", e.getMessage(), e);
            return PaymentStatusResponse.builder()
                    .status("error")
                    .message("Stripe error: " + e.getMessage())
                    .statusCode(400)
                    .build();
        } catch (Exception e) {
            logger.error("Unexpected error: {}", e.getMessage(), e);
            return PaymentStatusResponse.builder()
                    .status("error")
                    .message("Unexpected error: " + e.getMessage())
                    .statusCode(500)
                    .build();
        }
    }
}