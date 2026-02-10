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
     *
     * 처리 원칙:
     * - requestId = 요청 단위 (AiRequest 1건)
     * - data[] = 결과 단위 (Notification N건)
     */
    @Transactional
    public void handleCallback(String requestId, AiCallbackRequest request) {

        AiRequest req = aiRequestRepository.findByRequestId(requestId)
                .orElseThrow(() -> new CustomException(ExceptionCode.AI_REQUEST_NOT_FOUND));

        // 멱등성 보장 (이미 처리된 요청이면 무시)
        if (req.getStatus() != AiRequestStatus.PENDING) {
            return;
        }

        // AI 전체 실패
        if (!"SUCCESS".equalsIgnoreCase(request.getStatus())) {
            req.markFailed(request.getStatus());
            return;
        }

        // SUCCESS지만 결과 없음 → 비정상
        if (request.getData() == null || request.getData().isEmpty()) {
            req.markFailed("AI 결과 데이터가 비어있음");
            return;
        }

        // 대표 결과 1건 선택 (첫 번째 결과)
        AiCallbackRequest.DataDto first = request.getData().get(0);

        // AiRequest 요청 메타 업데이트
        req.markSuccess(
                null,
                first.getCategory(),
                first.getTitle(),
                first.getSourceName(),
                first.getSummary(),
                first.getOriginalUrl(),
                first.getTimestamp()
        );

        // 결과 개수만큼 Notification 생성
        for (AiCallbackRequest.DataDto d : request.getData()) {

            Notification notification = Notification.builder()
                    .user(req.getUser())
                    .category(d.getCategory())
                    .title(
                            (d.getTitle() != null && !d.getTitle().isBlank())
                                    ? d.getTitle()
                                    : "추천 결과 도착함"
                    )
                    .sourceName(d.getSourceName())
                    .summary(d.getSummary())
                    .originalUrl(
                            (d.getOriginalUrl() != null && !d.getOriginalUrl().isBlank())
                                    ? d.getOriginalUrl()
                                    : req.getTargetUrl()
                    )
                    .createdAt(LocalDateTime.now())
                    .build();

            notificationRepository.save(notification);
        }
    }
}
