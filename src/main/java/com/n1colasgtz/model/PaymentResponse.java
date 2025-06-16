package com.n1colasgtz.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentResponse {
    private String status;
    private String message;
    private String chargeId;
    private Long amount;
    private String currency;
    private int statusCode;
}