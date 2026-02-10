package com.realthon.etf.ai.service;

import com.realthon.etf.ai.client.AiCrawlerClient;
import com.realthon.etf.ai.domain.AiRequest;
import com.realthon.etf.ai.dto.request.AiCrawlRequest;
import com.realthon.etf.ai.repository.AiRequestRepository;
import com.realthon.etf.global.exception.CustomException;
import com.realthon.etf.global.exception.ExceptionCode;
import com.realthon.etf.targetUrl.domain.TargetUrl;
import com.realthon.etf.targetUrl.repository.TargetUrlRepository;
import com.realthon.etf.user.domain.User;
import com.realthon.etf.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
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
    private final TargetUrlRepository targetUrlRepository;

    @Value("${ai.crawler.callback-auth-token:}")
    private String callbackAuthToken;

    @Value("${app.public-base-url:http://localhost:8080}")
    private String publicBaseUrl;

    /**
     * [동기] 사용자에게 저장된 targetUrl 리스트를 한 번에 AI로 전송
     * - requestId = 배치 단위
     * - AiRequest = 1건
     * - callback = 1번
     */
    @Transactional
    public String createRequestAndDispatchWithTargetUrls(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));

        // targetUrl 리스트 조회
        List<String> targetUrls = targetUrlRepository.findAllByUser_UserId(userId)
                .stream()
                .map(TargetUrl::getTargetUrl)
                .toList();

        if (targetUrls.isEmpty()) {
            throw new CustomException(ExceptionCode.TARGET_URL_NOT_FOUND);
        }

        // requestId 생성 (배치 단위)
        String requestId = UUID.randomUUID().toString();

        // AiRequest 저장 (대표 URL만 저장)
        AiRequest aiRequest = new AiRequest(
                requestId,
                user,
                targetUrls.get(0)
        );
        aiRequestRepository.save(aiRequest);

        // AI 요청 DTO 생성
        AiCrawlRequest request = buildAiRequest(user, requestId, targetUrls);

        // AI 서버 호출
        aiCrawlerClient.requestCrawl(request);

        return requestId;
    }

    /**
     * User → AI 요청 DTO 변환
     */
    private AiCrawlRequest buildAiRequest(User user, String requestId, List<String> targetUrls) {

        Set<String> interestFields = (user.getInterestFields() == null)
                ? null
                : user.getInterestFields().stream()
                .map(Enum::name)
                .collect(Collectors.toSet());

        String alarmTimeStr = (user.getAlarmTime() == null)
                ? null
                : user.getAlarmTime().format(DateTimeFormatter.ISO_LOCAL_TIME);

        AiCrawlRequest.UserProfile profile =
                AiCrawlRequest.UserProfile.builder()
                        .username(user.getUsername())
                        .phoneNumber(user.getPhoneNumber())
                        .school(user.getSchool())
                        .major(user.getMajor())
                        .interestFields(interestFields)
                        .intervalDays(user.getIntervalDays())
                        .alarmTime(alarmTimeStr)
                        .build();

        String summary = (user.getResumeSummary() != null)
                ? user.getResumeSummary().getSummary()
                : null;

        String callbackUrl = publicBaseUrl + "/ai/callback/" + requestId;

        AiCrawlRequest.CallbackDto callback =
                AiCrawlRequest.CallbackDto.builder()
                        .enabled(true)
                        .callbackUrl(callbackUrl)
                        .authToken(callbackAuthToken)
                        .build();

        return AiCrawlRequest.builder()
                .userId(String.valueOf(user.getUserId()))
                .targetUrls(targetUrls)
                .userProfile(profile)
                .summary(summary)
                .callback(callback)
                .build();
    }
}
