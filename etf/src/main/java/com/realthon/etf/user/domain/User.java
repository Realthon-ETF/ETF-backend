package com.realthon.etf.user.domain;

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

    @Column(nullable = true, length = 100)
    private String interestField;

    @Column(nullable = false)
    private Long intervalDays;  // 원하는 알람 주기 (일 단위, ex. 1일, 3일, 7일)

    @Column(nullable = false)
    private LocalTime alarmTime;        // 알람 시간 (24시간 기준, ex. 09:00, 21:30)

    @Builder
    public User(String loginId, String password, String username, String phoneNumber, String email,
                String school, String major, String interestField, Long intervalDays, LocalTime alarmTime) {
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
}

