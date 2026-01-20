package com.realthon.etf.ai.service;

import com.realthon.etf.ai.domain.AiRequest;
import com.realthon.etf.ai.domain.AiRequestStatus;
import com.realthon.etf.ai.dto.request.AiCallbackRequest;
import com.realthon.etf.ai.repository.AiRequestRepository;
import com.realthon.etf.global.exception.CustomException;
import com.realthon.etf.global.exception.ExceptionCode;
import com.realthon.etf.notification.domain.Notification;
import com.realthon.etf.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AiCallbackService {

    private final AiRequestRepository aiRequestRepository;
    private final NotificationRepository notificationRepository;

    /**
     * AI 크롤러가 결과를 콜백으로 전달했을 때 호출되는 메서드
     *
     * 전체 흐름:
     * 1. requestId로 기존 AI 요청(AiRequest) 조회
     * 2. 이미 처리된 요청인지 검사(멱등성 보장)
     * 3. AI 응답 상태 확인(SUCCESS / FAILED)
     * 4. 성공 시 AiRequest 상태 업데이트 + Notification 생성
     */
    @Transactional
    public void handleCallback(String requestId, AiCallbackRequest request) {

        AiRequest req = aiRequestRepository.findByRequestId(requestId)
                .orElseThrow(() -> new CustomException(ExceptionCode.AI_REQUEST_NOT_FOUND));
        if (req.getStatus() != AiRequestStatus.PENDING) {
            throw new CustomException(ExceptionCode.AI_ALREADY_COMPLETED);
        }

        // AI 응답이 SUCCESS가 아닌 경우 → 실패로 처리하고 종료
        if (!"SUCCESS".equalsIgnoreCase(request.getStatus())) {
            req.markFailed(request.getStatus());
            return;
        }
        // SUCCESS인데 실제 결과 데이터가 없는 경우 → 비정상 응답으로 간주하여 실패 처리
        if (request.getData() == null) {
            req.markFailed("AI data가 비어있음");
            return;
        }

        // AI가 보내준 실제 분석 결과
        var d = request.getData();

        // AiRequest 엔티티를 SUCCESS 상태로 업데이트 → AI가 분석한 결과 메타데이터를 DB에 저장
        req.markSuccess(
                request.getRelevanceScore(),
                d.getCategory(),
                d.getTitle(),
                d.getSourceName(),
                d.getSummary(),
                d.getOriginalUrl(),
                d.getTimestamp()
        );

        // 사용자에게 보여줄 알림(Notification) 정보 구성
        String title = (d.getTitle() != null && !d.getTitle().isBlank())
                ? d.getTitle()
                : "추천 공고 도착함";

        String content = buildContent(request.getRelevanceScore(), d);

        String url = (d.getOriginalUrl() != null && !d.getOriginalUrl().isBlank())
                ? d.getOriginalUrl()
                : req.getTargetUrl();

        // Notification 엔티티 생성
        Notification notification = Notification.builder()
                .user(req.getUser())
                .category(d.getCategory())
                .title(d.getTitle())
                .sourceName(d.getSourceName())
                .summary(d.getSummary())
                .originalUrl(d.getOriginalUrl())
                .createdAt(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);
    }

    /**
     * Notification에 들어갈 알림 본문(content)을 생성하는 유틸 메서드
     *
     * AI 점수 및 요약 정보를 사람이 읽기 쉬운 문자열로 구성함
     */
    private String buildContent(Double relevanceScore, AiCallbackRequest.DataDto d) {

        // relevanceScore가 없는 경우를 대비한 기본 처리
        String score = (relevanceScore == null)
                ? "없음"
                : String.format("%.2f", relevanceScore);

        // 각 필드에 대해 null 방어 처리
        String category = (d.getCategory() == null) ? "미분류" : d.getCategory();
        String source = (d.getSourceName() == null) ? "출처 없음" : d.getSourceName();
        String summary = (d.getSummary() == null) ? "요약 없음" : d.getSummary();

        // 알림 메시지 포맷 구성
        return """
                [%s] %s
                relevanceScore=%s
                %s
                """.formatted(category, source, score, summary).trim();
    }
}
