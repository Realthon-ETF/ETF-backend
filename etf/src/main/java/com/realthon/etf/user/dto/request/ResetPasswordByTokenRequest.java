package com.realthon.etf.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ResetPasswordByTokenRequest {

    @NotBlank
    private String resetToken;

    @NotBlank
    private String newPassword;
}