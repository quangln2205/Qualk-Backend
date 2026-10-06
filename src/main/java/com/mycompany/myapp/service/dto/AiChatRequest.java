package com.mycompany.myapp.service.dto;

import jakarta.validation.constraints.NotBlank;

/** A question sent to the data-aware Qualk assistant. */
public class AiChatRequest {

    @NotBlank
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
