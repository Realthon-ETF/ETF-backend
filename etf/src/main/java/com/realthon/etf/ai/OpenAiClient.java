package com.realthon.etf.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class OpenAiClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${openai.api-key}")
    private String apiKey;

    @Value("${openai.model:gpt-4.1-mini}")
    private String model;

    private static final String OPENAI_CHAT_URL = "https://api.openai.com/v1/chat/completions";

    public String summarizeResume(String plainText) {
        try {
            // 너무 긴 PDF면 앞부분만 잘라서 보내기 (토큰 절약)
            String truncated = plainText.length() > 6000
                    ? plainText.substring(0, 6000)
                    : plainText;

            String systemPrompt = """
                너는 채용 담당자를 위한 이력서 요약 도우미야.
                아래 이력서 내용을 읽고 지원자의 경력, 기술 스택, 강점, 관심 분야를
                5~8문장 정도 한국어로 간결하게 요약해줘.
                불필요한 수식어는 줄이고 핵심 정보 위주로 작성해.
                """;

            String requestJson = """
                {
                  "model": "%s",
                  "messages": [
                    { "role": "system", "content": %s },
                    { "role": "user", "content": %s }
                  ],
                  "temperature": 0.3
                }
                """.formatted(
                    model,
                    objectMapper.writeValueAsString(systemPrompt),
                    objectMapper.writeValueAsString("이력서 내용:\n\n" + truncated)
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    OPENAI_CHAT_URL,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("OpenAI 호출 실패: " + response.getStatusCode());
            }

            JsonNode root = objectMapper.readTree(response.getBody());
            return root.path("choices").get(0)
                    .path("message")
                    .path("content")
                    .asText();

        } catch (Exception e) {
            throw new RuntimeException("이력서 요약 중 오류", e);
        }
    }
}
