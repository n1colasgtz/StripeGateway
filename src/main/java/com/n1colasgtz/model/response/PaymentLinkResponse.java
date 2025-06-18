package com.n1colasgtz.model.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentLinkResponse {
    private String status;
    private String message;
    private String paymentLink;
    private int statusCode;
}