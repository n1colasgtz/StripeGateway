package com.n1colasgtz.service;

import com.n1colasgtz.model.PaymentRequest;
import com.n1colasgtz.model.response.WebhookResponse;

public interface WebhookProcessor {
    WebhookResponse processWebhook(PaymentRequest request);
}