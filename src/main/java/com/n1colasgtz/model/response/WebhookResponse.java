package com.n1colasgtz.model.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WebhookResponse {
    private String status;
    private String message;
    private String eventId;
    private int statusCode;
}