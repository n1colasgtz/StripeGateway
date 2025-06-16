package com.n1colasgtz.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentRequest {
    private String storeId;
    private long amount;
    private String currency;
    private String paymentToken;
    private String description;
}