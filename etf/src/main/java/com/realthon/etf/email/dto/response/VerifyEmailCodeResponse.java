package com.realthon.etf.email.dto.response;

import lombok.Getter;

@Getter
public class VerifyEmailCodeResponse {

    private final String resetToken;
    private final long expiresInSec;

    public VerifyEmailCodeResponse(String resetToken, long expiresInSec) {
        this.resetToken = resetToken;
        this.expiresInSec = expiresInSec;
    }
}