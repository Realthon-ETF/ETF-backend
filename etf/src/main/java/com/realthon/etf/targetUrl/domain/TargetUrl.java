package com.realthon.etf.targetUrl.domain;

import com.realthon.etf.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "target_urls")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class TargetUrl {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long targetUrlId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "target_url", nullable = false, length = 1000)
    private String targetUrl;

    @Builder
    public TargetUrl(User user, String targetUrl) {
        this.user = user;
        this.targetUrl = targetUrl;
    }

    public void updateUrl(String newUrl) {
        this.targetUrl = newUrl;
    }
}
