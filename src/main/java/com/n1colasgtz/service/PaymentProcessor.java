package com.n1colasgtz.service;

import com.n1colasgtz.model.PaymentRequest;
import com.n1colasgtz.model.PaymentResponse;

public interface PaymentProcessor {
    PaymentResponse processPayment(PaymentRequest request, String apiKey);
}