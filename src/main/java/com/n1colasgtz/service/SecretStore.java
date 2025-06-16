package com.n1colasgtz.service;

public interface SecretStore {
    String getSecret(String storeId) throws Exception;
}