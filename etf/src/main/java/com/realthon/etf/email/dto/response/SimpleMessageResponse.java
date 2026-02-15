package com.realthon.etf.email.dto.response;

import lombok.Getter;

@Getter
public class SimpleMessageResponse {
    private final String message;

    public SimpleMessageResponse(String message) {
        this.message = message;
    }
}