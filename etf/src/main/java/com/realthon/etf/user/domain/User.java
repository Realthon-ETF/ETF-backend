package com.realthon.etf.user.domain;

import com.realthon.etf.resume.domain.UserResumeSummary;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.LocalTime;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false, unique = true)
    private String loginId;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 30)
    private String username;

    @Column(nullable = false)
    @Pattern(regexp = "^[0-9\\-]{9,15}$", message = "전화번호 형식이 올바르지 않습니다.")
    private String phoneNumber;

    @Email
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 50)
    private String school;

    @Column(nullable = false, length = 50)
    private String major;

    @Enumerated(EnumType.STRING)
    @Column(name = "interest_field", nullable = false)
    private InterestField interestField;

    @Column(nullable = true)
    private Long intervalDays = 2L;

    @Column(nullable = true)
    private LocalTime alarmTime = LocalTime.of(9, 0); // 알람 시간 (24시간 기준, ex. 09:00, 21:30)

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private UserResumeSummary resumeSummary;


    @PrePersist
    @PreUpdate
    private void applyDefaultAlarmSetting() {
        if (intervalDays == null) intervalDays = 2L;
        if (alarmTime == null) alarmTime = LocalTime.of(9, 0);
    }

    @Builder
    public User(String loginId, String password, String username, String phoneNumber, String email,
                String school, String major, InterestField interestField, Long intervalDays, LocalTime alarmTime) {
        this.loginId = loginId;
        this.password = password;
        this.username = username;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.school = school;
        this.major = major;
        this.interestField = interestField;
        this.intervalDays = intervalDays;
        this.alarmTime = alarmTime;
    }

    public void updateProfile(String username,
                              String phoneNumber,
                              String email,
                              String school,
                              String major,
                              InterestField interestField,
                              Long intervalDays,
                              LocalTime alarmTime) {

        if (username != null)      this.username = username;
        if (phoneNumber != null)   this.phoneNumber = phoneNumber;
        if (email != null)         this.email = email;
        if (school != null)        this.school = school;
        if (major != null)         this.major = major;
        if (interestField != null) this.interestField = interestField;
        if (intervalDays != null)  this.intervalDays = intervalDays;
        if (alarmTime != null)     this.alarmTime = alarmTime;
    }

}

