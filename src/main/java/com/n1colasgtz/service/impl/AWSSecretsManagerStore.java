package com.n1colasgtz.service.impl;

import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.n1colasgtz.service.SecretStore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AWSSecretsManagerStore implements SecretStore {
    private static final Logger logger = LogManager.getLogger(AWSSecretsManagerStore.class);
    private final AWSSecretsManager secretsManager;

    public AWSSecretsManagerStore(AWSSecretsManager secretsManager) {
        this.secretsManager = secretsManager;
    }

    @Override
    public String getSecret(String storeId) throws Exception {
        logger.debug("Retrieving secret for store: {}", storeId);
        String secretName = "stripe/secret/" + storeId;
        GetSecretValueRequest request = new GetSecretValueRequest().withSecretId(secretName);
        return secretsManager.getSecretValue(request).getSecretString();
    }
}