package com.realthon.etf.ai.client;

import com.realthon.etf.ai.dto.request.AiCrawlRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class AiCrawlerClient {

    private final RestClient restClient = RestClient.create();
    private final GoogleIdTokenProvider googleIdTokenProvider;

    @Value("${ai.crawler.url}")
    private String baseUrl;

    public void requestCrawl(AiCrawlRequest request) {
        String idToken = googleIdTokenProvider.getIdToken(baseUrl);

        restClient.post()
                .uri(baseUrl + "/crawl/request")
                .header("Authorization", "Bearer " + idToken)
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }
}
