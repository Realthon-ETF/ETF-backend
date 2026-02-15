package com.realthon.etf.targetUrl.dto.response;

import com.realthon.etf.targetUrl.domain.TargetUrl;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TargetUrlResponse {

    private Long targetUrlId;
    private String targetUrl;

    public static TargetUrlResponse from(TargetUrl entity) {
        return TargetUrlResponse.builder()
                .targetUrlId(entity.getTargetUrlId())
                .targetUrl(entity.getTargetUrl())
                .build();
    }
}
