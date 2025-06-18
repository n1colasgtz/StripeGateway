package com.n1colasgtz.service;

import com.n1colasgtz.model.PaymentRequest;
import com.n1colasgtz.model.response.RefundResponse;

public interface RefundProcessor {
    RefundResponse processRefund(PaymentRequest request);
}