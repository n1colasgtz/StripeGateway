package com.n1colasgtz.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.n1colasgtz.model.PaymentRequest;
import com.n1colasgtz.model.response.ChargeResponse;
import com.n1colasgtz.model.response.ErrorResponse;
import com.n1colasgtz.model.response.PaymentLinkResponse;
import com.n1colasgtz.service.impl.AWSSecretsManagerStore;
import com.n1colasgtz.service.impl.JsonRequestParser;
import com.n1colasgtz.service.impl.PaymentProcessorFactory;
import com.stripe.StripeClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class PaymentLambda implements RequestHandler<Map<String, Object>, Map<String, Object>> {
    private static final Logger logger = LogManager.getLogger(PaymentLambda.class);
    private final JsonRequestParser requestParser;
    private final AWSSecretsManagerStore secretStore;
    private final PaymentProcessorFactory paymentProcessorFactory;

    public PaymentLambda() {
        ObjectMapper objectMapper = new ObjectMapper();
        this.requestParser = new JsonRequestParser(objectMapper);
        try {
            AWSSecretsManager client = AWSSecretsManagerClientBuilder.standard().build();
            this.secretStore = new AWSSecretsManagerStore(client);
            String apiKey = secretStore.getSecret("store1");
            logger.debug("Initialized StripeClient with API key (redacted)");
            this.paymentProcessorFactory = new PaymentProcessorFactory(new StripeClient(apiKey));
        } catch (Exception e) {
            logger.error("Failed to initialize dependencies: {}", e.getMessage(), e);
            throw new RuntimeException("Initialization failed", e);
        }
    }

    public PaymentLambda(JsonRequestParser requestParser, AWSSecretsManagerStore secretStore, PaymentProcessorFactory paymentProcessorFactory) {
        this.requestParser = requestParser;
        this.secretStore = secretStore;
        this.paymentProcessorFactory = paymentProcessorFactory;
    }

    @Override
    public Map<String, Object> handleRequest(Map<String, Object> input, Context context) {
        try {
            logger.info("Processing payment request: {}", input);
            PaymentRequest request = requestParser.parse(input);
            Object response = paymentProcessorFactory.processPayment(request);
            logger.info("Request processed successfully: {}", response);
            return toResponseMap(response);
        } catch (Exception e) {
            logger.error("Error processing payment: {}", e.getMessage(), e);
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .status("error")
                    .message("Internal error: " + e.getMessage())
                    .statusCode(500)
                    .build();
            return toResponseMap(errorResponse);
        }
    }

    private Map<String, Object> toResponseMap(Object response) {
        Map<String, Object> result = new HashMap<>();
        int statusCode = 500;
        if (response instanceof ChargeResponse) {
            statusCode = ((ChargeResponse) response).getStatusCode();
        } else if (response instanceof PaymentLinkResponse) {
            statusCode = ((PaymentLinkResponse) response).getStatusCode();
        } else if (response instanceof ErrorResponse) {
            statusCode = ((ErrorResponse) response).getStatusCode();
        } else {
            logger.warn("Unexpected response type: {}", response != null ? response.getClass().getName() : "null");
            result.put("body", ErrorResponse.builder()
                    .status("error")
                    .message("Unexpected response type")
                    .statusCode(500)
                    .build());
            result.put("statusCode", 500);
            return result;
        }
        result.put("statusCode", statusCode);
        result.put("body", response);
        return result;
    }
}