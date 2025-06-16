package com.n1colasgtz.impl;

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
        String body = (String) input.get("body");
        if (body == null) {
            throw new IllegalArgumentException("Request body is missing");
        }
        return objectMapper.readValue(body, PaymentRequest.class);
    }
}