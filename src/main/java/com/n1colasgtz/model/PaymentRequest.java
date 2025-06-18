package com.n1colasgtz.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    private String requestType; // CHARGE or PAYMENT_LINK
    private String successUrl; // For PAYMENT_LINK
    private String cancelUrl;  // For PAYMENT_LINK

    // Default constructor for Jackson
    public PaymentRequest() {}

    // Constructor for Jackson deserialization
    @JsonCreator
    public PaymentRequest(
            @JsonProperty("storeId") String storeId,
            @JsonProperty("amount") long amount,
            @JsonProperty("currency") String currency,
            @JsonProperty("paymentToken") String paymentToken,
            @JsonProperty("description") String description,
            @JsonProperty("requestType") String requestType,
            @JsonProperty("successUrl") String successUrl,
            @JsonProperty("cancelUrl") String cancelUrl) {
        this.storeId = storeId;
        this.amount = amount;
        this.currency = currency;
        this.paymentToken = paymentToken;
        this.description = description;
        this.requestType = requestType;
        this.successUrl = successUrl;
        this.cancelUrl = cancelUrl;
    }
}