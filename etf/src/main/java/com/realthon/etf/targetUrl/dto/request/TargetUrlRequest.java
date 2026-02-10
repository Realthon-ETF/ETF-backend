package com.realthon.etf.targetUrl.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import org.hibernate.validator.constraints.URL;

@Getter
public class TargetUrlRequest {

    @NotBlank(message = "targetUrl은 필수임")
    @URL(message = "올바른 URL 형식이 아님")
    private String targetUrl;
}
