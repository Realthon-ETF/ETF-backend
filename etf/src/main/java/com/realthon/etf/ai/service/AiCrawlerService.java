package com.realthon.etf.ai.service;

import com.realthon.etf.ai.client.AiCrawlerClient;
import com.realthon.etf.ai.domain.AiRequest;
import com.realthon.etf.ai.dto.request.AiCrawlRequest;
import com.realthon.etf.ai.repository.AiRequestRepository;
import com.realthon.etf.global.exception.CustomException;
import com.realthon.etf.global.exception.ExceptionCode;
import com.realthon.etf.user.domain.User;
import com.realthon.etf.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiCrawlerService {

    private final UserRepository userRepository;
    private final AiRequestRepository aiRequestRepository;
    private final AiCrawlerClient aiCrawlerClient;

    @Value("${ai.crawler.callback-auth-token:}")
    private String callbackAuthToken;

    @Value("${app.public-base-url:http://localhost:8080}")
    private String publicBaseUrl;

    /**
     * [동기] 요청 생성 + 저장(PENDING) + AI 호출
     */
    @Transactional
    public String createRequestAndDispatch(Long userId, String targetUrl) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));

        String requestId = UUID.randomUUID().toString();
        AiRequest saved = aiRequestRepository.save(new AiRequest(requestId, user, targetUrl));
        AiCrawlRequest aiReq = buildAiRequest(user, requestId, targetUrl);

        aiCrawlerClient.requestCrawl(aiReq);

        return saved.getRequestId();
    }

    /**
     * User -> AI 전송 DTO로 변환함 (+ callbackUrl 생성)
     */
    private AiCrawlRequest buildAiRequest(User user, String requestId, String targetUrl) {

        // interestFields enum -> string set
        Set<String> interestFields = (user.getInterestFields() == null) ? null
                : user.getInterestFields().stream()
                .map(Enum::name)
                .collect(Collectors.toSet());

        String alarmTimeStr = (user.getAlarmTime() == null) ? null
                : user.getAlarmTime().format(DateTimeFormatter.ISO_LOCAL_TIME);

        AiCrawlRequest.UserProfile profile = new AiCrawlRequest.UserProfile(
                user.getUsername(),
                user.getPhoneNumber(),
                user.getSchool(),
                user.getMajor(),
                interestFields,
                user.getIntervalDays(),
                alarmTimeStr
        );

        // resume summary
        String summary = (user.getResumeSummary() != null)
                ? user.getResumeSummary().getSummary()
                : null;

        String callbackUrl = (publicBaseUrl + "/ai/callback/" + requestId).trim();

        AiCrawlRequest.CallbackDto callback = new AiCrawlRequest.CallbackDto(
                true,
                callbackUrl,
                callbackAuthToken
        );

        return AiCrawlRequest.builder()
                .userId(String.valueOf(user.getUserId()))
                .targetUrl(targetUrl)
                .userProfile(profile)
                .summary(summary)
                .callback(callback)
                .build();
    }

    /**
     * [비동기] AI 호출 (스케줄링/배치에서 쓰기 좋음)
     * - 실패해도 메인 로직 롤백 없이, ai_requests에 FAILED만 찍고 끝냄
     */
    @Async("aiExecutor")
    @Transactional(noRollbackFor = Exception.class)
    public void dispatchToAiAsync(Long userId, String targetUrl) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));

        String requestId = UUID.randomUUID().toString();
        aiRequestRepository.save(new AiRequest(requestId, user, targetUrl));

        AiCrawlRequest request = buildAiRequest(user, requestId, targetUrl);

        try {
            aiCrawlerClient.requestCrawl(request);
        } catch (Exception e) {
            log.error("[AI_CRAWLER] dispatch failed. requestId={}, userId={}", requestId, userId, e);

            AiRequest req = aiRequestRepository.findByRequestId(requestId).orElse(null);
            if (req != null) req.markFailed("AI 크롤러 호출 실패함");
        }
    }
}
