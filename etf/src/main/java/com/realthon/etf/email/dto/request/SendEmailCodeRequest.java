package com.realthon.etf.email.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SendEmailCodeRequest {

    @NotBlank
    private String username;

    @NotBlank
    private String phoneNumber;

    @NotBlank
    @Email
    private String email;
}