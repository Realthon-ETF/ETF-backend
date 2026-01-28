package com.realthon.etf.ai.controller;

import com.realthon.etf.ai.dto.request.AiCallbackRequest;
import com.realthon.etf.ai.service.AiCallbackService;
import com.realthon.etf.ai.service.AiCrawlerService;
import com.realthon.etf.auth.dto.CustomUserDetails;
import com.realthon.etf.global.exception.CustomException;
import com.realthon.etf.global.exception.ExceptionCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class AiCrawlerController {

    private final AiCrawlerService aiCrawlerService;
    private final AiCallbackService aiCallbackService;

    @Value("${ai.crawler.callback-auth-token:}")
    private String callbackAuthToken;

    /*
     * [프론트 -> 백엔드] "AI 크롤링 요청 트리거"
     *
     * 엔드포인트 목적:
     * - 사용자가 특정 공고/URL을 보고 "이거 AI로 분석/추천 받아줘"를 누르면 호출됨
     *
     * 처리 흐름:
     * 1) JWT 인증을 통해 현재 로그인한 사용자(userDetails)를 얻음
     * 2) request body에서 targetUrl만 받아서
     * 3) aiCrawlerService.createRequestAndDispatch(userId, targetUrl)로 위임
     */
    @PostMapping({"/crawl/request"})
    public ResponseEntity<?> requestCrawl(@RequestBody CrawlTriggerRequest request,
                                          @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId();
        return ResponseEntity.ok(aiCrawlerService.createRequestAndDispatch(userId, request.getTargetUrl()));
    }

    /*
     * [프론트 -> 백엔드] 트리거 요청 DTO
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CrawlTriggerRequest {
        private String targetUrl;
    }

    /*
     * [AI -> 백엔드] 크롤링 결과 콜백
     *
     * 엔드포인트 목적:
     * - AI 크롤러 서버가 작업을 끝내면 백엔드로 결과를 보내 DB(ai_requests, notification 등)를 갱신하게 함
     *
     * 처리 흐름:
     * 1) (선택) X-AI-CALLBACK-TOKEN 헤더로 콜백 인증
     *    - callbackAuthToken 설정이 있으면 반드시 일치해야 함
     *    - callbackAuthToken이 비어있으면 로컬 테스트로 보고 인증 스킵
     * 2) requestId는 path variable로 받음 (콜백 body에 requestId가 없어서 URL에 넣는 설계)
     * 3) body(AiCallbackRequest)를 service로 위임해서
     *    - ai_request 상태 SUCCESS/FAILED 업데이트
     *    - notification 생성 등 후처리
     */
    @PostMapping("/callback/{requestId}")
    public ResponseEntity<Void> callback(@RequestHeader(value = "X-AI-CALLBACK-TOKEN", required = false) String token,
                                         @PathVariable String requestId,
                                         @RequestBody AiCallbackRequest request) {

        // callbackAuthToken이 설정되어 있는 경우에만 토큰 검증 수행
        if (callbackAuthToken != null && !callbackAuthToken.isBlank()) {
            // 토큰이 없거나, 일치하지 않으면 차단
            if (token == null || !callbackAuthToken.equals(token)) {
                throw new CustomException(ExceptionCode.AI_CALLBACK_UNAUTHORIZED);
            }
        }

        // 콜백 처리 로직은 service에 위임 (멱등성 처리, 상태 업데이트, notification 생성 등)
        aiCallbackService.handleCallback(requestId, request);

        // 성공적으로 처리되면 200 OK (body 없음)
        return ResponseEntity.ok().build();
    }
}
