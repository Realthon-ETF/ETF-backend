package com.realthon.etf.ai.client;

import com.realthon.etf.ai.dto.request.AiCrawlRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class AiCrawlerClient {

    private final RestClient restClient = RestClient.create();
    private final GoogleIdTokenProvider googleIdTokenProvider;

    @Value("${ai.crawler.url}")
    private String baseUrl;

    public void requestCrawl(AiCrawlRequest request) {
        String url = baseUrl + "/crawl/request";
        log.info("[AI CALL] url={}", url);

        String idToken = googleIdTokenProvider.getIdToken(baseUrl);

        try {
            restClient.post()
                    .uri(url)
                    .header("Authorization", "Bearer " + idToken)
                    .body(request)
                    .retrieve()
                    .toBodilessEntity();
            log.info("[AI CALL] success");
        } catch (HttpClientErrorException e) {
            log.error("[AI CALL] status={} body={}", e.getStatusCode(), e.getResponseBodyAsString());
            throw e;
        }
    }

}
