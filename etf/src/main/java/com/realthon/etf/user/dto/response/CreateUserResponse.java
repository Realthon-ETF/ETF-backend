package com.realthon.etf.user.dto.response;

import com.realthon.etf.user.domain.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;

@Getter
@Builder
public class CreateUserResponse {

    private Long userId;
    private String loginId;
    private String username;
    private String phoneNumber;
    private String email;
    private String school;
    private String major;
    private String interestField;
    private Long intervalDays;
    private LocalTime alarmTime;

    public static CreateUserResponse from(User user) {
        return CreateUserResponse.builder()
                .userId(user.getUserId())
                .loginId(user.getLoginId())
                .username(user.getUsername())
                .phoneNumber(user.getPhoneNumber())
                .email(user.getEmail())
                .school(user.getSchool())
                .major(user.getMajor())
                .interestField(user.getInterestField())
                .intervalDays(user.getIntervalDays())
                .alarmTime(user.getAlarmTime())
                .build();
    }
}
