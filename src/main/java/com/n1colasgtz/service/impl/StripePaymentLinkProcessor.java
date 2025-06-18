package com.n1colasgtz.service.impl;

import com.n1colasgtz.model.PaymentRequest;
import com.n1colasgtz.model.response.PaymentLinkResponse;
import com.n1colasgtz.service.PaymentLinkProcessor;
import com.stripe.StripeClient;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StripePaymentLinkProcessor implements PaymentLinkProcessor {
    private static final Logger logger = LogManager.getLogger(StripePaymentLinkProcessor.class);
    private final StripeClient stripeClient;

    public StripePaymentLinkProcessor(StripeClient stripeClient) {
        this.stripeClient = stripeClient;
    }

    @Override
    public PaymentLinkResponse processPaymentLink(PaymentRequest request) {
        try {
            logger.info("Processing payment link for store: {}", request.getStoreId());
            if (request.getSuccessUrl() == null || request.getCancelUrl() == null) {
                throw new IllegalArgumentException("Success and cancel URLs are required for payment link");
            }
            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setPriceData(
                                            SessionCreateParams.LineItem.PriceData.builder()
                                                    .setCurrency(request.getCurrency())
                                                    .setUnitAmount(request.getAmount())
                                                    .setProductData(
                                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                    .setName(request.getDescription())
                                                                    .build()
                                                    )
                                                    .build()
                                    )
                                    .setQuantity(1L)
                                    .build()
                    )
                    .setSuccessUrl(request.getSuccessUrl())
                    .setCancelUrl(request.getCancelUrl())
                    .build();
            Session session = stripeClient.checkout().sessions().create(params);
            return PaymentLinkResponse.builder()
                    .status("success")
                    .paymentLink(session.getUrl())
                    .statusCode(200)
                    .build();
        } catch (StripeException e) {
            logger.error("Stripe error: {}", e.getMessage(), e);
            return PaymentLinkResponse.builder()
                    .status("error")
                    .message("Stripe error: " + e.getMessage())
                    .statusCode(400)
                    .build();
        } catch (Exception e) {
            logger.error("Unexpected error: {}", e.getMessage(), e);
            return PaymentLinkResponse.builder()
                    .status("error")
                    .message("Unexpected error: " + e.getMessage())
                    .statusCode(500)
                    .build();
        }
    }
}