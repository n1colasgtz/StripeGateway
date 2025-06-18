package com.n1colasgtz.model.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentStatusResponse {
    private String status;
    private String message;
    private String paymentId; // chargeId or sessionId
    private String paymentStatus;
    private Long amount;
    private String currency;
    private int statusCode;
}