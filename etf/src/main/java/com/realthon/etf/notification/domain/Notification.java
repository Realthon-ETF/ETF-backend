package com.realthon.etf.notification.domain;

import com.realthon.etf.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "notifications")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long notificationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 50)
    private String category;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(name = "source_name", nullable = false, length = 100)
    private String sourceName;

    @Column(nullable = false, length = 1000)
    private String summary;

    @Column(name = "original_url", nullable = false, length = 500)
    private String originalUrl;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "is_liked")
    private boolean isLiked = false;

    // relevanceScore 저장하고 싶으면 추가할 예정
    // @Column(name = "relevance_score", nullable = true)
    // private Double relevanceScore;

    public void like() {
        this.isLiked = true;
    }

    public void unlike() {
        this.isLiked = false;
    }

}
