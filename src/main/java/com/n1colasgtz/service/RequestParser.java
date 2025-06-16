package com.n1colasgtz.service;

import com.n1colasgtz.model.PaymentRequest;

import java.util.Map;

public interface RequestParser {
    PaymentRequest parse(Map<String, Object> input) throws Exception;
}