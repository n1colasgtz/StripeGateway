package com.n1colasgtz.service;

import com.n1colasgtz.model.PaymentRequest;
import com.n1colasgtz.model.response.PaymentStatusResponse;

public interface StatusProcessor {
    PaymentStatusResponse processStatus(PaymentRequest request);
}