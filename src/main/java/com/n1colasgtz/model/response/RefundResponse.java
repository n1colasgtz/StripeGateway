package com.n1colasgtz.model.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RefundResponse {
    private String status;
    private String message;
    private String refundId;
    private Long amount;
    private String currency;
    private int statusCode;
}