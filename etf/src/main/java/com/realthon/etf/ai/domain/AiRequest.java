package com.realthon.etf.ai.domain;


import com.realthon.etf.user.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "ai_requests")
public class AiRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ai_request_id")
    private Long aiRequestId;

    @Column(name = "request_id", nullable = false, unique = true, length = 60)
    private String requestId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "target_url", nullable = false, length = 2048)
    private String targetUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AiRequestStatus status;

    @Column(name = "requested_at", nullable = false)
    private LocalDateTime requestedAt;

    @Column(name = "responded_at")
    private LocalDateTime respondedAt;

    @Column(name = "fail_reason", length = 1000)
    private String failReason;

    // AI 결과 메타
    @Column(name = "relevance_score")
    private Double relevanceScore;

    @Column(name = "result_category", length = 50)
    private String resultCategory;

    @Column(name = "result_title", length = 300)
    private String resultTitle;

    @Column(name = "result_source_name", length = 100)
    private String resultSourceName;

    @Column(name = "result_summary", length = 2000)
    private String resultSummary;

    @Column(name = "result_original_url", length = 2048)
    private String resultOriginalUrl;

    @Column(name = "result_timestamp")
    private Instant resultTimestamp;

    public AiRequest(String requestId, User user, String targetUrl) {
        this.requestId = requestId;
        this.user = user;
        this.targetUrl = targetUrl;
        this.status = AiRequestStatus.PENDING;
        this.requestedAt = LocalDateTime.now();
    }

    public void markFailed(String reason) {
        this.status = AiRequestStatus.FAILED;
        this.respondedAt = LocalDateTime.now();
        this.failReason = (reason == null || reason.isBlank()) ? "AI 처리 실패함" : reason;
    }

    public void markSuccess(Double relevanceScore,
                            String category,
                            String title,
                            String sourceName,
                            String summary,
                            String originalUrl,
                            Instant timestamp) {
        this.status = AiRequestStatus.SUCCESS;
        this.respondedAt = LocalDateTime.now();
        this.failReason = null;

        this.relevanceScore = relevanceScore;
        this.resultCategory = category;
        this.resultTitle = title;
        this.resultSourceName = sourceName;
        this.resultSummary = summary;
        this.resultOriginalUrl = originalUrl;
        this.resultTimestamp = timestamp;
    }
}
