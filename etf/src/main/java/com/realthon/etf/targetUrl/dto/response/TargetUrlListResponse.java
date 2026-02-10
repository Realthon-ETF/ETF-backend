package com.realthon.etf.targetUrl.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class TargetUrlListResponse {

    private long totalCount;
    private List<TargetUrlResponse> targetUrls;

    public static TargetUrlListResponse of(long totalCount, List<TargetUrlResponse> targetUrls) {
        return TargetUrlListResponse.builder()
                .totalCount(totalCount)
                .targetUrls(targetUrls)
                .build();
    }
}
