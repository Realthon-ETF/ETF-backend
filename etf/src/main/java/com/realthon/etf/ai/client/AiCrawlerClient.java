package com.realthon.etf.ai.client;

import com.realthon.etf.ai.dto.request.AiCrawlRequest;
import com.realthon.etf.global.exception.CustomException;
import com.realthon.etf.global.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class AiCrawlerClient {

    private final RestClient aiRestClient;

    @Value("${ai.crawler.crawl-path:/crawl/request}")
    private String crawlPath;

    public void requestCrawl(AiCrawlRequest request) {
        try {
            System.out.println("[AI_CRAWLER] POST baseUrl + path = " + crawlPath);
            aiRestClient.post()
                    .uri(crawlPath)
                    .body(request)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException(ExceptionCode.AI_CRAWLER_CALL_FAILED);
        }
    }
}
