package com.n1colasgtz.service.impl;

import com.n1colasgtz.model.PaymentRequest;
import com.n1colasgtz.model.response.WebhookResponse;
import com.n1colasgtz.service.WebhookProcessor;
import com.stripe.StripeClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StripeWebhookProcessor implements WebhookProcessor {
    private static final Logger logger = LogManager.getLogger(StripeWebhookProcessor.class);
    private final StripeClient stripeClient;

    public StripeWebhookProcessor(StripeClient stripeClient) {
        this.stripeClient = stripeClient;
    }

    @Override
    public WebhookResponse processWebhook(PaymentRequest request) {
        try {
            logger.info("Processing webhook for store: {}", request.getStoreId());
            if (request.getWebhookEvent() == null || request.getWebhookEvent().isEmpty()) {
                throw new IllegalArgumentException("Webhook event data is required");
            }
            String eventId = (String) request.getWebhookEvent().get("id");
            String eventType = (String) request.getWebhookEvent().get("type");
            if (eventId == null || eventType == null) {
                throw new IllegalArgumentException("Webhook event must contain id and type");
            }
            logger.debug("Received webhook event: id={}, type={}", eventId, eventType);
            // Add custom logic to handle specific event types (e.g., charge.succeeded)
            return WebhookResponse.builder()
                    .status("success")
                    .eventId(eventId)
                    .statusCode(200)
                    .build();
        } catch (Exception e) {
            logger.error("Error processing webhook: {}", e.getMessage(), e);
            return WebhookResponse.builder()
                    .status("error")
                    .message("Webhook error: " + e.getMessage())
                    .statusCode(400)
                    .build();
        }
    }
}