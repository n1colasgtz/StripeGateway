package com.n1colasgtz.service.impl;

import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.amazonaws.services.secretsmanager.model.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.n1colasgtz.service.SecretStore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

public class AWSSecretsManagerStore implements SecretStore {
    private static final Logger logger = LogManager.getLogger(AWSSecretsManagerStore.class);
    private final AWSSecretsManager secretsManager;
    private final ObjectMapper objectMapper;

    public AWSSecretsManagerStore(AWSSecretsManager secretsManager) {
        this.secretsManager = secretsManager;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public String getSecret(String storeId) throws Exception {
        if (storeId == null || storeId.trim().isEmpty()) {
            throw new IllegalArgumentException("Store ID cannot be empty");
        }
        logger.debug("Retrieving secret for store: {}", storeId);
        String secretName = storeId; // Use storeId directly as the secret name
        GetSecretValueRequest request = new GetSecretValueRequest().withSecretId(secretName);
        try {
            String secret = secretsManager.getSecretValue(request).getSecretString();
            if (secret == null || secret.trim().isEmpty()) {
                throw new IllegalStateException("Secret is empty for store: " + storeId);
            }
            // Try parsing as JSON to extract stripeSecretKey if present
            try {
                Map<String, String> secretMap = objectMapper.readValue(secret, Map.class);
                String apiKey = secretMap.get("stripeSecretKey");
                if (apiKey != null && !apiKey.trim().isEmpty()) {
                    return apiKey;
                }
            } catch (Exception e) {
                // Not a JSON object; assume plain text
                logger.debug("Secret is not a JSON object, using as plain text");
            }
            return secret; // Use plain text if JSON parsing fails or no stripeSecretKey
        } catch (ResourceNotFoundException e) {
            logger.error("Secret not found for store: {}", storeId, e);
            throw new IllegalStateException("Secret not found for store: " + storeId, e);
        } catch (Exception e) {
            logger.error("Error retrieving secret for store: {}", storeId, e);
            throw e;
        }
    }
}