package com.n1colasgtz.service;

import com.n1colasgtz.model.PaymentRequest;
import com.n1colasgtz.model.response.PaymentLinkResponse;

public interface PaymentLinkProcessor {
    PaymentLinkResponse processPaymentLink(PaymentRequest request);
}