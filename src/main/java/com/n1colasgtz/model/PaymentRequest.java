package com.n1colasgtz.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class PaymentRequest {
    private String storeId;
    private Long amount; // Nullable for STATUS, WEBHOOK
    private String currency; // Nullable for STATUS, WEBHOOK
    private String paymentToken; // For CHARGE
    private String description; // For CHARGE, PAYMENT_LINK
    private String requestType; // CHARGE, PAYMENT_LINK, REFUND, STATUS, WEBHOOK
    private String successUrl; // For PAYMENT_LINK
    private String cancelUrl; // For PAYMENT_LINK
    private String chargeId; // For REFUND, STATUS
    private String sessionId; // For STATUS
    private Map<String, Object> webhookEvent; // For WEBHOOK

    public PaymentRequest() {}

    @JsonCreator
    public PaymentRequest(
            @JsonProperty("storeId") String storeId,
            @JsonProperty("amount") Long amount,
            @JsonProperty("currency") String currency,
            @JsonProperty("paymentToken") String paymentToken,
            @JsonProperty("description") String description,
            @JsonProperty("requestType") String requestType,
            @JsonProperty("successUrl") String successUrl,
            @JsonProperty("cancelUrl") String cancelUrl,
            @JsonProperty("chargeId") String chargeId,
            @JsonProperty("sessionId") String sessionId,
            @JsonProperty("webhookEvent") Map<String, Object> webhookEvent) {
        this.storeId = storeId;
        this.amount = amount;
        this.currency = currency;
        this.paymentToken = paymentToken;
        this.description = description;
        this.requestType = requestType;
        this.successUrl = successUrl;
        this.cancelUrl = cancelUrl;
        this.chargeId = chargeId;
        this.sessionId = sessionId;
        this.webhookEvent = webhookEvent;
    }
}