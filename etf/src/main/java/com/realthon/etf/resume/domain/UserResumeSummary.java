package com.realthon.etf.resume.domain;

import com.realthon.etf.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_resume_summary")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserResumeSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 각 유저당 하나씩
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Builder
    public UserResumeSummary(User user, String summary) {
        this.user = user;
        this.summary = summary;
    }

    public void updateUserResumeSummary(String summary) {
        this.summary = summary;
    }
}
