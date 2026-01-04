package com.realthon.etf.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class OpenAiClient {

    private final OpenAiProperties props;
    private final ObjectMapper objectMapper;
    private final RestTemplate openAiRestTemplate; // @Bean(name="openAiRestTemplate")로 주입받음

    /**
     * 공통 호출
     */
    private String callOpenAi(String systemPrompt, String userContent, double temperature) {
        // 1. 키 검증: accessToken 들어오는 사고 방지
        String apiKey = props.getApiKey();
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("openai.api-key is missing");
        }
        if (!apiKey.startsWith("sk-")) {
            // JWT가 들어오면 바로 잡아낼 수 있게
            log.error("openai.api-key is not an OpenAI key. prefix={}, length={}",
                    apiKey.substring(0, Math.min(3, apiKey.length())), apiKey.length());
            throw new IllegalStateException("openai.api-key is invalid (not sk-...)");
        }

        try {
            // 2. Responses API payload
            // 가장 단순하게 input을 문자열로 구성 (system+user), 필요하면 추후 response_format 등 추가 가능
            var root = objectMapper.createObjectNode();
            root.put("model", props.getModel());
            root.put("temperature", temperature);

            // Responses API는 input에 텍스트를 넣어도 됨
            // system/user 구분이 필요하면 "instructions"를 같이 쓰는 방식도 가능하지만,
            // 지금은 가장 안전한 형태로 합쳐서 전달함.
            String mergedInput = """
                [SYSTEM]
                %s

                [USER]
                %s
                """.formatted(systemPrompt, userContent);

            root.put("input", mergedInput);

            String body = objectMapper.writeValueAsString(root);

            // 3. Headers: 무조건 OpenAI key
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            HttpEntity<String> entity = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = openAiRestTemplate.exchange(
                    props.getBaseUrl(),
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            String respBody = response.getBody();
            if (respBody == null || respBody.isBlank()) {
                throw new RuntimeException("OpenAI response body is empty");
            }

            JsonNode json = objectMapper.readTree(respBody);

            // Responses API: output_text는 보통 문자열(또는 없을 수 있음)
            JsonNode outputText = json.get("output_text");
            if (outputText != null && !outputText.isNull() && outputText.isTextual()) {
                return outputText.asText();
            }

            // fallback: output -> content -> text
            JsonNode output = json.path("output");
            if (output.isArray()) {
                for (JsonNode item : output) {
                    JsonNode contentArr = item.path("content");
                    if (contentArr.isArray()) {
                        for (JsonNode c : contentArr) {
                            JsonNode text = c.path("text");
                            // 형식이 { "type":"output_text", "text":"..." } 인 경우도 있음
                            if (text.isTextual()) return text.asText();
                            JsonNode value = c.path("text").path("value");
                            if (value.isTextual()) return value.asText();
                        }
                    }
                }
            }

            throw new RuntimeException("OpenAI 응답에서 텍스트를 찾지 못함");

        } catch (HttpClientErrorException e) {
            // ✅ 401/400일 때 OpenAI가 준 바디를 반드시 보게 함
            log.error("OpenAI HTTP error status={}, body={}",
                    e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("OpenAI 호출 중 HTTP 오류", e);
        } catch (Exception e) {
            log.error("OpenAI 호출 중 예외", e);
            throw new RuntimeException("OpenAI 호출 중 오류", e);
        }
    }

    /**
     * 2단계 파이프라인: 이력서 → 분석(JSON 스타일) → 사람 말투 4줄 요약
     */
    public String summarizeResume(String plainText) {
        try {
            String truncated = (plainText != null && plainText.length() > 6000)
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
