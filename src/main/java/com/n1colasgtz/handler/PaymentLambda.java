package com.n1colasgtz.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.n1colasgtz.model.PaymentRequest;
import com.n1colasgtz.model.PaymentResponse;
import com.n1colasgtz.service.impl.AWSSecretsManagerStore;
import com.n1colasgtz.service.impl.JsonRequestParser;
import com.n1colasgtz.service.impl.StripePaymentProcessor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class PaymentLambda implements RequestHandler<Map<String, Object>, Map<String, Object>> {
    private static final Logger logger = LogManager.getLogger(PaymentLambda.class);
    private final JsonRequestParser requestParser;
    private final AWSSecretsManagerStore secretStore;
    private final StripePaymentProcessor paymentProcessor;

    public PaymentLambda() {
        ObjectMapper objectMapper = new ObjectMapper();
        this.requestParser = new JsonRequestParser(objectMapper);
        try {
            AWSSecretsManager client = AWSSecretsManagerClientBuilder.standard().build();
            this.secretStore = new AWSSecretsManagerStore(client);
        } catch (Exception e) {
            logger.error("Failed to initialize Secrets Manager client: {}", e.getMessage(), e);
            throw new RuntimeException("Secrets Manager initialization failed", e);
        }
        this.paymentProcessor = new StripePaymentProcessor();
    }

    // Constructor for dependency injection (testing)
    public PaymentLambda(JsonRequestParser requestParser, AWSSecretsManagerStore secretStore, StripePaymentProcessor paymentProcessor) {
        this.requestParser = requestParser;
        this.secretStore = secretStore;
        this.paymentProcessor = paymentProcessor;
    }

    @Override
    public Map<String, Object> handleRequest(Map<String, Object> input, Context context) {
        try {
            logger.info("Processing payment request: {}", input);
            PaymentRequest request = requestParser.parse(input);
            String apiKey = secretStore.getSecret(request.getStoreId());
            if (apiKey == null || apiKey.trim().isEmpty()) {
                throw new IllegalStateException("Stripe API key is missing for store: " + request.getStoreId());
            }
            PaymentResponse response = paymentProcessor.processPayment(request, apiKey);
            logger.info("Payment processed successfully: {}", response.getChargeId());
            return toResponseMap(response);
        } catch (Exception e) {
            logger.error("Error processing payment: {}", e.getMessage(), e);
            PaymentResponse errorResponse = PaymentResponse.builder()
                    .status("error")
                    .message("Internal error: " + e.getMessage())
                    .statusCode(500)
                    .build();
            return toResponseMap(errorResponse);
        }
    }

    private Map<String, Object> toResponseMap(PaymentResponse response) {
        Map<String, Object> result = new HashMap<>();
        result.put("statusCode", response.getStatusCode());
        try {
            result.put("body", new ObjectMapper().writeValueAsString(response));
        } catch (Exception e) {
            logger.error("Error serializing response: {}", e.getMessage(), e);
            result.put("body", "{\"status\":\"error\",\"message\":\"Response serialization failed\",\"statusCode\":500}");
        }
        return result;
    }
}