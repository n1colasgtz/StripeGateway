package com.n1colasgtz.service.impl;

import com.n1colasgtz.model.PaymentRequest;
import com.n1colasgtz.model.response.RefundResponse;
import com.n1colasgtz.service.RefundProcessor;
import com.stripe.StripeClient;
import com.stripe.exception.StripeException;
import com.stripe.model.Refund;
import com.stripe.param.RefundCreateParams;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StripeRefundProcessor implements RefundProcessor {
    private static final Logger logger = LogManager.getLogger(StripeRefundProcessor.class);
    private final StripeClient stripeClient;

    public StripeRefundProcessor(StripeClient stripeClient) {
        this.stripeClient = stripeClient;
    }

    @Override
    public RefundResponse processRefund(PaymentRequest request) {
        try {
            logger.info("Processing refund for store: {}", request.getStoreId());
            if (request.getChargeId() == null || request.getChargeId().trim().isEmpty()) {
                throw new IllegalArgumentException("Charge ID is required for refund");
            }
            RefundCreateParams.Builder paramsBuilder = RefundCreateParams.builder()
                    .setCharge(request.getChargeId());
            if (request.getAmount() != null && request.getAmount() > 0) {
                paramsBuilder.setAmount(request.getAmount());
            }
            Refund refund = stripeClient.refunds().create(paramsBuilder.build());
            return RefundResponse.builder()
                    .status("success")
                    .refundId(refund.getId())
                    .amount(refund.getAmount())
                    .currency(refund.getCurrency())
                    .statusCode(200)
                    .build();
        } catch (StripeException e) {
            logger.error("Stripe error: {}", e.getMessage(), e);
            return RefundResponse.builder()
                    .status("error")
                    .message("Stripe error: " + e.getMessage())
                    .statusCode(400)
                    .build();
        } catch (Exception e) {
            logger.error("Unexpected error: {}", e.getMessage(), e);
            return RefundResponse.builder()
                    .status("error")
                    .message("Unexpected error: " + e.getMessage())
                    .statusCode(500)
                    .build();
        }
    }
}