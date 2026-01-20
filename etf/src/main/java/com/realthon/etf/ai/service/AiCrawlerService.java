package com.realthon.etf.ai.service;

import com.realthon.etf.ai.client.AiCrawlerClient;
import com.realthon.etf.ai.domain.AiRequest;
import com.realthon.etf.ai.dto.request.AiCrawlRequest;
import com.realthon.etf.ai.dto.response.AiCrawlAcceptedResponse;
import com.realthon.etf.ai.repository.AiRequestRepository;
import com.realthon.etf.global.exception.CustomException;
import com.realthon.etf.global.exception.ExceptionCode;
import com.realthon.etf.user.domain.User;
import com.realthon.etf.user.dto.response.UserResponse;
import com.realthon.etf.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AiCrawlerService {

    private final UserRepository userRepository;
    private final AiRequestRepository aiRequestRepository;
    private final AiCrawlerClient aiCrawlerClient; // 실제 AI 크롤러 서버로 HTTP 요청을 보내는 클라이언트

    @Value("${ai.crawler.callback-auth-token:}")
    private String callbackAuthToken;

    @Value("${app.public-base-url:http://localhost:8080}")
    private String publicBaseUrl;

    @Value("${ai.crawler.url:}")
    private String callbackUrl;

    /**
     * [동기 트리거 메서드]
     * 프론트엔드 → 백엔드
     *
     * 역할:
     * 1. 사용자 검증
     * 2. AI 요청 엔티티(AiRequest) 생성 및 저장
     * 3. AI로 보낼 요청 DTO 구성
     * 4. AI 호출
     * 5. 즉시 응답 반환 (ACCEPTED)
     */
    @Transactional
    public AiCrawlAcceptedResponse createRequestAndDispatch(Long userId, String targetUrl) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));

        String requestId = UUID.randomUUID().toString();

        UserResponse profile = UserResponse.from(user);

        // AI가 결과를 다시 보내줄 콜백 정보(현재 callbackUrl은 설정값 그대로 사용 중)
        AiCrawlRequest.CallbackDto callback =
                new AiCrawlRequest.CallbackDto(true, callbackUrl, callbackAuthToken);

        // AI 크롤러에게 전달할 최종 요청 DTO 구성
        AiCrawlRequest aiReq = new AiCrawlRequest(
                user.getUserId(),
                requestId,
                targetUrl,
                profile,
                callback
        );

        // DB에 AI 요청 상태 저장 (PENDING)
        AiRequest saved = aiRequestRepository.save(
                new AiRequest(requestId, user, targetUrl)
        );

        // AI 크롤러 서버 호출 (동기)
        // ※ 실패하면 예외 발생 → 컨트롤러에서 502 등으로 처리 가능
        aiCrawlerClient.requestCrawl(aiReq);

        // 프론트엔드에는 "요청 접수됨"만 즉시 응답
        return new AiCrawlAcceptedResponse(
                saved.getRequestId(),
                saved.getStatus().name()
        );
    }

    /**
     * [비동기 AI 호출 메서드]
     *
     * 목적:
     * - 프론트 응답과 AI 호출을 분리하고 싶을 때 사용
     * - AI 장애가 있어도 메인 트랜잭션에 영향 최소화
     *
     * 특징:
     * - @Async → 별도 스레드
     * - noRollbackFor → 예외 발생해도 트랜잭션 롤백 안 함
     */
    @Async("aiExecutor")
    @Transactional(noRollbackFor = Exception.class)
    public void dispatchToAiAsync(
            String requestId,
            Long userId,
            String targetUrl,
            UserResponse profile
    ) {

        // AI 콜백 URL 생성
        //    → 콜백 바디에 requestId가 없으므로 URL path에 포함
        String callbackUrl = publicBaseUrl + "/ai/callback/" + requestId;

        // AI로 보낼 요청 DTO 구성
        AiCrawlRequest request = new AiCrawlRequest(
                userId,
                requestId,
                targetUrl,
                profile,
                new AiCrawlRequest.CallbackDto(true, callbackUrl, callbackAuthToken)
        );

        try {
            aiCrawlerClient.requestCrawl(request);

        } catch (Exception e) {
            // AI 호출 자체가 실패한 경우 → DB의 ai_requests 상태를 FAILED로 기록
            AiRequest req = aiRequestRepository.findByRequestId(requestId)
                    .orElse(null);

            if (req != null) {
                req.markFailed("AI 크롤러 호출 실패함");
            }
        }
    }
}
