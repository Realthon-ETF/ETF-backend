package com.realthon.etf.ai.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.Instant;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AiCallbackRequest {

    private String status;
    private List<DataDto> data;

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
