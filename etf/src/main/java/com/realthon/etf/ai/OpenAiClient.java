package com.realthon.etf.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class OpenAiClient {

    private static final String OPENAI_URL = "https://api.openai.com/v1/responses";

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${openai.api-key}")
    private String apiKey;

    @Value("${openai.model:gpt-4.1-mini}")
    private String model;

    // 공통 호출 함수
    private String callOpenAi(String systemPrompt, String userContent, double temperature) {
        try {
            var root = objectMapper.createObjectNode();
            root.put("model", model);
            root.put("temperature", temperature);

            var input = objectMapper.createArrayNode();
            input.add(objectMapper.createObjectNode()
                    .put("role", "system")
                    .put("content", systemPrompt));
            input.add(objectMapper.createObjectNode()
                    .put("role", "user")
                    .put("content", userContent));
            root.set("input", input);

            String body = objectMapper.writeValueAsString(root);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            HttpEntity<String> entity = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    OPENAI_URL,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            if (!response.getStatusCode().is2xxSuccessful()) {
                log.error("OpenAI 호출 실패 status={}, body={}",
                        response.getStatusCode(), response.getBody());
                throw new RuntimeException("OpenAI 호출 실패");
            }

            JsonNode json = objectMapper.readTree(response.getBody());

            // output_text가 있으면 그거 사용
            JsonNode outputText = json.path("output_text");
            if (outputText.isArray() && outputText.size() > 0) {
                return outputText.get(0).asText();
            }

            // 혹시 몰라서 fallback
            JsonNode output = json.path("output");
            if (output.isArray() && output.size() > 0) {
                JsonNode content = output.get(0).path("content");
                if (content.isArray() && content.size() > 0) {
                    JsonNode textNode = content.get(0).path("text").path("value");
                    if (!textNode.isMissingNode()) {
                        return textNode.asText();
                    }
                }
            }

            throw new RuntimeException("OpenAI 응답에서 텍스트를 찾지 못함");

        } catch (Exception e) {
            log.error("OpenAI 호출 중 예외", e);
            throw new RuntimeException("OpenAI 호출 중 오류", e);
        }
    }

    // 2단계 파이프라인: 이력서 → 분석(JSON 스타일) → 사람 말투 4줄 요약
    public String summarizeResume(String plainText) {
        try {
            String truncated = plainText.length() > 6000
                    ? plainText.substring(0, 6000)
                    : plainText;

            // 1단계: 구조화된 분석
            String stage1System = """
                너는 전문 채용 담당자이자 커리어 분석 전문가야.
                주어진 이력서를 정확하게 분석해서, 아래 형식의 JSON 스타일 텍스트로 정리해라.

                {
                  "summary": "지원자의 전체적인 배경과 핵심 요약",
                  "strengths": ["강점1", "강점2", ...],
                  "weaknesses": ["약점1", "약점2", ...],
                  "improvements": ["노력하면 좋은 점1", "노력하면 좋은 점2", ...]
                }

                규칙:
                - 가능한 한 사실 기반으로만 작성해라.
                - 추측은 최소화하고, 이력서에 드러난 내용 위주로 정리해라.
                - JSON 이외의 설명 문장은 넣지 마라.
                """;

            String stage1User = "아래는 한 지원자의 이력서 전문이야:\n\n" + truncated;

            String analysis = callOpenAi(stage1System, stage1User, 0.1);

            // 2단계: 사람에게 보여줄 4줄 요약으로 변환
            String stage2System = """
                너는 채용 담당자야.
                아래에 이력서를 분석한 JSON 스타일 텍스트가 주어질 거야.
                이 내용을 바탕으로 사람에게 말하듯 자연스럽게 4줄로만 요약해라.

                출력 형식은 반드시 아래 네 줄 형식을 따라야 한다:

                요약: ~~
                강점: ~~
                약점: ~~
                노력할 점: ~~

                규칙:
                - JSON, 대괄호, 따옴표 등은 절대 출력하지 마라.
                - 각 줄은 한 문장으로만 작성해라.
                - "~에요", "~한 편이에요", "~하면 좋을 것 같아요" 같은 부드러운 말투를 사용해라.
                - 키 이름은 반드시 '요약:', '강점:', '약점:', '노력할 점:' 으로 시작해야 한다.
                - 네 줄만 출력하고, 그 외의 문장은 추가하지 마라.
                """;

            String stage2User = "이력서 분석 결과:\n" + analysis;

            String finalText = callOpenAi(stage2System, stage2User, 0.3);

            return finalText.trim();
        } catch (Exception e) {
            log.error("이력서 요약 파이프라인 오류", e);
            throw new RuntimeException("이력서 요약 중 오류", e);
        }
    }
}
