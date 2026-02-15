package com.realthon.etf.ai.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AiCrawlAcceptedResponse {
    private String requestId;
    private String message;
}
