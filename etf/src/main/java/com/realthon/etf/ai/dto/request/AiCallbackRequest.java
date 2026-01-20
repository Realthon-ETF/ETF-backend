package com.realthon.etf.ai.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.Instant;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AiCallbackRequest {

    private String status;
    private Double relevanceScore;
    private DataDto data;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DataDto {
        private String category;
        private String title;
        private String sourceName;
        private String summary;
        private String originalUrl;
        private Instant timestamp;
    }
}
