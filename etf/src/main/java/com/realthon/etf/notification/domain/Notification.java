package com.realthon.etf.notification.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter
@NoArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 알림 제목
    @Column(nullable = false, length = 200)
    private String title;

    // 알림 내용(요약 or 본문)
    @Column(columnDefinition = "TEXT")
    private String content;

    // 해당 공지(원문) URL
    @Column(nullable = false, length = 500)
    private String noticeUrl;

    // 알림 생성 시각
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public Notification(String title, String content, String noticeUrl, LocalDateTime createdAt) {
        this.title = title;
        this.content = content;
        this.noticeUrl = noticeUrl;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }
}
