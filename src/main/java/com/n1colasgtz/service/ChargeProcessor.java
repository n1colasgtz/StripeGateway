package com.n1colasgtz.service;

import com.n1colasgtz.model.PaymentRequest;
import com.n1colasgtz.model.response.ChargeResponse;

public interface ChargeProcessor {
    ChargeResponse processCharge(PaymentRequest request);
}