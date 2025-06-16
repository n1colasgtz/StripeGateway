package com.n1colasgtz.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.n1colasgtz.model.PaymentRequest;
import com.n1colasgtz.service.RequestParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

public class JsonRequestParser implements RequestParser {
    private static final Logger logger = LogManager.getLogger(JsonRequestParser.class);
    private final ObjectMapper objectMapper;

    public JsonRequestParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public PaymentRequest parse(Map<String, Object> input) throws Exception {
        logger.debug("Parsing input: {}", input);
        Object body = input.get("body");
        if (body == null) {
            throw new IllegalArgumentException("Request body is missing");
        }
        if (body instanceof String) {
            // Handle body as a JSON string
            return objectMapper.readValue((String) body, PaymentRequest.class);
        } else if (body instanceof Map) {
            // Handle body as a JSON object (Map)
            return objectMapper.convertValue(body, PaymentRequest.class);
        } else {
            throw new IllegalArgumentException("Body must be a JSON string or object");
        }
    }
}